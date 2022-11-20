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

trait HAProxyError {
  val error: String
}



object BackendMatchError extends HAProxyError {
  val error = "Can't find backend"
}
object NoSuchServer extends HAProxyError {
  val error = "No such server"
}
object BadCommand extends HAProxyError {
  val error = "Unknown command"
}

object HAProxySocket {
  val returnVal = "^[0-9]+$".r

  def validateBadCommand(r: List[String]): Either[HAProxyError, List[String]] = r.head match{
    case e:String if e.startsWith(NoSuchServer.error) => Left(NoSuchServer)
    case e:String if e.startsWith(BackendMatchError.error) => Left(BackendMatchError)
    case e:String if e.startsWith(BadCommand.error) => Left(BackendMatchError)
    case m:String if (m.trim().isEmpty || m.startsWith("#")) => Right(r)
  }

  def socket(hostname: String, port: Int): Resource[IO, Socket] =
    Resource.fromAutoCloseable(IO.pure(new Socket(InetAddress.getByName(hostname), port)))


  def outputStream(socket: Socket): Resource[IO, PrintWriter] =
    Resource.fromAutoCloseable(IO.blocking(new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))))

  def inputStream(socket: Socket): Resource[IO, InputStreamReader] =
    Resource.fromAutoCloseable(IO.blocking(new InputStreamReader(socket.getInputStream)))


  def socketRequest(host: String, port: Int, cmd: String): List[String]  =
    (
      for {
        sock <- socket(host, port)
        out <- outputStream(sock)
        in <- inputStream(sock)
      } yield (sock, out, in)
    ).use { case (s, outStream, inStream) =>
      for {
        _ <- IO.blocking(outStream.println(cmd))
        _ <- IO.pure(outStream.flush())
        in <- IO.blocking(new BufferedReader(inStream))
        // Some return values include the number of elements, we don't want that.
        output  = in.lines().toList.asScala.filter(line => returnVal.pattern.matcher(line).matches == false || line.isEmpty())
      } yield output.toList
    }.unsafeRunSync()

  def socketResponse(result: List[String]): Either[HAProxyError, List[Map[String, String]]] = {
    validateBadCommand(result) match {
      case Left(e:HAProxyError) => Left(e)
      case Right(a) => Right({
        val headers = a.head.split(" ").filter(x => x != "#" && x != "-")
                                .map(_.replace("/",",").replaceAll("\\[[0-9]+\\]","")
                                .underscoreToCamelCase).toList
        val values = a.drop(1).foldLeft(List[List[String]]())((acc, str) =>
                            acc.appended(str.replace("/", " ").split(" ").filter(_ != "-").toList))

        CSVReader.open(Source.fromString(headers.mkString(",") + "\n" ++ values.map(_.mkString(",")).mkString("\n").trim())).allWithHeaders()

      })
    }
  }


    // These are in the wrong order
    //val mapped = values.map(values => headers.zip(values).toMap)
    // I started with csv, this is super shitty and it will be fixed
}
  // def toModel(in: List[Map[String, String]]) = ???
