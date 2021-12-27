val akkaVersion = "2.5.32"
val akkaHttpVersion = "10.2.0"
val logbackVersion = "1.2.3"
val slickVersion = "3.2.0"
val slf4jVersion = "1.7.10"
val scalaTestVersion = "3.0.1"
val h2Version = "1.4.193"
val javaAgentVersion = "1.9-Release"

name := "akka-http-microservice"
organization := "com.theiterators"

version := "1.0"
scalaVersion := "2.12.2"


Compile / mainClass := Some("org.example.service.RestService")

lazy val distProject = project
  .in(file("."))
  .enablePlugins(JavaAgent)
  .settings(
    javaAgents += JavaAgent("OpenTelemetry" % "javaagent" % "1.9-Release" % "runtime")
  )

resolvers += "Artifactory" at "https://aspecto.jfrog.io/artifactory/aspecto-public-maven"
run / javaOptions := List(
  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
  "-javaagent:/Users/dave.e/Library/Caches/Coursier/v1/https/aspecto.jfrog.io/artifactory/aspecto-public-maven/OpenTelemetry/javaagent/1.9-Release/javaagent-1.9-Release.jar"
)

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka"               %% "akka-http"                % akkaHttpVersion,
    "com.typesafe.akka"               %% "akka-http-spray-json"     % akkaHttpVersion,
    "com.typesafe.akka"               %% "akka-http-testkit"        % akkaHttpVersion % "test",
    "com.typesafe.akka"               %% "akka-actor-typed"         % akkaVersion,
    "com.typesafe.akka"               %% "akka-stream"              % akkaVersion,
    "com.typesafe.akka"               %% "akka-actor-typed"         % akkaVersion,
    "com.typesafe.akka"               %% "akka-stream-typed"        % akkaVersion,
    "com.typesafe.slick"              %% "slick"                    % slickVersion,
    "org.slf4j"                       % "slf4j-nop"                 % slf4jVersion,
    "com.h2database"                  % "h2"                        % h2Version,
    "org.scalatest"                   %% "scalatest"                % scalaTestVersion % "test",
    "OpenTelemetry"                   % "javaagent"                 % javaAgentVersion
    // "io.opentelemetry.javaagent"      % "opentelemetry-javaagent"   % "1.9.0",
    // https://mvnrepository.com/artifact/io.opentelemetry.javaagent/opentelemetry-muzzle
    // "io.opentelemetry.javaagent" % "opentelemetry-javaagent-instrumentation-api" % "1.9.0-alpha" % "runtime",
    // "io.opentelemetry.javaagent" % "opentelemetry-agent-for-testing" % "1.9.0-alpha"
  )
}
