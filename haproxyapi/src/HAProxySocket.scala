package haproxyapi

import cats.effect.{IO, Resource}
import java.io._
import java.net.{InetAddress, Socket}
import cats.effect.unsafe.implicits.global
import scala.jdk.CollectionConverters._
import cats.syntax.either._
import scala.util.matching.Regex
import com.github.tototoshi.csv._
import haproxyapi.syntax.string._
import scala.io.Source
import io.circe.{Decoder, Encoder}

object HAProxySocket {

  def socket(hostname: String, port: Int): Resource[IO, Socket] =
    Resource.fromAutoCloseable(IO.pure(new Socket(InetAddress.getByName(hostname), port)))

  def outputStream(socket: Socket): Resource[IO, PrintWriter] =
    Resource.fromAutoCloseable(IO.blocking(new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))))

  def inputStream(socket: Socket): Resource[IO, InputStreamReader] =
    Resource.fromAutoCloseable(IO.blocking(new InputStreamReader(socket.getInputStream)))

  val returnVal = "^[0-9]+$".r

  def socketRequest(host: String, port: Int, cmd: String)  =
    (
      for {
        sock <- socket(host, port)
        out <- outputStream(sock)
        in <- inputStream(sock)
      } yield (sock, out, in)
    ).use { case (s, outStream, inStream) =>
      for {
        _ <- IO.pure(outStream.println(cmd))
        _ <- IO.pure(outStream.flush())
        in <- IO.blocking(new BufferedReader(inStream))
        // Some return values include the number of elements, we don't want that.
        output  = in.lines().toList.asScala.filterNot(line => returnVal.pattern.matcher(line).matches)
      } yield output.toList
    }.unsafeRunSync()

  def socketResponse(result: List[String]): List[Map[String, String]] =  {
    val headers = result.head.split(" ").filter(x => x != "#" && x != "-")
                                 .map(_.replace("/",",").replaceAll("\\[[0-9]+\\]","")
                                 .underscoreToCamelCase).toList
    val values = result.drop(1).foldLeft(List[List[String]]())((acc, str) =>
      acc.appended(str.replace("/", " ").split(" ").filter(_ != "-").toList)
    )

    // These are in the wrong order
    //val mapped = values.map(values => headers.zip(values).toMap)


    // values.zipWithIndex.foldLeft(List[Map[String,String]]())((acc, vi) => acc += (headers(vi._2) -> vi._1))
    // I started with csv, this is super shitty and it will be fixed
    // TODO: also add encoding of the case class after clean up
    val csv = CSVReader.open(Source.fromString(headers.mkString(",") + "\n" ++ values.map(_.mkString(",")).mkString("\n")))

    csv.allWithHeaders()
  }
  def toModel(in: List[Map[String, String]]) = ???
}