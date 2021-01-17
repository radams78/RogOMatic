package model.rogue

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal._
import com.jediterm.terminal.model.JediTerminal
import com.pty4j.PtyProcess
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Class for low-dungeonLevel communication with the RG process. A humble object. */
class Rogue private (screenReader : ScreenReader, rogueProcess : RogueProcess) extends IRogue {
    private val command: Array[String] = Rogue.DEFAULT_COMMAND
    private val env: java.util.Map[String, String] = Rogue.DEFAULT_ENVIRONMENT
    // Set up log4j
    BasicConfigurator.configure(new NullAppender)

  private val terminal: JediTerminal = new JediTerminal(rogueProcess.display, rogueProcess.buffer, rogueProcess.state)

    private val pty: PtyProcess = PtyProcess.exec(command, env)
    private val connector: PtyProcessTtyConnector = new PtyProcessTtyConnector(pty, rogueProcess.charset)
    private val starter: TerminalStarter =
      new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector))

    override def startGame(): Unit = {
      Future {
        starter.start()
      }
      // Wait for RG to clear the message "just a moment while I dig the dungeon"
      // TODO More elegant way to do this?
      Thread.sleep(1000)
      screenReader.notify(Screen.makeScreen(rogueProcess.buffer.getScreenLines))
    }

    /** Send the given character to RG as input from the actuator, orElse pause until screen stops updating. */
    override def sendKeypress(keyPress: Char): Unit = {
      starter.sendBytes(Array(keyPress.toByte))
      //noinspection ZeroIndexToHead
      Iterator.continually({
        Thread.sleep(10)
        rogueProcess.buffer.getScreenLines
      }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
      screenReader.notify(Screen.makeScreen(rogueProcess.buffer.getScreenLines)) // TODO Duplication
    }

    /** Terminate the RG process and perform all necessary cleanup.  This method must be called before the end
     * of the application, or there may be a zombie RG process created. */
    def close(): Unit = connector.close()
}

  object Rogue {
    private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
    private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
    DEFAULT_ENVIRONMENT.put("TERM", "xterm")

    def apply(screenReader: ScreenReader): IRogue = new Rogue(screenReader, new RogueProcess)
  }
