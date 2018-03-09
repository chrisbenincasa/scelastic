logLevel := Level.Warn

credentials += Credentials(Path.userHome / ".sbt" / "credentials")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
