lazy val repoDir = file("shared-repo")

lazy val sharedResolver =
  Resolver.defaultShared
  //MavenRepository("example-shared-repo", "file:///tmp/shared-maven-repo-bad-example")
  //Resolver.file("example-shared-repo", repoDir)(Resolver.defaultPatterns)

lazy val common = (
  project
  .settings(
    organization := "com.badexample",
    name := "badexample",
    version := "1.0-SNAPSHOT",
    publishTo := Some(sharedResolver),
    crossVersion := CrossVersion.Disabled,
    publishMavenStyle := (publishTo.value match {
      case Some(repo) =>
        repo match {
          case repo: PatternsBasedRepository => repo.patterns.isMavenCompatible
          case _: RawRepository => false // TODO - look deeper
          case _: MavenRepository => true
          case _ => false  // TODO - Handle chain repository?
        }
      case _ => true
    })
  )
)

lazy val dependent = (
  project
  .settings(
    // Ignore the inter-project resolver, so we force to look remotely.
    resolvers += sharedResolver,
    fullResolvers := Seq(sharedResolver),
    libraryDependencies += "com.badexample" % "badexample" % "1.0-SNAPSHOT"
  )
)

lazy val dumpContents = taskKey[Unit]("dump contents of shared repo directory")

dumpContents := {
  repoDir.***.get.map(_.getAbsolutePath).sorted foreach { fname =>
     streams.value.log.info(fname)
  }
}