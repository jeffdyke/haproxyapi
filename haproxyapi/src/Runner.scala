package haproxyapi
import pprint._

object Runner {


  def main(args: Array[String] = Array[String]()) = {
      val resp = Commands(LocalConfig).getBackend("web_app1_h1")

      pprint.pprintln(resp)
  }
}