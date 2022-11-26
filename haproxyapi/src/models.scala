package haproxyapi.models
import io.circe.syntax._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

object models {
  trait HAProxyResponse
  case class Backends(backends: List[Backend])
  case class Backend(
    bkname: String,
    svname: String,
    bkid: String,
    svid: String,
    addr: String,
    port: String,
    purgeDelay: String,
    usedCur: String,
    usedMax: String,
    needEst: String,
    unsafeNb: String,
    safeNb: String,
    idleLim: String,
    idleCur: String,
    idlePerThr: String
  ) extends HAProxyResponse

  case class BackendsState(backends: List[BackendState])

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
