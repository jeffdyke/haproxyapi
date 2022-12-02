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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

  val logger = LoggerFactory.getLogger("HAProxyCommands")
  def sleeper = Right({logger.info("Yawn...sleeping"); Thread.sleep(1000); logger.info("I'm awake")})
  def rawResponse(cmd: String): Either[HAProxyError, List[Map[String,Any]]] = for {
    req <- HAProxySocket.socketRequest(config.host, config.port, cmd)
    resp <- HAProxySocket.socketResponse(req)
  } yield resp

  def backendDetails(cmd: String): Either[HAProxyError, List[models.Backend]] = for {
    resp <- rawResponse(cmd)
    lar = resp.collect { case m: Map[String, Any] => ParseCaseClass.to[models.Backend].from(m)}.flatten
  } yield lar

  def backendState(cmd: String): Either[HAProxyError, List[models.BackendState]] = for {
    resp <- rawResponse(cmd)
    lar = resp.collect { case m: Map[String, Any] => ParseCaseClass.to[models.BackendState].from(m)}.flatten
  } yield lar

  def emptyResponse(cmd: String): Either[HAProxyError, models.HAProxyNoResult] = for {
    resp <- rawResponse(cmd)
  } yield new models.HAProxyNoResult(Some(s"No Result for ${cmd} (normally positive)"))

  def listBackends: Either[HAProxyError, List[models.Backends]] = for {
    raw <- rawResponse("show backend")
    resp = raw.collect {m: Map[String, Any] => ParseCaseClass.to[models.Backends].from(m)}.flatten
  } yield resp

  def getBackend(backend: String) = backendDetails(s"show servers conn ${backend}")
  def getBackendState(backend: String) = backendState(s"show servers state ${backend}")

  def disableBackend(backend: String, server: String) = emptyResponse(s"disable server ${backend}/${server}")
  def enableBackend(backend: String, server: String) = emptyResponse(s"enable server ${backend}/${server}")

  def restartWith(backend: String, server: String, f: () => Either[HAProxyError, Unit]) = for {
    be <- disableBackend(backend, server)
    _ = logger.info(s"Disabled ${backend}/${server}")
    be <- enableBackend(backend, server)
    _ <- f()
    _ = logger.info(s"Enabled ${backend}/${server} after function")
    backendS <- getBackend(backend)
  } yield backendS

  def restart(backend: String, server: String) = for {
    be <- disableBackend(backend, server)
    _ = logger.info(s"Disabled ${backend}/${server}")
    _ <- sleeper
    be <- enableBackend(backend, server)
    _ = logger.info(s"Enabled ${backend}/${server} after sleeper")
    backendS <- getBackend(backend)
  } yield backendS
}

object Commands {
  def apply(config: Config) = {
    new Commands(config)
  }
}
