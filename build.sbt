import org.typelevel.Dependencies._

addCommandAlias("gitSnapshots", ";set version in ThisBuild := git.gitDescribedVersion.value.get + \"-SNAPSHOT\"")

val apache2 = "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
val gh = GitHubSettings(org = "kailuowang", proj = "play-json-util", publishOrg = "com.kailuowang", license = apache2)


lazy val myLibraries = Map(
  singleModuleLib("newtype" , "io.estatico"),
  singleModuleLib("scalacheck-shapeless_1.13" , "com.github.alexarchambault"),
  singleModuleLib("play-json" , "com.typesafe.play")
)

lazy val myVersions = Map(
  "newtype" -> "0.1.0",
  "scalacheck-shapeless_1.13" -> "1.1.6",
  "play-json" -> "2.6.8"
)

val vAll = Versions(versions ++ myVersions, libraries ++ myLibraries, scalacPlugins)

lazy val rootSettings = buildSettings ++ commonSettings ++ publishSettings ++ scoverageSettings
lazy val module = mkModuleFactory(gh.proj, mkConfig(rootSettings, commonJvmSettings, commonJsSettings))
lazy val prj = mkPrjFactory(rootSettings)


lazy val rootPrj = project
  .configure(mkRootConfig(rootSettings,rootJVM))
  .aggregate(rootJVM, rootJS)
  .settings(noPublishSettings)


lazy val rootJVM = project
  .configure(mkRootJvmConfig(gh.proj, rootSettings, commonJvmSettings))
  .aggregate(coreJVM, testsJVM, docs)
  .settings(noPublishSettings)


lazy val rootJS = project
  .configure(mkRootJsConfig(gh.proj, rootSettings, commonJsSettings))
  .aggregate(coreJS, testsJS)
  .settings(noPublishSettings)


lazy val core    = prj(coreM)
lazy val coreJVM = coreM.jvm
lazy val coreJS  = coreM.js
lazy val coreM   = module("core", CrossType.Pure)
  .settings(addLibs(vAll, "shapeless", "play-json"))

lazy val newType    = prj(newTypeM)
lazy val newTypeJVM = newTypeM.jvm
lazy val newTypeJS  = newTypeM.js
lazy val newTypeM   = module("newtype", CrossType.Pure)
  .dependsOn(coreM)
  .aggregate(coreM)
  .settings(addJVMLibs(vAll, "newtype"))

lazy val tests    = prj(testsM)
lazy val testsJVM = testsM.jvm
lazy val testsJS  = testsM.js
lazy val testsM   = module("tests", CrossType.Pure)
  .dependsOn(coreM, newTypeM)
  .aggregate(coreM, newTypeM)
  .settings(noPublishSettings)
  .settings(addTestLibs(vAll, "scalatest"))


/** Docs - Generates and publishes the scaladoc API documents and the project web site using sbt-microsite.*/
lazy val docs = project.configure(mkDocConfig(gh, rootSettings, commonJvmSettings,
  coreJVM))

lazy val buildSettings = sharedBuildSettings(gh, vAll)

lazy val commonSettings = sharedCommonSettings ++ scalacAllSettings ++ Seq(
  parallelExecution in Test := false,
  crossScalaVersions := Seq(vAll.vers("scalac_2.11"), scalaVersion.value),
  scalacOptions in Test ~= (_.filterNot(Set("-Ywarn-unused-import", "-Ywarn-dead-code")))
)  ++ unidocCommonSettings ++
  addCompilerPlugins(vAll, "kind-projector")

lazy val commonJsSettings = Seq(scalaJSStage in Global := FastOptStage)

lazy val commonJvmSettings = Seq()

lazy val publishSettings = sharedPublishSettings(gh) ++ credentialSettings ++ sharedReleaseProcess

lazy val scoverageSettings = sharedScoverageSettings(60)
