name := "untitled"

version := "0.1"

scalaVersion := "2.13.4"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.badlogicgames.gdx" % "gdx" % "1.9.13"
libraryDependencies += "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % "1.9.13"
libraryDependencies += "com.badlogicgames.gdx" % "gdx-platform" % "1.9.13" classifier "natives-desktop"

libraryDependencies += "space.earlygrey" % "shapedrawer" % "2.4.0"