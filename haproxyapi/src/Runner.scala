package haproxyapi
import pprint._

object Runner {


  def main(args: Array[String] = Array[String]()) = {
      val cmd = Commands(LocalConfig)
      pprint.pprintln(cmd.restartWith("web_app1_h1","web_app1_h1", Unit => {println("Yawwwwwmmmmmm"); Thread.sleep(5000); println("hey i'm in here")}))

      // val resp = Commands(LocalConfig).enableBackend("web_app1_h1", "web_app1_h1")

      // pprint.pprintln(resp)
  }
}