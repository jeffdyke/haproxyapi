package haproxyapi

//import com.lihaoyi.pprint._



object Runner {

  // implicit val backendsDecoder: Encoder[Backends] = deriveEncoder[Backends]
  // implicit val jsonDecoder: Encoder[Backend] = deriveEncoder[Backend]

  def main(args: Array[String] = Array[String]()) = {
    println(Commands(LocalConfig).getBackend("web_app1_h1") )
    // val request = HAProxySocket.socketRequest("localhost", 9999, "show servers state web_app1_h1")
    // val formatted = HAProxySocket.socketResponse(request)
    // println(formatted)

  }
}