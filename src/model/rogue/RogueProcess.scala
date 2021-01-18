package model.rogue

import com.jediterm.terminal.TerminalStarter
import com.jediterm.terminal.model.TerminalTextBuffer

import scala.concurrent.Future

class RogueProcess(screenReader: ScreenReader, starter : TerminalStarter, buffer: TerminalTextBuffer) {
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
