package haproxyapi

import cats.effect.{IO, Resource}
import java.io._
import java.net.{InetAddress, Socket}
import cats.effect.unsafe.implicits.global
import scala.collection.immutable._
import scala.jdk.CollectionConverters._

object HAProxySocket {
  def socket(hostname: String, port: Int): Resource[IO, Socket] =
    Resource.fromAutoCloseable(IO.pure(new Socket(InetAddress.getByName(hostname), port)))

  def outputStream(socket: Socket): Resource[IO, PrintWriter] =
    Resource.fromAutoCloseable(IO.blocking(new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())))))

  def inputStream(socket: Socket): Resource[IO, InputStreamReader] =
    Resource.fromAutoCloseable(IO.blocking(new InputStreamReader(socket.getInputStream)))

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
        output  = in.lines().toList()
      } yield output
    }.unsafeRunSync()
}