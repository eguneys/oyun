package oyun.common

case class ApiVersion(value: Int) extends AnyVal {
  def v1 = value == 1
}

case class AssetVersion(value: String) extends AnyVal with StringValue

object AssetVersion {

  var current = random
  def change() = { current = random }

  private def random = AssetVersion(ornicar.scalalib.Random secureString 6)

}

case class NormalizedEmailAddress(value: String) extends AnyVal with StringValue

case class EmailAddress(value: String) extends AnyVal with StringValue {
  def conceal = value split '@' match {
    case Array(user, domain) => s"${user take 3}*****@${domain}"
    case _                   => value
  }
  def normalize = NormalizedEmailAddress {
    val lower = value.toLowerCase
    lower.split('@') match {
      case Array(name, domain) if domain == "gmail.com" || domain == "googlemail.com" => {
        val normalizedName = name
          .replace(".", "")  // remove all dots
          .takeWhile('+' !=) // skip everything after the first '+'
        if (normalizedName.isEmpty) lower else s"$normalizedName@$domain"
      }
      case _ => lower
    }
  }
  // def domain: Option[Domain] = value split '@' match {
  //   case Array(_, domain) => Domain from domain.toLowerCase
  //   case _                => none
  // }

  // safer logs
  override def toString = "EmailAddress(****)"
}

object EmailAddress {

  private val regex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]++@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def matches(str: String): Boolean = (regex findFirstIn str) isDefined

  def from(str: String): Option[EmailAddress] =
    matches(str) option EmailAddress(str)
}
