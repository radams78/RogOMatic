name := "Main"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "JCenter" at "https://jcenter.bintray.com/"

libraryDependencies += "org.jetbrains.jediterm" % "jediterm-pty" % "2.22"
libraryDependencies += "org.jetbrains.pty4j" % "pty4j" % "0.9.6"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % "test"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.30"

scalacOptions += "-deprecation"
scalacOptions += "-feature"
scalacOptions += "-language:implicitConversions"
scalacOptions += "-unchecked"