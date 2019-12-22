import BuildSettings._
import Dependencies._

lazy val root = Project("oyun", file("."))
  .enablePlugins(PlayScala, PlayAkkaHttpServer)
  .dependsOn(api)
  .aggregate(api)


scalaVersion := globalScalaVersion
resolvers ++= Dependencies.Resolvers.commons
scalacOptions ++= compilerOptions //  :+ "-P:silencer:pathFilters=target/scala-2.13/routes"
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
)

lazy val modules = Seq(common, user)

lazy val moduleRefs = modules map projectToRef
lazy val moduleCPDeps = moduleRefs map { new sbt.ClasspathDependency(_, None) }

lazy val api = module("api",
  moduleCPDeps,
  Seq(play.api, play.json, hasher) ++ reactivemongo.bundle
).settings(
  aggregate in Runtime := false,
  aggregate in Test := true
) aggregate (moduleRefs: _*)


lazy val common = module("common",
  Seq(),
  Seq(scalatags, scaffeine) ++ reactivemongo.bundle
)

lazy val user = module("user",
  Seq(common),
  Seq(hasher) ++ reactivemongo.bundle
)
