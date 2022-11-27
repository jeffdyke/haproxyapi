package haproxyapi
import pprint._

object Runner {


  def main(args: Array[String] = Array[String]()) = {
      val cmd = Commands(LocalConfig)
      pprint.pprintln(cmd.getBackendState("web_app1_h1"))

      // val resp = Commands(LocalConfig).enableBackend("web_app1_h1", "web_app1_h1")

      // pprint.pprintln(resp)
  }
}