package haproxyapi.models
import io.circe.syntax._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

object models {
  trait HAProxyResponse
  case class Backend(
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
  ) extends HAProxyResponse

  case class Backends(name: String) extends HAProxyResponse

  case class BackendState(
    srvCheckHealth: Int,
    beName: String,
    srvIweight: Int,
    beId: Int,
    srvUweight: Int,
    srvFForcedId: Int,
    srvOpState: Int,
    srvAdminState: Int,
    srvCheckState: Int,
    srvAgentState: Int,
    srvCheckPort: Int,
    srvrecord: Int,
    srvId: Int,
    bkFForcedId: Int,
    srvAddr: String,
    srvUseSsl: Int,
    srvFqdn: String,
    srvName: String,
    srvCheckResult: Int,
    srvPort: Int,
    srvTimeSinceLastChange: Int,
    srvCheckStatus: Int
  ) extends HAProxyResponse

  case class HAProxyNoResult(empty: Option[String]) extends HAProxyResponse
}
