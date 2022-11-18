package haproxyapi
import haproxyapi.syntax.string._
import scala.io.Source
import scala.jdk.CollectionConverters._
import java.io.InputStream
import haproxyapi.models.models._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import io.circe.generic._
import scala.reflect._
import scala.reflect.runtime.universe._

object ccFromMap {

  def fromMap[T: TypeTag: ClassTag](m: Map[String, String]) = {
    val rm = runtimeMirror(classTag[T].runtimeClass.getClassLoader)
    val classTest = typeOf[T].typeSymbol.asClass
    val classMirror = rm.reflectClass(classTest)
    val constructor = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val constructorMirror = classMirror.reflectConstructor(constructor)

    val constructorArgs = constructor.paramLists.flatten.map( (param: Symbol) => {
      val paramName = param.name.toString
      if(param.typeSignature <:< typeOf[Option[String]])
        m.get(paramName)
      else
        m.get(paramName).getOrElse(throw new IllegalArgumentException("Map is missing required parameter named " + paramName))
    })

    constructorMirror(constructorArgs:_*).asInstanceOf[T]
  }
}

object Runner {

  // implicit val backendsDecoder: Encoder[Backends] = deriveEncoder[Backends]
  // implicit val jsonDecoder: Encoder[Backend] = deriveEncoder[Backend]

  def main(args: Array[String] = Array[String]()): Unit =  {
    val result = HAProxySocket.socketRequest("localhost", 9999, "show servers state web_app1_h1")
    val formatted = HAProxySocket.socketResponse(result)
    val loo = formatted.foldLeft(List[BackendState]())((acc, item) => {
      acc ++: List(ccFromMap.fromMap[BackendState](item))
    })
    println(loo)
  }
}