package ru.softage.scalatest

import org.scalameter.Gen
import org.scalameter.api._
import scala.collection.mutable
import scala.util.Random

object HashSetBenchmark extends PerformanceTest {

  /* Configuration */

  lazy val executor = LocalExecutor(//need to change to SeparateJvmsExecutor
    new Executor.Warmer.Default,
    Aggregator.min,
    new Measurer.Default)
  lazy val reporter = ChartReporter(ChartFactory.XYLine())
  lazy val persistor = Persistor.None

  /* Inputs */

  val count: Gen[Int] = Gen.enumeration("size")(3000000, 9000000, 15000000)
  val rand: Random = new Random(1l)

  def scalaIntSets = for {
    size <- count
  } yield {
    val hs = mutable.HashSet[Int]()
    for (x <- 0 until size) hs.add(rand.nextInt())
    hs
  }

  def newScalaIntSets = for {
    size <- count
  } yield {
    val hs = ru.softage.collection.mutable.LibTrieHashSet[Int]()
    for (x <- 0 until size) hs.add(rand.nextInt())
    hs
  }

  // TODO: add sets with float and string

  /* Tests for {Contains, Add, Remove} operations */
  performance of "HashSet" config(
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1,
    exec.jvmflags -> "-server -Xms3072m -Xmx3072m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=64m -XX:+UseCondCardMark -XX:CompileThreshold=100",
    reports.regression.noiseMagnitude -> 0.15
    ) in {

    measure method "Contains" in {

      using(scalaIntSets) curve "scala" in {
        set =>
          set.foreach(i => set.contains(i))
      }

      using(newScalaIntSets) curve "new_scala" in {
        set =>
          set.foreach(i => set.contains(i))
      }
    }

    measure method "Add" in {

      using(scalaIntSets) curve "scala" in {
        set =>
          set.foreach(i => set.add(i))
      }

      using(newScalaIntSets) curve "new_scala" in {
        set =>
          set.foreach(i => set.add(i))
      }

    }

    measure method "Remove" in {

      using(scalaIntSets) curve "scala" in {
        set =>
          set.foreach(i => set.remove(i))
      }

      using(newScalaIntSets) curve "new_scala" in {
        set =>
          set.foreach(i => set.remove(i))
      }
    }
  }
}