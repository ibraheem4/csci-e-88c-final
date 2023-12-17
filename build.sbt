import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "csci-e-88c-final",
    libraryDependencies ++= scalaTest ++ core
  )
