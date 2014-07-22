package sbt

import Keys._
import complete._
import complete.DefaultParsers._
import Def.Initialize

object PublishToThem extends Plugin {

  val publishToThem = InputKey[Unit]("publish-to-them", "Publish to repositories specified as arguments.")

  val repositories = SettingKey[Map[String, Resolver]]("repositories", "Repositories to publish to.")

  val parser: Initialize[Parser[Seq[String]]] = Def.setting {
    val names = repositories.value.keys.map(token(_)).reduce(_ | _)
    token(Space ~> names)*
  }

  override lazy val settings = Seq(

    publishToThem := {
      val names: Seq[String] = parser.parsed
      val reposToPublish = names.flatMap(repositories.value.get)
      val log = streams.value.log
      ivyModule.value.withModule(log) {
        case (ivy, module, _) =>
          val artifacts = IvyActions.mapArtifacts(module, Some(sv => sv), packagedArtifacts.value)
          reposToPublish.distinct.foreach { repo =>
            val ivyResolver = ConvertResolver(repo, ivy.getSettings, log)
            ivyResolver.asInstanceOf[org.apache.ivy.plugins.resolver.AbstractResolver].setSettings(ivy.getSettings)
            IvyActions.publish(module, artifacts, ivyResolver, true)
          }
      }
    }

  )
}
