name    := "egonet"
version := "1.0.0"

// Pure Java project — no Scala library on the classpath
autoScalaLibrary := false
crossPaths       := false

javacOptions ++= Seq("--release", "21")

Compile / run / mainClass  := Some("org.egonet.gui.EgonetRunner")
assembly / mainClass       := Some("org.egonet.gui.EgonetRunner")
assembly / assemblyJarName := name.value + "-" + version.value + ".jar"

libraryDependencies ++= Seq(
  // Graph visualization (2.1.x changed Transformer→Function; source uses 2.0.x API)
  "net.sf.jung"          % "jung-api"            % "2.0.1",
  "net.sf.jung"          % "jung-graph-impl"     % "2.0.1",
  "net.sf.jung"          % "jung-algorithms"     % "2.0.1",
  "net.sf.jung"          % "jung-io"             % "2.0.1",
  "net.sf.jung"          % "jung-visualization"  % "2.0.1",

  // Swing UI
  // swing-worker: source uses org.jdesktop.swingworker (Java 5 backport);
  //   update imports to javax.swing.SwingWorker to drop this dep.
  // swing-layout: source uses org.jdesktop.layout.GroupLayout;
  //   update imports to javax.swing.GroupLayout to drop this dep.
  "org.jdesktop"         % "swing-worker"        % "1.1",
  "org.swinglabs"        % "swing-layout"        % "1.0.3",
  "org.swinglabs.swingx" % "swingx-all"          % "1.6.5-1",
  "com.miglayout"        % "miglayout-swing"     % "5.3",
  // jgoodies 1.6+ (jgoodies-forms artifact) removed addGridded/setLeadingColumnOffset
  "com.jgoodies"         % "forms"               % "1.1.0",
  "com.jgoodies"         % "looks"               % "2.1.4",

  // Utilities
  "com.google.guava"     % "guava"               % "33.4.0-jre",
  "com.github.mwiede"    % "jsch"                % "0.2.21",   // maintained fork of com.jcraft:jsch
  "com.opencsv"          % "opencsv"             % "5.10",
  "commons-io"           % "commons-io"          % "2.18.0",
  "commons-codec"        % "commons-codec"       % "1.17.1",

  // PDF / RTF output (last LGPL iText release)
  "com.lowagie"          % "itext"               % "2.1.7",
  "com.lowagie"          % "itext-rtf"           % "2.1.7",

  // Logging
  "org.slf4j"            % "slf4j-api"           % "2.0.16",
  "org.slf4j"            % "slf4j-jdk14"         % "2.0.16",

  // Test
  "junit"                % "junit"               % "4.13.2"   % Test,
  "com.novocode"         % "junit-interface"     % "0.11"     % Test,
)

assembly / assemblyMergeStrategy := {
  case "RELEASE-NOTES.txt" => MergeStrategy.discard
  case x =>
    val old = (assembly / assemblyMergeStrategy).value
    old(x)
}
