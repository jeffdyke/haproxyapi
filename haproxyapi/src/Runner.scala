package haproxyapi

//import com.lihaoyi.pprint._

object Runner {


  def main(args: Array[String] = Array[String]()) = {
      val resp = Commands(LocalConfig).getBackend("web_app1_h1")
      println(resp)
    ()
  }
}