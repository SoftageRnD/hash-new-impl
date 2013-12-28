package ru.softage.collection.mutable

import org.scalatest.FunSuite

class HashSetSuite extends FunSuite {
  test("An empty Set should have size 0") {
    assert(HashSet.empty.size == 0)
  }
}
