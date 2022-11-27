package haproxyapi
import pprint._

object Runner {


  def main(args: Array[String] = Array[String]()) = {
      val cmd = Commands(LocalConfig)
      pprint.pprintln(cmd.rawCommand("show servers conn web_app1_h1") match {
        case Left(l) => l.error
        case Right(r) => r
      })

      // val resp = Commands(LocalConfig).enableBackend("web_app1_h1", "web_app1_h1")

      // pprint.pprintln(resp)
  }
}