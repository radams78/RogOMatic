name := "RogOMatic"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "JCenter" at "https://jcenter.bintray.com/"

libraryDependencies += "org.jetbrains.jediterm" % "jediterm-pty" % "2.22"
libraryDependencies += "org.jetbrains.pty4j" % "pty4j" % "0.9.6"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"

scalacOptions += "-deprecation"