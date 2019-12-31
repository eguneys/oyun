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
  def domain: Option[Domain] = value split '@' match {
    case Array(_, domain) => Domain from domain.toLowerCase
    case _                => none
  }

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


case class Domain private (value: String) extends AnyVal with StringValue {
  // heuristic to remove user controlled subdomain tails:
  // tail.domain.com, tail.domain.co.uk, tail.domain.edu.au, etc.
  def withoutSubdomain: Option[Domain] = value.split('.').toList.reverse match {
    case tld :: sld :: tail :: _ if sld.length <= 3 => Domain from s"$tail.$sld.$tld"
    case tld :: sld :: _                            => Domain from s"$sld.$tld"
    case _                                          => none
  }
  def lower = Domain.Lower(value.toLowerCase)
}

object Domain {
  // https://stackoverflow.com/a/26987741/1744715
  private val regex =
    """^(((?!-))(xn--|_{1,1})?[a-z0-9-]{0,61}[a-z0-9]{1,1}\.)*(xn--)?([a-z0-9][a-z0-9\-]{0,60}|[a-z0-9-]{1,30}\.[a-z]{2,})$""".r
  def isValid(str: String)              = regex.matches(str)
  def from(str: String): Option[Domain] = isValid(str) option Domain(str)
  def unsafe(str: String): Domain       = Domain(str)

  case class Lower(value: String) extends AnyVal with StringValue {
    def domain = Domain(value)
  }
}
