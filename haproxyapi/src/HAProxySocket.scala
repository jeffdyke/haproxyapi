package haproxyapi

import cats.effect.{IO, Resource}
import java.io._
import java.net.{InetAddress, Socket}
import cats.effect.unsafe.implicits.global

object HAProxySocket {
  def socket(hostname: String, port: Int): Resource[IO, Socket] =
    Resource.make {
      IO.pure(new Socket(InetAddress.getByName(hostname), port))
    } { connection =>

      IO.blocking(connection.close()).handleErrorWith(e => IO.println(e))
    }

  def outputStream(socket: Socket): Resource[IO, PrintWriter] =
    Resource.make {
      IO.blocking(new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))))
    } { outString =>
      IO.blocking(outString.close()).handleErrorWith(_ => IO.unit)
    }
  def inputStream(socket: Socket): Resource[IO, InputStreamReader] =
    Resource.make {
      IO.blocking(new InputStreamReader(socket.getInputStream))
    } { in =>
      IO.blocking(in.close()).handleErrorWith(_ => IO.unit)
    }
  // TODO: may have/want to pass the socket to use to ensure its closed
  def socketRequest(cmd: String) =
    (
      for {
        sock <- socket("localhost", 9999)
        out <- outputStream(sock)
        in <- inputStream(sock)
      } yield (out, in)
    ).use { case (outStream, inStream) =>
      for {
        _ <- IO.pure(outStream.println(cmd))
        _ <- IO.pure(outStream.flush())
        in <- IO.blocking(new BufferedReader(inStream))
        output <- IO.blocking(in.lines().toList())
      } yield output

    }.unsafeRunSync()
}
