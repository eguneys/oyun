package oyun.user

import oyun.common.config.Secret

case class HashedPassword(bytes: Array[Byte]) extends AnyVal {
  def parse = bytes.length == 39 option bytes.splitAt(16)
}

final private class PasswordHasher(
) {


}
