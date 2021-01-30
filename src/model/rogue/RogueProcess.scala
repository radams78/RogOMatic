package model.rogue

import scala.concurrent.Future

trait Starter {
  def start(): Unit
  def sendBytes(bytes: Array[Byte]): Unit
}

trait Buffer {
  def getScreenLines : String
}

class RogueProcess(starter : Starter, buffer: Buffer) extends IRogue {
  val screenReader : ScreenReader = null
  
  def startGame(): Unit = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    Future {
      starter.start()
    }
    // Wait for RG to clear the message "just a moment while I dig the dungeon"
    // TODO More elegant way to do this?
    Thread.sleep(1000)
    screenReader.notify(Screen.makeScreen(buffer.getScreenLines))
  }
  
  def sendKeypress(keyPress: Char): Unit = {
    starter.sendBytes(Array(keyPress.toByte))
    //noinspection ZeroIndexToHead
    Iterator.continually({
      Thread.sleep(10)
      buffer.getScreenLines
    }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
    screenReader.notify(Screen.makeScreen(buffer.getScreenLines)) // TODO Duplication
  }
}

object RogueProcess {
  def apply(rogue : Rogue): RogueProcess = new RogueProcess(rogue.starter, rogue.buffer)
}