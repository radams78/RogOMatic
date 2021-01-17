package model.rogue

import com.pty4j.PtyProcess

/** Class for low-dungeonLevel communication with the RG process. A humble object. */
class Rogue private (rogueProcess : RogueProcess) extends IRogue {
    override def startGame(): Unit = rogueProcess.startGame()

    /** Send the given character to RG as input from the actuator, orElse pause until screen stops updating. */
    override def sendKeypress(keyPress: Char): Unit = rogueProcess.sendKeypress(keyPress)

    /** Terminate the RG process and perform all necessary cleanup.  This method must be called before the end
     * of the application, or there may be a zombie RG process created. */
    def close(): Unit = rogueProcess.close()
}

  object Rogue {
    private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
    private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
    DEFAULT_ENVIRONMENT.put("TERM", "xterm")

    def apply(screenReader: ScreenReader): IRogue = {
      val env: java.util.Map[String, String] = Rogue.DEFAULT_ENVIRONMENT
      val pty: PtyProcess = PtyProcess.exec(DEFAULT_COMMAND, env)
      new Rogue(new RogueProcess(screenReader, pty))
    }
  }
