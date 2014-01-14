package ru.softage.scalatest

import org.scalameter.{Gen, PerformanceTest}
import org.scalameter.api._
import scala.collection.mutable
import scala.collection.immutable.Set
import scala.util.Random

object HashSetBenchmark extends PerformanceTest {

  /* Configuration */

  lazy val executor = LocalExecutor( //need to change to SeparateJvmsExecutor
    new Executor.Warmer.Default,
    Aggregator.min,
    new Measurer.Default)
  lazy val reporter = ChartReporter(ChartFactory.XYLine())
  lazy val persistor = Persistor.None

  /* Inputs */

  val count: Gen[Int] = Gen.range("count")(100, 50000, 1000)
  val rand: Random = new Random(1l)

  def scalaSets = for {
    size <- count
  } yield {
    val hs = mutable.HashSet[Int]()
    for (x <- 0 until size) hs.add(rand.nextInt())
    hs
  }

  def newScalaSets = for {
    size <- count
  } yield {
    val hs = ru.softage.collection.mutable.HashSet[Int]()
    for (x <- 0 until size) hs.add(rand.nextInt())
    hs
  }

  /* Tests for {Contains, Add, Remove} operations */

  /* On standart scala HashSet implementation */
  performance of "scala" in {

    measure method "Contains" in {
      using(scalaSets) in {
        set => {
          set.foreach(
            i =>
              set.contains(i)
          )
        }
      }
    }

    measure method "Add" in {
      using(scalaSets) in {
        set => {
          set.foreach(
            i =>
              set.add(i)
          )
        }
      }
    }

    measure method "Remove" in {
      using(scalaSets) in {
        set => {
          set.foreach(
            i =>
              set.remove(i)
          )
        }
      }
    }

  }

  /* On Softage new experimental HashSet implementation */
  performance of "new_scala" in {

    measure method "Contains" in {
      using(newScalaSets) in {
        set => {
          set.foreach(
            i =>
              set.contains(i)
          )
        }
      }
    }

    measure method "Add" in {
      using(newScalaSets) in {
        set => {
          set.foreach(
            i =>
              set.add(i)
          )
        }
      }
    }

    measure method "Remove" in {
      using(newScalaSets) in {
        set => {
          set.foreach(
            i =>
              set.remove(i)
          )
        }
      }
    }

  }

}