package model.rogue

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.{TerminalStarter, TtyBasedArrayDataStream}
import com.jediterm.terminal.model.{JediTerminal, StyleState, TerminalTextBuffer}
import com.pty4j.PtyProcess
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender

import java.nio.charset.Charset

// TODO Close?
class Rogue(screenReader: ScreenReader) extends IRogue {
  // Set up log4j
  BasicConfigurator.configure(new NullAppender)

  val charset: Charset = Rogue.DEFAULT_CHARSET
  val state: StyleState = new StyleState
  val buffer: TerminalTextBuffer = new TerminalTextBuffer(80, 24, state)
  val display: MinimalTerminalDisplay = new MinimalTerminalDisplay(buffer)
  val terminal: JediTerminal = new JediTerminal(display, buffer, state)
  val pty: PtyProcess = PtyProcess.exec(Rogue.DEFAULT_COMMAND, Rogue.DEFAULT_ENVIRONMENT)
  val connector: PtyProcessTtyConnector = new PtyProcessTtyConnector(pty, charset)
  val starter: TerminalStarter = new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector))

  val process: RogueProcess2 = new RogueProcess2(screenReader, starter, buffer)
  
  override def startGame(): Unit = process.startGame()

  override def sendKeypress(keyPress: Char): Unit = process.sendKeypress(keyPress)
}

/** Start an instance of Rogue running.
 * 
 * This starts Rogue running in a separate process and wraps it in a [[RogueProcess]] object. This is a humble object. */
object Rogue {
  // Command to launch the Rogue process
  private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
  
  // Environment for the Rogue process
  private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
  DEFAULT_ENVIRONMENT.put("TERM", "xterm")

  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
}
