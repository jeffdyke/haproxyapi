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
    srvCheckStatus: String
  ) extends HAProxyResponse

}
