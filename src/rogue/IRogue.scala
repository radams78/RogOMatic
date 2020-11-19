package rogue

trait IRogue {

  def getScreen: Seq[String]

  def sendKeypress(keyPress: Char): Unit
}
