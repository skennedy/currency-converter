val Http4sVersion     = "0.20.1"
val CirceVersion      = "0.11.1"
val ScalaCacheVersion = "0.27.0"
val ScalaTestVersion  = "3.0.5"
val LogbackVersion    = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.skennedy",
    name := "currency-converter",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    scalacOptions ++= Seq("-Ypartial-unification"),
    libraryDependencies ++= Seq(
      "org.http4s"       %% "http4s-blaze-server"    % Http4sVersion,
      "org.http4s"       %% "http4s-blaze-client"    % Http4sVersion,
      "org.http4s"       %% "http4s-circe"           % Http4sVersion,
      "org.http4s"       %% "http4s-dsl"             % Http4sVersion,
      "io.circe"         %% "circe-generic"          % CirceVersion,
      "com.github.cb372" %% "scalacache-guava"       % ScalaCacheVersion,
      "com.github.cb372" %% "scalacache-cats-effect" % ScalaCacheVersion,
      "org.scalatest"    %% "scalatest"              % ScalaTestVersion % "test",
      "ch.qos.logback"   % "logback-classic"         % LogbackVersion
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector"     % "0.9.6"),
    addCompilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.2.4")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)
