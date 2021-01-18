package model.rogue

import com.jediterm.terminal.model.TerminalTextBuffer
import com.jediterm.terminal.{TerminalOutputStream, TerminalStarter}

import scala.concurrent.Future

class RogueProcess2(screenReader: ScreenReader, starter : TerminalStarter, buffer: TerminalTextBuffer) {
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
}
