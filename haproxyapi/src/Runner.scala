package haproxyapi
import haproxyapi.models._
import haproxyapi.conversions._
import shapeless._
import shapeless.record._
//import com.lihaoyi.pprint._

case class Test(one: String, three: Int)

object Runner {

  // implicit val backendsDecoder: Encoder[Backends] = deriveEncoder[Backends]
  // implicit val jsonDecoder: Encoder[Backend] = deriveEncoder[Backend]

  def main(args: Array[String] = Array[String]()) = {
      val resp = Commands(LocalConfig).getBackend("web_app1_h1")

      println(resp)


    // val m = Map("one" -> "two", "three" -> "4")
    // ParseCaseClass.to[Test].from(m)
    // println(Commands(LocalConfig).getBackend("web_app1_h1"))
    // val request = HAProxySocket.socketRequest("localhost", 9999, "show servers state web_app1_h1")
    // val formatted = HAProxySocket.socketResponse(request)
    // println(formatted)
    ()
  }
}