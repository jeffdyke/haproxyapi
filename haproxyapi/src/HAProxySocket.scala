package haproxyapi
import cats._
import cats.effect.{IO, Resource}
import cats.implicits._
import cats.effect.unsafe.implicits.global
import scala.jdk.CollectionConverters._
import cats.syntax.either._
import scala.util.matching.Regex
import haproxyapi.syntax.string._
import scala.io.Source
import java.io._
import java.net.{InetAddress, Socket, ConnectException}
import pprint._

trait HAProxyError {
  val error: String
  val th: Option[Throwable]
}

object BackendMatchError extends HAProxyError {
  val error = "Can't find backend"
  val th = None
}
object NoSuchServer extends HAProxyError {
  val error = "No such server"
  val th = None
}
object BadCommand extends HAProxyError {
  val error = "Unknown command"
  val th = None
}
case class SocketError(error: String, th: Option[Throwable]) extends HAProxyError
case class ResourceError(error: String, th: Option[Throwable]) extends HAProxyError

object HAProxySocket {
  val returnVal = "^-?[0-9]+$".r

  def validateBadCommand(r: List[String]): Either[HAProxyError, List[String]] = r.head match{
    case e:String if e.startsWith(NoSuchServer.error) => Left(NoSuchServer)
    case e:String if e.startsWith(BackendMatchError.error) => Left(BackendMatchError)
    case e:String if e.startsWith(BadCommand.error) => Left(BackendMatchError)
    case m:String if (m.trim().isEmpty || m.startsWith("#")) => Right(r)
  }

  // defer will catch exceptions
  def socket(hostname: String, port: Int): Resource[IO, Socket] =
    Resource.fromAutoCloseable(IO.defer(IO.pure(new Socket(InetAddress.getByName(hostname), port))))

  def outputStream(socket: Socket): Resource[IO, PrintWriter] =
    Resource.fromAutoCloseable(IO.blocking(new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))))

  def inputStream(socket: Socket): Resource[IO, InputStreamReader] =
    Resource.fromAutoCloseable(IO.blocking(new InputStreamReader(socket.getInputStream)))

  def socketRequest(host: String, port: Int, cmd: String): Either[HAProxyError, List[String]]  =
    (
      for {
        sock <- socket(host, port)
        out <- outputStream(sock)
        in <- inputStream(sock)
      } yield (sock, out, in)
    ).use { case (s, outStream, inStream) =>
      for {
        _ <- IO.defer(IO.pure(outStream.println(cmd)))
        _ <- IO.defer(IO.pure(outStream.flush()))
        in <- IO.defer(IO.pure(new BufferedReader(inStream)))
        // Some return values include the number a number at the top of the output, remove that
        output  = in.lines().toList.asScala.filter(line => returnVal.pattern.matcher(line).matches == false || line.isEmpty())
      } yield Right(output.toList)
    }.handleErrorWith(th => (th match {
        case c: ConnectException => IO(Left(new SocketError(c.getMessage(), Some(c))))
        case ioe: IOException => IO(Left(new SocketError(s"IOError: ${ioe.getMessage()}", Some(ioe))))
        case t: Throwable => IO(Left(new ResourceError(s"Catch all: ${t.getMessage()}", Some(t))))
      })).unsafeRunSync()

  def socketResponse(result: List[String]): Either[HAProxyError, List[Map[String, Any]]] = {
    validateBadCommand(result) match {
      case Left(e:HAProxyError) => Left(e)
      //enable/disable don't return anything
      case Right(e) if e.isEmpty => Right(List[Map[String,String]]())
      case Right(a) => Right({

        // This takes the haproxy output, trims it down to the lenght of the headers and creates and ordered map (effectively a csv)
        val headers = a.head.replace("/"," ").split(" ").filter(x => x != "#" && x != "-")
                                .map(_.replaceAll("\\[[0-9]+\\]","").underscoreToCamelCase).toList

        // There are only two types String and Ints, so just hardcoding conversion
        val values = a.slice(1,a.length -1).foldLeft(List[List[Any]]())((acc, str) =>
                            acc.appended(str.replace("/", " ").split(" ").filter(_ != "-").toList.map(m => m match {
                              case si: String if returnVal.pattern.matcher(si).matches => si.toInt
                              case s: String => s
                            })))
        val mapped = values.filter(_.nonEmpty)
          .map(_.zipWithIndex)
          .map(value => value.slice(0, headers.length))
          .map(_.map(x => (headers(x._2).trim() -> x._1)).toMap)
        mapped

      })
    }
  }
}
