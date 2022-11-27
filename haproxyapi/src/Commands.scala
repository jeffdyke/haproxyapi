package haproxyapi
import haproxyapi.models._
import haproxyapi.conversions._
import cats._
import cats.data.{Kleisli, EitherT}
import cats.effect.IO
import shapeless._
import io.circe.syntax._
// import io.circe.generic.semiauto._

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
  import io.circe._, io.circe.generic.auto._, io.circe.syntax._

  implicit val encodeIntOrString: Encoder[Either[Int, String]] =
    Encoder.instance(_.fold(_.asJson, _.asJson))

  implicit val decodeIntOrString: Decoder[Either[Int, String]] =
    Decoder[Int].map(Left(_)).or(Decoder[String].map(Right(_)))

  def rawResponse(cmd: String): Either[HAProxyError, List[Map[String,Either[String, Int]]]] = for {
    req <- HAProxySocket.socketRequest(config.host, config.port, cmd)
    resp <- HAProxySocket.socketResponse(req)

    } yield resp

  def mappedResponse(cmd: String): Either[HAProxyError, List[io.circe.Json]] = for {
    resp <- rawResponse(cmd)
    lar = resp.map(_.asJson)
  } yield lar

  def getBackend(backend: String) = mappedResponse(s"show servers conn ${backend}")
  def disableBackend(backend: String, server: String) = mappedResponse(s"disable server ${backend}/${server}")
  def enableBackend(backend: String, server: String) = mappedResponse(s"enable server ${backend}/${server}")
  def rawCommand(cmd: String) = mappedResponse(cmd)
  // req <- HAProxySocket.socketRequest(config.host, config.port, cmd)
}
object Commands {
  def apply(config: Config) = {
    new Commands(config)
  }

}
