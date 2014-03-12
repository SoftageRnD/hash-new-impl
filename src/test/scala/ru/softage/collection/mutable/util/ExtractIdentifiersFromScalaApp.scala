package ru.softage.collection.mutable.util

import scala.util.matching.Regex
import java.io._
import scala.io.Source
import scala.util.Random


object ExtractIdentifiersFromScalaApp extends App {
  val PathToScala = "/media/migesok/a18cf1fa-5ed3-4899-975f-e2cf727b0221/home/migesok/prog/scala-rnd"
  val PathToResultFile = "/home/migesok/identifiers.txt"

  val PlainIdPattern = "([a-zA-Z][a-zA-Z0-9_]*)".r

  def extractIdentifiers(str: String): Set[String] = {
    (for (m <- PlainIdPattern.findAllMatchIn(str)) yield m.group(1)).toSet
  }

  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_, r))
  }

  val rawIds = for {
    file <- recursiveListFiles(new File(PathToScala), "\\.scala".r).filter(_.getName.endsWith(".scala"))
    line <- Source.fromFile(file).getLines()
    id <- extractIdentifiers(line)
  } yield id

  val shuffledIds = new Random(1488L).shuffle(rawIds.toBuffer.distinct)

  val writer = new PrintWriter(
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PathToResultFile, false), "UTF-8")))
  try {
    shuffledIds.foreach(id => writer.println(id))
  } finally {
    writer.close()
  }
}
