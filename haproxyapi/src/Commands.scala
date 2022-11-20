package haproxyapi
import haproxyapi.models._
import haproxyapi.conversions._

trait Config {
  val host: String
  val port: Int
}
object LocalConfig extends Config {
  val host = "localhost"
  val port = 9999
}


class Commands(config: Config) {
  def writeSocket(cmd: String) = HAProxySocket.socketRequest(config.host, config.port, cmd)
  def getHAProxyResponse(cmd: String): Either[HAProxyError, List[models.Backend]] = {
    HAProxySocket.socketResponse(writeSocket(cmd)) match {
      case Left(e: HAProxyError) => Left(e)
      case Right(lom: List[Map[String,Any]]) => Right(lom.collect { case m:Map[String,Any] => ParseCaseClass.to[models.Backend].from(m) }.flatten)
    }
  }

  def getResponse(cmd: String): Either[HAProxyError, List[Map[String,String]]] = {
    val resp = HAProxySocket.socketRequest(config.host, config.port, cmd)
    HAProxySocket.socketResponse(resp)
  }
  def disableServer(backend: String, server: String) = {
    getResponse(s"disable server ${backend}/${server}")
  }
  def getBackend(backend: String) = getHAProxyResponse(s"show servers conn ${backend}")
  def enableServer(backend: String, server: String) = getResponse(s"enable server ${backend}/${server}")
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
