package oyun.i18n

import scalatags.Text.all._

import oyun.common.String.html.escapeHtml

sealed private trait Translation

final private class Simple(val message: String) extends Translation {

  def formatTxt(args: Seq[Any]): String =
    if (args.isEmpty) message
    else message.format(args: _*)

  def format(args: Seq[RawFrag]): RawFrag =
    if (args.isEmpty) RawFrag(message)
    else RawFrag(message.format(args.map(_.v): _*))

  override def toString = s"Simple($message)"

}

final private class Escaped(val message: String, escaped: String) extends Translation {

  def formatTxt(args: Seq[Any]): String =
    if (args.isEmpty) message
    else message.format(args: _*)

  def format(args: Seq[RawFrag]): RawFrag =
    if (args.isEmpty) RawFrag(escaped)
    else RawFrag(escaped.format(args.map(_.v): _*))

  override def toString = s"Escaped($message)"
}
