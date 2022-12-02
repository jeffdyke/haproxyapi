package haproxyapi
import pprint._
import shapeless._

object Runner {


  def main(args: Array[String] = Array[String]()) = {
      val cmd = Commands(LocalConfig)
      // def run =
      //   for {
      //     backend <- cmd.getBackendState("web_app1_h1")
      //     _ = println("I'm here what da fuq")
      //     filter = backend.filter(be => be.srvName != "web_app1_h1")
      //   } yield filter
      // val r = run
      pprint.pprintln(cmd.listBackends)
      ()
      // val resp = Commands(LocalConfig).enableBackend("web_app1_h1", "web_app1_h1")
  }
}