publish-to-them
===============

publish-to-them is an sbt plugin that enables publishing to one or more repositories specified at command line.

## How to use

Add the plugin globally in ~/.sbt/0.13/plugins/publish-to-them.sbt:
```scala
addSbtPlugin("me.thirstycrow.sbt" % "publish-to-them" % "0.1")
```

Add most often used repositories in PublishToThem.publishableRepos:
```scala
PublishToThem.publishableRepos ++= Map(
  "gh-pages" -> {
    val userHome = System.getProperty("user.home")
    Resolver.file("gh-pages", file(userHome) / "Projects" / "repo")
  },
  "artifactoryLocal" -> {
    val artifactoryLocal = "http://127.0.0.1:8081/artifactory"
    if (version.value endsWith "SNAPSHOT")
      "artifactory-local-snapshots" at artifactoryLocal + "/libs-snapshot-local"
    else
      "artifactory-local-release" at artifactoryLocal + "/libs-release-local"
  },
  "company" -> {
    val company = "http://repo.mycompany.com/nexus/content/repositories"
    if (version.value endsWith "SNAPSHOT")
      "company-snapshots" at company + "/snapshots"
    else
      "company-releases" at company + "/releases"
  }
)
```

Then publish to any repositories defined in publishableRepos by:
```
$ sbt publishToThem artifactoryLocal gh-pages company
```

In sbt console, press `tab` to see the list of publishableRepos.
