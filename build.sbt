lazy val distProject = project
  .in(file("."))
  .enablePlugins(JavaAgent)
  .settings(
    //    javaAgents += "io.opentelemetry.javaagent" % "opentelemetry-javaagent" % "1.7.2"
    javaAgents += JavaAgent("OpenTelemetry" % "javaagent" % "1.7.2-Release" % "runtime")
    //      javaAgents += JavaAgent("com.example" % "agent" % "1.2.3" % "compile;test", arguments = "java_agent_argument_string")

  )


name := "akka-http-microservice"
organization := "com.theiterators"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += "Artifactory" at "https://aspecto.jfrog.io/artifactory/aspecto-public-maven"
javaOptions in run := List(
  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
  "-javaagent:/Users/dave.e/Library/Caches/Coursier/v1/https/aspecto.jfrog.io/artifactory/aspecto-public-maven/OpenTelemetry/javaagent/1.7.2-Release/javaagent-1.7.2-Release.jar"
)

libraryDependencies ++= {
  val akkaHttpV = "10.0.5"
  val slickV = "3.2.0"
  val scalaTestV = "3.0.1"
  val h2V = "1.4.193"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.slick" %% "slick" % slickV,
    "org.slf4j" % "slf4j-nop" % "1.7.10",
    "com.h2database" % "h2" % h2V,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test"
  ) ++ Seq(
    "OpenTelemetry" % "javaagent" % "1.7.2-Release"
  )
}