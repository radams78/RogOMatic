package model.rogue

import com.pty4j.PtyProcess
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender

/** Class for low-dungeonLevel communication with the RG process. A humble object. */
class Rogue private (screenReader : ScreenReader, rogueProcess : RogueProcess) extends IRogue {
    // Set up log4j
    BasicConfigurator.configure(new NullAppender)

    override def startGame(): Unit = rogueProcess.startGame()

    /** Send the given character to RG as input from the actuator, orElse pause until screen stops updating. */
    override def sendKeypress(keyPress: Char): Unit = {
      rogueProcess.starter.sendBytes(Array(keyPress.toByte))
      //noinspection ZeroIndexToHead
      Iterator.continually({
        Thread.sleep(10)
        rogueProcess.buffer.getScreenLines
      }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
      screenReader.notify(Screen.makeScreen(rogueProcess.buffer.getScreenLines)) // TODO Duplication
    }

    /** Terminate the RG process and perform all necessary cleanup.  This method must be called before the end
     * of the application, or there may be a zombie RG process created. */
    def close(): Unit = rogueProcess.connector.close()
}

  object Rogue {
    private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
    private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
    DEFAULT_ENVIRONMENT.put("TERM", "xterm")

    def apply(screenReader: ScreenReader): IRogue = {
      val command: Array[String] = Rogue.DEFAULT_COMMAND
      val env: java.util.Map[String, String] = Rogue.DEFAULT_ENVIRONMENT
      val pty: PtyProcess = PtyProcess.exec(command, env)
      new Rogue(screenReader, new RogueProcess(screenReader, pty))
    }
  }
