package haproxyapi
import java.net.Socket
import java.io._
import java.io.FileInputStream
// import cats.implicits.IO
import com.github.tototoshi.csv._
import scala.io.Source
// Type HAProxyCmd = String

trait SocketClient {
  val socat: String
  val socket: String
}

trait HAProxyHdrs {
  val headers: Map[String, String]
}
// List(HashMap(srv_addr -> 10.1.1.198, srvrecord -> -, srv_check_health -> 3, srv_op_state -> 2, srv_check_addr -> -, srv_uweight -> 1,
//   bk_f_forced_id -> 0, srv_check_result -> 3, srv_id -> 1, srv_port -> 80, srv_check_status -> 6, srv_f_forced_id -> 0, srv_check_port -> 0,
//   be_name -> http1-nodes, be_id -> 11, srv_time_since_last_change -> 621559, srv_check_state -> 6, srv_agent_port -> 0, srv_fqdn -> -,
//   srv_agent_state -> 0, srv_use_ssl -> 0, srv_name -> stagingweb01, srv_iweight -> 1, srv_admin_state -> 0, srv_agent_addr -> -)

case class ServerResponse(
  address: String,
  operationalState: Int,
  port: Int,
  backend: String,
  server: String,
  checkStatus: Int,
  checkResult: Int

)

trait ServerHeaders extends HAProxyHdrs {
    val headers = Map("srv_addr" -> "address", "srv_op_state" -> "operationalState", "srv_port" -> "port", "be_name" -> "backend",
    "srv_name" -> "server", "srv_check_status" -> "checkStatus", "srv_check_result" -> "checkResult")
}
object SpaceFormat {
  implicit object SpaceFormat extends DefaultCSVFormat {
    override val delimiter = '#'
  }
}

// implicit class StringImp {
//   implicit def underToCamel(s: String) {
//     val s1 = "_([a-z\\d])".r.replaceAllIn(s.stripPrefix("_"), {m =>
//           m.group(1).toUpperCase()
//     })
//   }

// }

object HaProxySocketClient extends SocketClient {
  val localFile = "/Users/jeff/server_response.csv"
  val socat = "/usr/bin/socat stdio "
  val socket = "/var/run/haproxy/admin.sock"


  def cmdBuild(in: String): String = {
    s"""echo "$in" | ${socat} ${socket} """
  }

  def servers(backend: String): String = {
    cmdBuild(s"show servers ${backend}")
  }

  def readServers(): List[Map[String, String]] = {
    // will be haproxy call, io will become more involved
    val rawr = Source.fromFile(localFile)
    val commaCsv = Source.fromString(rawr.getLines().map(_.replaceAll(" ", ",")).toString())

    val reader = CSVReader.open(commaCsv)
    val m = List(Map("one" -> 1, "two" -> 2, "three" -> 3, "four" -> 4, "five" -> 5, "six" -> 6))
    val remapped = m.foldLeft(Map[String,Int]())( (map, value) => map +: {value.head._1.toUpperCase -> value.head._2})
    println(remapped)
    // println(upperM)
    // println(reader)
    // val csvMap = reader.allWithHeaders().foldLeft(Map[String, String]) { case (map,item)
    //   (k,v) => ServerHeaders.headers[k]
    //}
    List[Map[String, String]]()
  }

}
