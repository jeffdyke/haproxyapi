package haproxyapi
import haproxyapi.syntax.string._
import scala.io.Source
import scala.jdk.CollectionConverters._
import java.io.InputStream
import com.github.tototoshi.csv._
import io.circe.syntax._

case class BackendResponse(
  bkname: String,
  svname: String,
  bkid: Int,
  svid: Int,
  addr: String,
  port: Int,
  purgeDelay: Int,
  usedCur: Int,
  usedMax: Int,
  needEst: Int,
  unsafeNb: Int,
  safeNb: Int,
  idleLim: Int,
  idleCur: Int,
  idlePerThr: Int
)
object Runner {
  def main(args: Array[String] = Array[String]()): Unit =  {
    val result = HAProxySocket.socketRequest("localhost", 9999, "show servers conn")
    val headers = result.asScala.head.split(" ").filter(x => x != "#" && x != "-")
                                 .map(_.replace("/",", ").replaceAll("\\[[0-9]+\\]","").underscoreToCamelCase).toList // +:
    val values = result.asScala.toList.drop(1).foldLeft(List[List[String]]())((acc, str) => acc.appended(str.replace("/", " ").split(" ").filter(_ != "-").toList))
    // I started with csv, this is super shitty and it will be fixed
    // TODO: also add encoding of the case class after clean up
    val csv = CSVReader.open(Source.fromString(headers.mkString(",") + "\n" ++ values.map(_.mkString(",")).mkString("\n")))

    println(csv.allWithHeaders().asJson)
    ()

  }
}