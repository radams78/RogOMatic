package model.rogue

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.emulator.mouse.MouseMode
import com.jediterm.terminal.model.JediTerminal.ResizeHandler
import com.jediterm.terminal.model.{JediTerminal, StyleState, TerminalSelection, TerminalTextBuffer}
import com.jediterm.terminal._
import com.pty4j.PtyProcess
import _root_.model.rogue.Rogue.MinimalTerminalDisplay
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender

import java.awt.{Dimension, Point}
import java.nio.charset.Charset
import scala.concurrent.Future

// TODO Close?
/** Start an instance of Rogue running.
 *
 * This starts Rogue running in a separate process. This is a humble object. */
class Rogue private () extends IRogue {
  // Set up log4j
  BasicConfigurator.configure(new NullAppender)

  private var _screenObserver : Option[IScreenObserver] = None
  
  private val state: StyleState = new StyleState
  private val terminalTextBuffer: TerminalTextBuffer = new TerminalTextBuffer(80, 24, state)
  private val terminal: JediTerminal = new JediTerminal(new MinimalTerminalDisplay(terminalTextBuffer), terminalTextBuffer, state)
  private val pty: PtyProcess = PtyProcess.exec(Rogue.DEFAULT_COMMAND, Rogue.DEFAULT_ENVIRONMENT)
  private val connector: PtyProcessTtyConnector = new PtyProcessTtyConnector(pty, Rogue.DEFAULT_CHARSET)
  private val terminalStarter: TerminalStarter = new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector))

  override def startGame() : Unit = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    Future {
      terminalStarter.start()
    }
    // Wait for RG to clear the message "just a moment while I dig the dungeon"
    // TODO More elegant way to do this?
    Thread.sleep(1000)
    for (observer <- _screenObserver) observer.notify(Screen.makeScreen(terminalTextBuffer.getScreenLines))
  }

  override def sendKeypress(keyPress: Char): Unit = {
    terminalStarter.sendBytes(Array(keyPress.toByte))
    //noinspection ZeroIndexToHead
    Iterator.continually({
      Thread.sleep(10)
      terminalTextBuffer.getScreenLines
    }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
    for (observer <- _screenObserver) observer.notify(Screen.makeScreen(terminalTextBuffer.getScreenLines))
  }

  override def addScreenObserver(observer: IScreenObserver): Unit = _screenObserver = Some(observer)
}

object Rogue {
  def apply(screenObserver: IScreenObserver) : IRogue = new Rogue()
  
  // Command to launch the Rogue process
  private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
  
  // Environment for the Rogue process
  private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
  DEFAULT_ENVIRONMENT.put("TERM", "xterm")

  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")

  // A minimal implementation of TerminalDisplay
  private class MinimalTerminalDisplay(buffer: TerminalTextBuffer) extends TerminalDisplay {
    override def beep(): Unit = ()

    override def setBlinkingCursor(b: Boolean): Unit = ()

    override def scrollArea(i: Int, i1: Int, i2: Int): Unit = ()

    override def setScrollingEnabled(b: Boolean): Unit = ()

    override def setCursorVisible(b: Boolean): Unit = ()

    override def getRowCount: Int = buffer.getHeight

    override def getColumnCount: Int = buffer.getWidth

    override def terminalMouseModeSet(mouseMode: MouseMode): Unit = ()

    override def ambiguousCharsAreDoubleWidth(): Boolean = false

    override def getSelection: TerminalSelection = new TerminalSelection(new Point())

    override def setCurrentPath(s: String): Unit = ()

    override def setCursor(i: Int, i1: Int): Unit = ()

    override def setWindowTitle(s: String): Unit = ()

    //noinspection ScalaDeprecation
    override def requestResize(pendingResize: Dimension, origin: RequestOrigin, cursorY: Int, resizeHandler: ResizeHandler): Dimension =
      throw new Error("Called requestResize on fixed size terminal display")

    override def setCursorShape(cursorShape: CursorShape): Unit = ()
  }
}
