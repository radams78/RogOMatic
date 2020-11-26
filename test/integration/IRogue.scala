package integration

trait IRogue {
  def readScreen : Seq[String]
  def sendKeypress(keypress : Char) : Unit
}
