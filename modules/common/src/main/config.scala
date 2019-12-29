package oyun.common

import io.methvin.play.autoconfig._
import play.api.ConfigLoader

object config {

  case class Secret(value: String) extends AnyVal {

    override def toString = "Secret(****)"

  }

  case class BaseUrl(value: String) extends AnyVal with StringValue

  case class NetDomain(value: String) extends AnyVal with StringValue
  case class AssetDomain(value: String) extends AnyVal with StringValue

  case class NetConfig(
    domain: NetDomain,
    protocol: String,
    @ConfigName("base_url") baseUrl: BaseUrl,
    @ConfigName("asset.domain") assetDomain: AssetDomain,
    @ConfigName("socket.domain") socketDomain: String,
    crawlable: Boolean
  )

  implicit val secretLoader = strLoader(Secret.apply)
  implicit val baseUrlLoader = strLoader(BaseUrl.apply)
  implicit val netDomainLoader = strLoader(NetDomain.apply)
  implicit val assetDomainLoader = strLoader(AssetDomain.apply)
  implicit val netLoader = AutoConfig.loader[NetConfig]


  def strLoader[A](f: String => A): ConfigLoader[A] = ConfigLoader(_.getString) map f
}
