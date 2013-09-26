name := "play-module-greenmail"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "com.icegreen" % "greenmail" % "1.3.1b"
)     

play.Project.playJavaSettings
