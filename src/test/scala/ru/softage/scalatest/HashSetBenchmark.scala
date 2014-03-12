package ru.softage.scalatest

import org.scalameter.Gen
import org.scalameter.api._
import scala.collection.mutable
import scala.util.Random
import scala.io.Source

// copy-paste mostly, mb need to refactor
object HashSetBenchmark extends PerformanceTest {
  val rand: Random = new Random(1l)
  lazy val executor = LocalExecutor(//need to change to SeparateJvmsExecutor
    new Executor.Warmer.Default,
    Aggregator.min,
    new Measurer.Default)
  lazy val reporter = ChartReporter(ChartFactory.XYLine())
  lazy val persistor = Persistor.None

  object IntData {
    def generateDistinctRandomNums(size: Int): List[Int] = {
      val set = mutable.HashSet[Int]()
      while (set.size != size) set.add(rand.nextInt())
      rand.shuffle(set.toList)
    }

    val MaxSize = 1000000
    val TestDataSize = 1000
    val count: Gen[Int] = Gen.range("size")(100000, MaxSize, 20000)
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

    def scalaSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = mutable.HashSet[Int]() ++= setData
      (set, testData)
    }

    def immutableTrieBucketSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = ru.softage.collection.mutable.ImmutableTrieBucketHashSet[Int]() ++= setData
      (set, testData)
    }

    def listBucketSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = ru.softage.collection.mutable.ListBucketHashSet[Int]() ++= setData
      (set, testData)
    }

    def treeSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = mutable.TreeSet[Int]() ++= setData
      (set, testData)
    }
  }

  object StringData {
    val TestDataSize = 1000
    val NumOfDots = 50

    val totalData = Source.fromInputStream(getClass.getResourceAsStream("/identifiers.txt"), "UTF-8").getLines().toList
    val data = totalData.take(totalData.size - TestDataSize)
    val notExistedTestData = totalData.takeRight(TestDataSize)
    if (data.size < TestDataSize) throw new IllegalStateException("string test data is too small")
    val count: Gen[Int] = Gen.range("size")(TestDataSize, data.size, (data.size - TestDataSize) / NumOfDots)

    val dataGen = for {
      size <- count
    } yield {
      val setData = data.take(size)
      val testData = rand.shuffle(setData).take(TestDataSize).toArray
      (setData, testData)
    }

    def scalaSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = mutable.HashSet[String]() ++= setData
      (set, testData)
    }

    def immutableTrieBucketSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = ru.softage.collection.mutable.ImmutableTrieBucketHashSet[String]() ++= setData
      (set, testData)
    }

    def listBucketSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = ru.softage.collection.mutable.ListBucketHashSet[String]() ++= setData
      (set, testData)
    }

    def treeSets = for {
      (setData, testData) <- dataGen
    } yield {
      val set = mutable.TreeSet[String]() ++= setData
      (set, testData)
    }
  }

  performance of "HashSet" config(
    exec.minWarmupRuns -> 50,
    exec.maxWarmupRuns -> 100,
    exec.benchRuns -> 30,
    exec.independentSamples -> 1,
    exec.jvmflags -> "-server -Xms3072m -Xmx3072m -XX:MaxPermSize=256m -XX:ReservedCodeCacheSize=64m -XX:+UseCondCardMark -XX:CompileThreshold=100",
    reports.regression.noiseMagnitude -> 0.15
    ) in {

    measure method "ContainsInts" in {

      using(IntData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(IntData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(IntData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(IntData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }
    }

    measure method "ContainsStrings" in {

      using(StringData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(StringData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(StringData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }

      using(StringData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.contains(testData(i))
            i += 1
          }
      }
    }

    measure method "ContainsNotExistedInts" in {

      using(IntData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.contains(IntData.notExistedTestData(i))
            i += 1
          }
      }

      using(IntData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.contains(IntData.notExistedTestData(i))
            i += 1
          }
      }

      using(IntData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.contains(IntData.notExistedTestData(i))
            i += 1
          }
      }

      using(IntData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.contains(IntData.notExistedTestData(i))
            i += 1
          }
      }
    }

    measure method "ContainsNotExistedStrings" in {

      using(StringData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.contains(StringData.notExistedTestData(i))
            i += 1
          }
      }

      using(StringData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.contains(StringData.notExistedTestData(i))
            i += 1
          }
      }

      using(StringData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.contains(StringData.notExistedTestData(i))
            i += 1
          }
      }

      using(StringData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.contains(StringData.notExistedTestData(i))
            i += 1
          }
      }
    }

    measure method "AddInts" in {

      using(IntData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.add(IntData.notExistedTestData(i))
            i += 1
          }
      }

      using(IntData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.add(IntData.notExistedTestData(i))
            i += 1
          }
      }

      using(IntData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.add(IntData.notExistedTestData(i))
            i += 1
          }
      }

      using(IntData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < IntData.notExistedTestData.length) {
            set.add(IntData.notExistedTestData(i))
            i += 1
          }
      }
    }

    measure method "AddStrings" in {

      using(StringData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.add(StringData.notExistedTestData(i))
            i += 1
          }
      }

      using(StringData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.add(StringData.notExistedTestData(i))
            i += 1
          }
      }

      using(StringData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.add(StringData.notExistedTestData(i))
            i += 1
          }
      }

      using(StringData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < StringData.notExistedTestData.length) {
            set.add(StringData.notExistedTestData(i))
            i += 1
          }
      }
    }

    measure method "RemoveInts" in {

      using(IntData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(IntData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(IntData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(IntData.treeSets) curve "tree_set" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }
    }

    measure method "RemoveStrings" in {

      using(StringData.scalaSets) curve "scala" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(StringData.immutableTrieBucketSets) curve "new_immutable_trie_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(StringData.listBucketSets) curve "new_list_bucket" in {
        case (set, testData) =>
          var i = 0
          while (i < testData.length) {
            set.remove(testData(i))
            i += 1
          }
      }

      using(StringData.treeSets) curve "tree_set" in {
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