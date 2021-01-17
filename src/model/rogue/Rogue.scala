package model.rogue

import com.pty4j.PtyProcess

  object Rogue {
    private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
    private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
    DEFAULT_ENVIRONMENT.put("TERM", "xterm")

    def apply(screenReader: ScreenReader): IRogue = {
      val pty: PtyProcess = PtyProcess.exec(DEFAULT_COMMAND, DEFAULT_ENVIRONMENT)
      new RogueProcess(screenReader, pty)
    }
  }
