package haproxyapi
import haproxyapi.models._
import haproxyapi.conversions._
import cats._
import cats.data.{Kleisli, EitherT}
import cats.effect.IO
import shapeless._
import io.circe._
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.generic.semiauto._


object results {
  type Result[A] = Either[HAProxyError, A]
  type ResultT[A] = EitherT[IO, HAProxyError, A]
  implicit class ResultOps[A](result: Result[A]) {
    def toResultT: ResultT[A] = EitherT.fromEither[IO](result)
  }

}

trait Config {
  val host: String
  val port: Int
}
object LocalConfig extends Config {
  val host = "localhost"
  val port = 9999
}


class Commands(config: Config) {
  val intRe = "(-?[0-9]+)".r


  implicit val encodeIntOrString: Encoder[Either[Int, String]] =
    Encoder.instance(_.fold(_.asJson, _.asJson))

  implicit val decodeIntOrString: Decoder[Either[Int, String]] =
    Decoder[Int].map(Left(_)).or(Decoder[String].map(Right(_)))


  implicit val lgenBackend = LabelledGeneric[models.Backend]
  implicit val lgenBackendState = LabelledGeneric[models.BackendState]

  def rawResponse(cmd: String): Either[HAProxyError, List[Map[String,Any]]] = for {
    req <- HAProxySocket.socketRequest(config.host, config.port, cmd)
    resp <- HAProxySocket.socketResponse(req)
  } yield resp

  def strOrInt(s: String): Either[String, Int] = s match {
    case intRe(s) => Right(s.toInt)
    case s => Left(s)
  }

  def bankendDetails(cmd: String): Either[HAProxyError, IO[List[models.Backend]]] = for {
    resp <- rawResponse(cmd)
    // parsable = resp.foldLeft(Map[String, Either[String, Int]]())((acc, mofa) =>
    //   acc + (mofa.toMap.keys.head -> strOrInt(mofa.toMap.values.head.toString())))
    //lar = parsable.map(_.asJson)
    //_ = pprint.pprintln(lar.toString)
    lar = resp.collect { case m: Map[String, Any] => ParseCaseClass.to[models.Backend].from(m)}.flatten
    //decoded = lar.map(i => decode[models.Backend](i.toString()))
  } yield IO.pure(lar)

  def backendState(cmd: String): Either[HAProxyError, IO[List[models.BackendState]]] = for {
    resp <- rawResponse(cmd)
    _ = pprint.pprintln(resp)
    lar = resp.collect { case m: Map[String, Any] => ParseCaseClass.to[models.BackendState].from(m)}.flatten
    //decoded = lar.map(i => decode[models.Backend](i.toString()))
  } yield IO.pure(lar)

  def emptyResponse(cmd: String): Either[HAProxyError, IO[models.HAProxyNoResult]] = for {
    resp <- rawResponse(cmd)
  } yield IO.pure(new models.HAProxyNoResult(Some(s"No Result for ${cmd}")))

  def getBackend(backend: String) = bankendDetails(s"show servers conn ${backend}")
  def getBackendState(backend: String) = backendState(s"show servers state ${backend}")
  def disableBackend(backend: String, server: String) = emptyResponse(s"disable server ${backend}/${server}")
  def enableBackend(backend: String, server: String) = emptyResponse(s"enable server ${backend}/${server}")

  def listBackends: Either[HAProxyError, IO[List[models.Backends]]] = for {
    raw <- rawResponse("show backend")
    resp = raw.collect {m: Map[String, Any] => ParseCaseClass.to[models.Backends].from(m)}.flatten
  } yield IO.pure(resp)
}

object Commands {
  def apply(config: Config) = {
    new Commands(config)
  }

}
