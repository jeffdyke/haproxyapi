package haproxyapi

object Runner {
  def main(args: Array[String] = Array[String]()): Unit = HaProxySocketClient.readServers()
}