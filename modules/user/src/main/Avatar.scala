package oyun.user

import oyun.common.{ AssetVersion }

import scala.util.Random.nextInt

case class Avatar(value: String) {

  def link: String = makeAvatarUrl(value)

  private def makeAvatarUrl(avatar: String): String = s"assets/_${AssetVersion.current}/avatars/701987-avatar/png/${avatar}"

}

case object Avatar {

  val names = List(
    "001-man-13.png",
    "002-woman-14.png",
    "003-woman-13.png",
    "004-woman-12.png",
    "005-woman-11.png",
    "006-woman-10.png",
    "007-woman-9.png",
    "008-woman-8.png",
    "009-woman-7.png",
    "010-woman-6.png",
    "011-woman-5.png",
    "012-woman-4.png",
    "013-woman-3.png",
    "014-man-12.png",
    "015-man-11.png",
    "016-man-10.png",
    "017-man-9.png",
    "018-man-8.png",
    "019-man-7.png",
    "020-man-6.png",
    "021-man-5.png",
    "022-man-4.png",
    "023-man-3.png",
    "024-man-2.png",
    "025-man-1.png",
    "026-man.png",
    "027-boy-6.png",
    "028-boy-5.png",
    "029-boy-4.png",
    "030-boy-3.png",
    "031-boy-2.png",
    "032-boy-1.png",
    "033-boy.png",
    "034-woman-2.png",
    "035-woman-1.png",
    "036-woman.png",
  )

  val all = names map Avatar.apply

  def pickRandom: Avatar = all(nextInt(all.length))

}
