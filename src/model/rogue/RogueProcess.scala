package model.rogue

import com.jediterm.terminal.model.StyleState

import java.nio.charset.Charset

class RogueProcess {
  val charset: Charset = RogueProcess.DEFAULT_CHARSET
  val state : StyleState = new StyleState
}

object RogueProcess {
  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
}