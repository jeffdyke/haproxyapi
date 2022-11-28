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
  // lazy val osName = System.getProperty("os.name") match {
  //   case n if n.startsWith("Linux")   => "linux"
  //   case n if n.startsWith("Mac")     => "mac"
  //   case n if n.startsWith("Windows") => "win"
  //   case _                            => throw new Exception("Unknown platform!")
  // }

  // // Add dependency on JavaFX libraries, OS dependent
  // val javaFXModules = List("base", "controls", "fxml", "graphics", "media", "swing", "web")
  //   .map(m => ivy"org.openjfx:javafx-$m:17.0.1;classifier=$osName")
  // import ch.qos.logback.classic.util.ContextInitializer;
  // System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "/src/haproxy/haproxy/src/logback.xml")
  def mainClass = Some("haproxyapi.Runner")
}
