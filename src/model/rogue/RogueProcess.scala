package model.rogue

import com.jediterm.terminal.model.{StyleState, TerminalTextBuffer}

import java.nio.charset.Charset

class RogueProcess {
  val charset: Charset = RogueProcess.DEFAULT_CHARSET
  val state : StyleState = new StyleState
  val buffer: TerminalTextBuffer = new TerminalTextBuffer(80, 24, state)
}

object RogueProcess {
  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
}