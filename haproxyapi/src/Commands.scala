package haproxyapi
import haproxyapi.models._
import haproxyapi.conversions._
import cats._
import cats.data.{Kleisli, EitherT}
import cats.effect.IO

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
  def rawResponse(cmd: String): Either[HAProxyError, List[Map[String,String]]] = for {
    req <- HAProxySocket.socketRequest(config.host, config.port, cmd)
    resp <- HAProxySocket.socketResponse(req)

    } yield resp

  def mappedResponse(cmd: String) = for {
    resp <- rawResponse(cmd)
    lar <- Right(resp.collect { case m:Map[String,Any] => ParseCaseClass.to[models.Backend].from(m) }.flatten)
  } yield lar

  def getBackend(backend: String) = mappedResponse(s"show servers conn ${backend}")

  // req <- HAProxySocket.socketRequest(config.host, config.port, cmd)
}
object Commands {
  def apply(config: Config) = {
    new Commands(config)
  }

}
