// package haproxyapi

// import fs2.Stream
// import io.circe.Json
// import cats.effect._
// import cats.implicits._
// import org.http4s.HttpRoutes
// import org.http4s.syntax._
// import org.http4s.dsl.io._
// import org.http4s.implicits._
// import org.http4s.server.blaze._
// import scala.concurrent.duration._
// import haproxyapi.HaProxySocketClient
// import org.http4s.server.Router
// import scala.concurrent.ExecutionContext.global
// import org.http4s.headers.`Content-Type`
// import org.slf4j.Logger
// import org.slf4j.LoggerFactory
// import org.http4s.MediaType
// // object BackendQueryParamMatcher extends QueryParamDecoderMatcher[String]("backend")
// sealed trait Resp
// case class Servers(body: String)

// //class HaProxyConfigService[F[_]](implicit ev: Async[F]) extends Http4sDsl[F] {
// object HaProxyConfigService extends IOApp {
//   val logger = LoggerFactory.getLogger(getClass())
//   // import AppContextShift._


//   val haProxySerivce = HttpRoutes.of[IO] {

//     case GET -> Root / "servers" => {

//       Ok(HaProxySocketClient.readServers().toString()).map(_.withContentType(`Content-Type`(MediaType.`text/event-stream`)))
//     }
//     // case _ -> Root =>
//     //   // The default route result is NotFound. Sometimes MethodNotAllowed is more appropriate.
//     //   MethodNotAllowed(Allow(GET))

//   }.orNotFound


//   override def run(args: List[String]): IO[ExitCode] = {

//     BlazeServerBuilder[IO](global)
//       .bindHttp(8080, "localhost")
//       .withHttpApp(haProxySerivce)
//       .resource
//       .use(_ => IO.never)
//       .as(ExitCode.Success)

//   }
// }
