sbtPlugin := true

organization := "me.thirstycrow.sbt"

name := "publish-to-them"

version := "0.1"

scalaVersion := "2.10.4"

sbtVersion := "0.13.5"

description := "publish to one or more repositories"

scalacOptions := Seq("-deprecation", "-unchecked")

PublishToThem.repositories := Map(
  "artifactoryLocal" -> {
    val artifactoryLocal = "http://127.0.0.1:8081/artifactory"
    if (version.value endsWith "SNAPSHOT")
      "artifactory-local-snapshots" at artifactoryLocal + "/libs-snapshot-local"
    else
      "artifactory-local-release" at artifactoryLocal + "/libs-release-local"
  },
  "xueqiu" -> {
    val xueqiu = "http://repo.snowballfinance.com/nexus/content/repositories"
    if (version.value endsWith "SNAPSHOT")
      "xueqiu-snapshots" at xueqiu + "/snapshots"
    else
      "xueqiu-releases" at xueqiu + "/releases"
  }
)
