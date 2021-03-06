package rogue

import java.awt.Dimension
import java.nio.charset.Charset

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal._
import com.jediterm.terminal.emulator.mouse.MouseMode
import com.jediterm.terminal.model.JediTerminal.ResizeHandler
import com.jediterm.terminal.model.{JediTerminal, StyleState, TerminalSelection, TerminalTextBuffer}
import com.pty4j.PtyProcess
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Class for low-level communication with the Rogue process. This class is part of the untested thin layer. */
class Rogue extends IRogue {

  private val command: Array[String] = Rogue.DEFAULT_COMMAND
  private val env: java.util.Map[String, String] = Rogue.DEFAULT_ENVIRONMENT
  private val charset: Charset = Rogue.DEFAULT_CHARSET
  // Set up log4j
  BasicConfigurator.configure(new NullAppender)

  private val state: StyleState = new StyleState
  private val buffer: TerminalTextBuffer = new TerminalTextBuffer(80, 24, state)
  private val display: MinimalTerminalDisplay = new MinimalTerminalDisplay(buffer)
  private val terminal: JediTerminal = new JediTerminal(display, buffer, state)

  private val pty: PtyProcess = PtyProcess.exec(command, env)
  private val connector: PtyProcessTtyConnector = new PtyProcessTtyConnector(pty, charset)
  private val starter: TerminalStarter =
    new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector))

  override def start(): Unit = {
    Future {
      starter.start()
    }
    // Wait for Rogue to clear the message "just a moment while I dig the dungeon"
    // TODO More elegant way to do this?
    Thread.sleep(1000)
  }

  /** Returns the current contents of the screen, as a single string with newlines (\n) between lines.
   * Every line is padded with spaces; thus, for example, a blank line will be represented as 80 space characters. */
  override def getScreen: String = buffer.getScreenLines

  /** Send the given character to Rogue as input from the actuator, then pause until screen stops updating. */
  override def sendKeypress(keyPress: Char): Unit = {
    starter.sendBytes(Array(keyPress.toByte))
    //noinspection ZeroIndexToHead
    Iterator.continually({
      Thread.sleep(10)
      buffer.getScreenLines
    }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
  }

  /** Terminate the Rogue process and perform all necessary cleanup.  This method must be called before the end
   * of the application, or there may be a zombie Rogue process created. */
  def close(): Unit = connector.close()
}

object Rogue {
  private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
  private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
  DEFAULT_ENVIRONMENT.put("TERM", "xterm")
  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
}

// A minimal implementation of TerminalDisplay
private class MinimalTerminalDisplay(buffer: TerminalTextBuffer) extends TerminalDisplay {
  private var selection: TerminalSelection = _

  override def beep(): Unit = ()

  override def setBlinkingCursor(b: Boolean): Unit = ()

  override def scrollArea(i: Int, i1: Int, i2: Int): Unit = ()

  override def setScrollingEnabled(b: Boolean): Unit = ()

  override def setCursorVisible(b: Boolean): Unit = ()

  override def getRowCount: Int = buffer.getHeight

  override def getColumnCount: Int = buffer.getWidth

  override def terminalMouseModeSet(mouseMode: MouseMode): Unit = ()

  override def ambiguousCharsAreDoubleWidth(): Boolean = false

  override def getSelection: TerminalSelection = selection

  override def setCurrentPath(s: String): Unit = ()

  override def setCursor(i: Int, i1: Int): Unit = ()

  override def setWindowTitle(s: String): Unit = ()

  override def requestResize(pendingResize: Dimension, origin: RequestOrigin, cursorY: Int, resizeHandler: ResizeHandler): Dimension =
    throw new Error("Called requestResize on fixed size terminal display")

  override def setCursorShape(cursorShape: CursorShape): Unit = ()
}