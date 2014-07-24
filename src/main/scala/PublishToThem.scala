package sbt

import Keys._
import complete._
import complete.DefaultParsers._
import Def.Initialize

object PublishToThem extends Plugin {

  val publishToThem = InputKey[Unit]("publish-to-them", "Publish to repositories specified as arguments.")

  val publishableRepos = SettingKey[Map[String, Resolver]]("publishable-repositories", "Repositories to publish to.")

  val parser: Initialize[Parser[Seq[String]]] = Def.setting {
    val names = publishableRepos.value.keys.map(token(_)).reduce(_ | _)
    token(Space ~> names)*
  }

  override lazy val buildSettings = Seq(
    publishableRepos := Map(
      "ivylocal" -> Resolver.defaultLocal,
      "mavenLocal" -> Resolver.publishMavenLocal
    )
  )

  override lazy val settings = Seq(

    otherResolvers := (otherResolvers.value ++ publishableRepos.value.values).distinct,

    publishToThem := {
      val names: Seq[String] = parser.parsed
      val reposToPublish = names.flatMap(publishableRepos.value.get)
      val log = streams.value.log
      reposToPublish.distinct.foreach { repo =>
        val config = if (repo == Resolver.publishMavenLocal) {
          publishM2Configuration.value
        }
        else if (repo == Resolver.defaultLocal) {
          publishLocalConfiguration.value
        }
        else {
          Classpaths.publishConfig(
            packagedArtifacts.in(publish).value,
            if (publishMavenStyle.value) None else Some(deliver.value),
            resolverName = repo.name,
            checksums = checksums.in(publish).value,
            logging = ivyLoggingLevel.value, overwrite = isSnapshot.value
          )
        }
        IvyActions.publish(ivyModule.value, config, log)
      }
    }

  )
}
