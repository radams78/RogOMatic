package model.rogue

import java.nio.charset.Charset

class RogueProcess {
  val charset: Charset = RogueProcess.DEFAULT_CHARSET
}

object RogueProcess {
  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
}