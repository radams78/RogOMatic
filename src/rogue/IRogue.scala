package rogue

trait IRogue {
  def sendKeypress(keypress: Char): Unit

  def readScreen: Seq[String]
}
