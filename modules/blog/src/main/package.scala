package oyun

package object blog extends PackageObject {

  private[blog] def logger = oyun.log("blog")

  lazy val thisYear = org.joda.time.DateTime.now.getYear
  
}
