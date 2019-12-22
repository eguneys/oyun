package oyun.common

case class ApiVersion(value: Int) extends AnyVal {
  def v1 = value == 1
}

case class AssetVersion(value: String) extends AnyVal

object AssetVersion {

  var current = random
  def change() = { current = random }

  private def random = AssetVersion(ornicar.scalalib.Random secureString 6)

}
