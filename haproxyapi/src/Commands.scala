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
  //   def enableServer(backend: String, server: String) = getResponse(s"enable server ${backend}/${server}")
  def checkServers(backend: String, server: Option[String]) = {

  }
  def checkServer(backend: String, server: String) = checkServers(backend, Some(server))
  // def getBackend(backend: String) = {
  //   getResponse()
  // }
}
object Commands {
  def apply(config: Config) = {
    new Commands(config)
  }

}
