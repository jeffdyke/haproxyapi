package haproxyapi

object Runner {
  def main(args: Array[String] = Array[String]()): Unit =  {
    val result = HAProxySocket.socketRequest("show servers conn")
    println(s"post result ${result}")
  }
}