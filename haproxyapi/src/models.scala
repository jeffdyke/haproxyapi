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

  case class Backends(name: String)

  case class BackendState(
    srvCheckHealth: String,
    beName: String,
    srvIweight: String,
    beId: String,
    srvUweight: String,
    srvFForcedId: String,
    srvOpState: String,
    srvAdminState: String,
    srvCheckState: String,
    srvAgentState: String,
    srvCheckPort: String,
    srvrecord: String,
    srvId: String,
    bkFForcedId: String,
    srvAddr: String,
    srvUseSsl: String,
    srvFqdn: String,
    srvName: String,
    srvCheckResult: String,
    srvPort: String,
    srvTimeSinceLastChange: String,
    srvCheckStatus: String
  ) extends HAProxyResponse

  case class HAProxyNoResult(empty: Option[String]) extends HAProxyResponse
}
