package model.rogue

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.{CursorShape, RequestOrigin, TerminalDisplay, TerminalStarter, TtyBasedArrayDataStream}
import com.jediterm.terminal.emulator.mouse.MouseMode
import com.jediterm.terminal.model.JediTerminal.ResizeHandler
import com.jediterm.terminal.model.{JediTerminal, StyleState, TerminalSelection, TerminalTextBuffer}
import com.pty4j.PtyProcess
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.varia.NullAppender

import java.awt.Dimension
import java.nio.charset.Charset
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** Class for low-dungeonLevel communication with the RG process. A humble object. */
class Rogue private (screenReader : ScreenReader, rogueProcess : RogueProcess) extends IRogue {
    private val command: Array[String] = Rogue.DEFAULT_COMMAND
    private val env: java.util.Map[String, String] = Rogue.DEFAULT_ENVIRONMENT
    // Set up log4j
    BasicConfigurator.configure(new NullAppender)

    private val state: StyleState = new StyleState
    private val buffer: TerminalTextBuffer = new TerminalTextBuffer(80, 24, state)
    private val display: MinimalTerminalDisplay = new MinimalTerminalDisplay(buffer)
    private val terminal: JediTerminal = new JediTerminal(display, buffer, state)

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
      screenReader.notify(Screen.makeScreen(buffer.getScreenLines))
    }

    /** Send the given character to RG as input from the actuator, orElse pause until screen stops updating. */
    override def sendKeypress(keyPress: Char): Unit = {
      starter.sendBytes(Array(keyPress.toByte))
      //noinspection ZeroIndexToHead
      Iterator.continually({
        Thread.sleep(10)
        buffer.getScreenLines
      }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
      screenReader.notify(Screen.makeScreen(buffer.getScreenLines)) // TODO Duplication
    }

    /** Terminate the RG process and perform all necessary cleanup.  This method must be called before the end
     * of the application, or there may be a zombie RG process created. */
    def close(): Unit = connector.close()
}

  object Rogue {
    private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
    private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
    DEFAULT_ENVIRONMENT.put("TERM", "xterm")
    private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
    
    def apply(screenReader: ScreenReader): IRogue = new Rogue(screenReader, new RogueProcess)
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

    //noinspection ScalaDeprecation
    override def requestResize(pendingResize: Dimension, origin: RequestOrigin, cursorY: Int, resizeHandler: ResizeHandler): Dimension =
      throw new Error("Called requestResize on fixed size terminal display")

    override def setCursorShape(cursorShape: CursorShape): Unit = ()
  }
