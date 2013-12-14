name := "hash-new-impl"

version := "1.0"

scalaVersion := "2.10.0"

resolvers += "Sonatype OSS Snapshots" at
  "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.github.axel22" % "scalameter_2.10" % "0.5-SNAPSHOT"

testFrameworks += new TestFramework(
  "org.scalameter.ScalaMeterFramework")

logBuffered := false