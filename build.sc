import mill._, scalalib._
import $ivy.`com.lihaoyi::mill-contrib-bloop:0.10.8`

object haproxyapi extends ScalaModule {
  val Http4sVersion = "1.0.0-M34"
  val CirceVersion = "0.14.1"
  def scalaVersion = "2.13.9"
  def ivyDeps = Agg(
    ivy"org.typelevel::cats-effect:3.4.0",
    ivy"org.http4s::http4s-dsl:0.23.14",
    ivy"io.circe::circe-generic:0.14.3",
    ivy"io.circe::circe-parser:0.14.3",
    // ivy"org.slf4j:slf4j-api:2.0.4",
    // ivy"org.slf4j:slf4j-simple:2.0.4",
    ivy"com.lihaoyi::pprint:0.7.0",
    ivy"com.chuusai::shapeless:2.3.9",
    ivy"ch.qos.logback:logback-classic:1.3.5",
    ivy"com.typesafe.scala-logging::scala-logging:3.9.4"
  )

  def mainClass = Some("haproxyapi.Runner")
}
