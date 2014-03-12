package ru.softage.scalatest

import org.scalameter.Gen
import org.scalameter.api._
import scala.collection.mutable
import scala.util.Random

object HashSetBenchmark extends PerformanceTest {

  val rand: Random = new Random(1l)

  def generateDistinctRandomNums(size: Int): List[Int] = {
    val set = mutable.HashSet[Int]()
    while (set.size != size) set.add(rand.nextInt())
    rand.shuffle(set.toList)
  }

  /* Configuration */

  lazy val executor = LocalExecutor(//need to change to SeparateJvmsExecutor
    new Executor.Warmer.Default,
    Aggregator.min,
    new Measurer.Default)
  lazy val reporter = ChartReporter(ChartFactory.XYLine())
  lazy val persistor = Persistor.None

  /* Inputs */
  val MaxSize = 1000000
  val TestDataSize = 1000
  val count: Gen[Int] = Gen.range("size")(100000, MaxSize, 10000)
  val totalData = generateDistinctRandomNums(MaxSize + TestDataSize)
  val data = totalData.take(MaxSize)
  val notExistedTestData = totalData.takeRight(TestDataSize)

  val dataGen = for {
    size <- count
  } yield {
    val setData = data.take(size)
    val testData = rand.shuffle(setData).take(TestDataSize).toArray
    (setData, testData)
  }

  def scalaIntSets = for {
    (setData, testData) <- dataGen
  } yield {
    val set = mutable.HashSet[Int]() ++= setData
    (set, testData)
  }

  def immutableTrieBucketIntSets = for {
    (setData, testData) <- dataGen
  } yield {
    val set = ru.softage.collection.mutable.ImmutableTrieBucketHashSet[Int]() ++= setData
    (set, testData)
  }

  def listBucketIntSets = for {
    (setData, testData) <- dataGen
  } yield {
    val set = ru.softage.collection.mutable.ListBucketHashSet[Int]() ++= setData
    (set, testData)
  }

  def treeIntSets = for {
    (setData, testData) <- dataGen
  } yield {
    val set = mutable.TreeSet[Int]() ++= setData
    (set, testData)
  }

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
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(immutableTrieBucketIntSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(listBucketIntSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(treeIntSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }
    }

    measure method "ContainsNotExisted" in {

      using(scalaIntSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.contains(notExistedTestData(i))
            i += 1
          }
      }

      using(immutableTrieBucketIntSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.contains(notExistedTestData(i))
            i += 1
          }
      }

      using(listBucketIntSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.contains(notExistedTestData(i))
            i += 1
          }
      }

      using(treeIntSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.contains(notExistedTestData(i))
            i += 1
          }
      }
    }

    measure method "Add" in {

      using(scalaIntSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.add(notExistedTestData(i))
            i += 1
          }
      }

      using(immutableTrieBucketIntSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.add(notExistedTestData(i))
            i += 1
          }
      }

      using(listBucketIntSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.add(notExistedTestData(i))
            i += 1
          }
      }

      using(treeIntSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < notExistedTestData.length) {
            set.add(notExistedTestData(i))
            i += 1
          }
      }
    }

    measure method "Remove" in {

      using(scalaIntSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(immutableTrieBucketIntSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(listBucketIntSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(treeIntSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }
    }

  }
}