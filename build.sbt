name := "hash-new-impl"

version := "1.0"

scalaVersion := "2.10.0"

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.github.axel22" % "scalameter_2.10" % "0.4"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

testFrameworks += new TestFramework(
  "org.scalameter.ScalaMeterFramework")

logBuffered := false