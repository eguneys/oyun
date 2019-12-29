import BuildSettings._
import Dependencies._

lazy val root = Project("oyun", file("."))
  .enablePlugins(PlayScala, PlayAkkaHttpServer)
  .dependsOn(api)
  .aggregate(api)


scalaVersion := globalScalaVersion
resolvers ++= Dependencies.Resolvers.commons
scalacOptions ++= compilerOptions :+ "-P:silencer:pathFilters=target/scala-2.13/routes"
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false
PlayKeys.playDefaultPort := 9663

PlayKeys.externalizeResources := false
scriptClasspath := Seq("*")

resourceDirectory in Assets := baseDirectory.value / "public-nothanks"

PlayKeys.generateAssetsJar := false
// routesGenerator := InjectedRoutesGenerator


libraryDependencies ++= Seq(
  macwire.macros, macwire.util, play.json, ws,
  scalaz, scalalib, hasher,
  reactivemongo.driver, reactivemongo.bson,
  scalatags,
  scaffeine
) ++ silencer.bundle

lazy val modules = Seq(common, db, user, i18n, security)

lazy val moduleRefs = modules map projectToRef
lazy val moduleCPDeps = moduleRefs map { new sbt.ClasspathDependency(_, None) }

lazy val api = module("api",
  moduleCPDeps,
  Seq(play.api, play.json, hasher) ++ reactivemongo.bundle
).settings(
  aggregate in Runtime := false,
  aggregate in Test := true
) aggregate (moduleRefs: _*)


lazy val i18n = module("i18n",
  Seq(common, user),
  Seq(scalatags)
).settings(
  sourceGenerators in Compile += Def.task {
    MessageCompiler(
      sourceDir = new File("translation/source"),
      destDir = new File("translation/dest"),
      dbs = List("site"),
      compileTo = (sourceManaged in Compile).value / "messages"
    )
  }.taskValue,
  scalacOptions += "-P:silencer:pathFilters=modules/i18n/target"
)

lazy val common = module("common",
  Seq(),
  Seq(scalatags, scaffeine) ++ reactivemongo.bundle
)

lazy val db = module("db",
  Seq(common),
  Seq(hasher) ++ reactivemongo.bundle
)

lazy val user = module("user",
  Seq(common, db),
  Seq(hasher) ++ reactivemongo.bundle
)


lazy val security = module("security",
  Seq(common, db, i18n),
  Seq(scalatags, hasher) ++ reactivemongo.bundle
)
