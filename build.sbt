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
  poker, scalaz, scalalib, hasher,
  reactivemongo.driver, prismic, scalatags,
  kamon.core, kamon.influxdb, kamon.metrics,
  scaffeine, lettuce
) ++ silencer.bundle

lazy val modules = Seq(common, db, user, i18n, security, blog, hub, socket, lobby, game, masa, round)

lazy val moduleRefs = modules map projectToRef
lazy val moduleCPDeps = moduleRefs map { new sbt.ClasspathDependency(_, None) }

lazy val api = module("api",
  moduleCPDeps,
  Seq(play.api, play.json, kamon.core, kamon.influxdb, hasher) ++ reactivemongo.bundle
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
  Seq(kamon.core, scalatags, scaffeine) ++ reactivemongo.bundle
)

lazy val db = module("db",
  Seq(common),
  Seq(hasher) ++ reactivemongo.bundle
)

lazy val user = module("user",
  Seq(common, db, memo),
  Seq(hasher) ++ reactivemongo.bundle
)


lazy val security = module("security",
  Seq(common, db, i18n),
  Seq(scalatags, hasher) ++ reactivemongo.bundle
)

lazy val blog = module("blog",
  Seq(common),
  Seq(prismic) ++ reactivemongo.bundle
)

lazy val lobby = module("lobby",
  Seq(common, db, user, memo, masa, socket, hub),
  Seq(lettuce) ++ reactivemongo.bundle
)


lazy val masa = module("masa",
  Seq(common, db, user, game, memo, room, socket, hub),
  Seq(lettuce) ++ reactivemongo.bundle
)

lazy val round = module("round",
  Seq(common, db, user, game, memo, socket, hub),
  Seq(lettuce) ++ reactivemongo.bundle
)

lazy val game = module("game",
  Seq(common, db, user, memo, socket, hub),
  Seq() ++ reactivemongo.bundle
)

lazy val room = module("room",
  Seq(common, socket),
  Seq(lettuce) ++ reactivemongo.bundle
)

lazy val memo = module("memo",
  Seq(common, db),
  Seq(scaffeine) ++ reactivemongo.bundle
)

lazy val socket = module("socket",
  Seq(common, hub, memo),
  Seq(lettuce)
)

lazy val hub = module("hub",
  Seq(common),
  Seq()
)
