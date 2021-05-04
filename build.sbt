name := "tempjson"

version := "0.1"

resolvers += "runeliteMaven" at "https://repo.runelite.net"

scalaVersion := "2.13.5"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "ai.x" %% "play-json-extensions" % "0.42.0"
libraryDependencies += "net.runelite" % "runelite-api" % "1.7.6"
