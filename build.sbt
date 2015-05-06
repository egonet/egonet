name := "egonet"

libraryDependencies += "net.sf.jung" % "jung-api" % "2.0.1"

libraryDependencies += "net.sf.jung" % "jung-graph-impl" % "2.0.1"

libraryDependencies += "net.sf.jung" % "jung-algorithms" % "2.0.1"

libraryDependencies += "net.sf.jung" % "jung-io" % "2.0.1"

libraryDependencies += "net.sf.jung" % "jung-visualization" % "2.0.1"

libraryDependencies += "org.swinglabs" % "swingx" % "0.9.7"

libraryDependencies += "org.jdesktop" % "swing-worker" % "1.1"

libraryDependencies += "org.swinglabs" % "swing-layout" % "1.0.3"

libraryDependencies += "com.miglayout" % "miglayout" % "3.7"

libraryDependencies += "com.jgoodies" % "forms" % "1.1.0"

libraryDependencies += "com.jgoodies" % "looks" % "2.1.4"

libraryDependencies += "com.google.guava" % "guava" % "r08"

libraryDependencies += "com.jcraft" % "jsch" % "0.1.41"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "1.8"

libraryDependencies += "com.lowagie" % "itext" % "2.1.5"

libraryDependencies += "com.lowagie" % "itext-rtf" % "2.1.5"

libraryDependencies += "commons-codec" % "commons-codec" % "1.3"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.5.6"

libraryDependencies += "org.slf4j" % "slf4j-jdk14" % "1.5.6"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.9" % "test"

mainClass in (Compile, run) := Some("org.egonet.gui.EgonetRunner")

mainClass in assembly := Some("org.egonet.gui.EgonetRunner")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
//    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
//    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
//    case "application.conf" => MergeStrategy.concat
    case "RELEASE-NOTES.txt"     => MergeStrategy.discard
    case x => old(x)
  }
}
