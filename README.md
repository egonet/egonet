# EgoNet

Check out downloads and more on SourceForge at http://egonet.sf.net.

[![Download egonet](https://a.fsdn.com/con/app/sf-download-button)](https://sourceforge.net/projects/egonet/files/latest/download)

## Installing and building

Egonet uses the Scala Build too; you'll need this installed to run tests, run development versions, and build artifacts to be used for releases.

[![CircleCI Status Badge](https://circleci.com/gh/egonet/egonet.svg?style=shield&circle-token=f120f41c0d823f28fe3421ec4b92cb149af5c5e0)](https://circleci.com/gh/egonet/egonet)

## Running the project

SBT can also be used to run Egonet directly from source, by using:
```
$ sbt run
Java HotSpot(TM) 64-Bit Server VM warning: ignoring option MaxPermSize=256M; support was removed in 8.0
[info] Loading project definition from /Users/martin/src/egonet/project
[info] Set current project to egonet (in build file:/Users/martin/src/egonet/)
[info] Updating {file:/Users/martin/src/egonet/}egonet...
[info] Resolving org.jacoco#org.jacoco.agent;0.7.1.201405082137 ...
[info] Done updating.
[info] Compiling 176 Java sources to /Users/martin/src/egonet/target/classes...
[info] /Users/martin/src/egonet/src/main/java/org/egonet/gui/EgoStore.java: Some input files use or override a deprecated API.
[info] /Users/martin/src/egonet/src/main/java/org/egonet/gui/EgoStore.java: Recompile with -Xlint:deprecation for details.
[info] Running org.egonet.gui.EgonetRunner
```

## Releases

The `sbt assembly` plugin can be used to make a 'fat' jar that contains everything needed for Egonet, by running:

```
$ sbt assembly
[info] SHA-1: a93b4d23f49d32c7ff5229da7eef8ca9d34b03af
[info] Packaging /Users/martin/src/egonet/target/egonet-assembly-0.1-SNAPSHOT.jar ...
[info] Done packaging.
[success] Total time: 3 s, completed Mar 26, 2017 1:48:08 PM
```

`sbt build-launcher` can be used to build a Windows executable as well.

## Versions and Changes

Before 2017, versions were based on dates. After that, we started using [semantic versioning](http://semver.org). We've gone back and retroactively assigned versions to all of the old date based releases, and added them to the [CHANGELOG](CHANGELOG.md) for easy reference. Please beware that matching dates to commits for the older revisions wasn't perfect, and may not match up exactly for the very, very old ones.

## To do
- Write more unit tests for other features, specifically study and interview readers and writers
- Convert ListBuilder to something with better editing w/ undelete, reorder, where GUID for a selection is preserved
- Convert file formats to use java.beans.XMLEncoder and java.beans.XMLDecoder
- Explain how to build runnable jars and executables
