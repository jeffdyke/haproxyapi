package haproxyapi

trait Config {
  val host: String
  val port: Int
}
object LocalConfig extends Config {
  val host = "localhost"
  val port = 9999
}


class Commands(config: Config) {
  def getResponse(cmd: String): Either[HAProxyError, List[Map[String,String]]] = {
    val resp = HAProxySocket.socketRequest(config.host, config.port, cmd)
    HAProxySocket.socketResponse(resp)
  }
  def disableServer(backend: String, server: String) = {
    getResponse(s"disable server ${backend}/${server}")
  }
  def getBackend(backend: String) = getResponse(s"show servers conn ${backend}")
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
