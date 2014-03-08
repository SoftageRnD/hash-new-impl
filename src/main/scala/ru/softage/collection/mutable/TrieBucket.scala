package ru.softage.collection.mutable

import scala.Array
import ru.softage.collection.mutable.TrieBucket.Trie


class TrieBucket {
  private val root = new Trie()
  private var totalSize = 0

  def add(elem: AnyRef): Boolean = ???

  def addWithoutCheck(elem: AnyRef) = ???

  def contains(elem: AnyRef): Boolean = ???

  def remove(elem: AnyRef): Boolean = ???

  def getSingleValue: AnyRef = ???

  def iterator: Iterator[AnyRef] = ???

  def foreach[U](f: AnyRef => U) = ???

  def size: Int = totalSize

}

object TrieBucket {

  class Trie {
    val table = new Array[AnyRef](TrieBucket.TrieSize)
  }

  private val TrieSize = 32

  // TODO: не учитывает последние два бита хэша
  private def index(level: Int, hashCode: Int): Int = (hashCode >>> (27 - 5 * level)) & 0x01f
}
