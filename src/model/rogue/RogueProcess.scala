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
import scala.concurrent.Future

class RogueProcess(screenReader: ScreenReader, pty: PtyProcess) {
  // Set up log4j
  BasicConfigurator.configure(new NullAppender)
  
  val charset: Charset = RogueProcess.DEFAULT_CHARSET
  val state: StyleState = new StyleState
  val buffer: TerminalTextBuffer = new TerminalTextBuffer(80, 24, state)
  val display: MinimalTerminalDisplay = new MinimalTerminalDisplay(buffer)
  val terminal: JediTerminal = new JediTerminal(display, buffer, state)
  val connector: PtyProcessTtyConnector = new PtyProcessTtyConnector(pty, charset)
  val starter: TerminalStarter = new TerminalStarter(terminal, connector, new TtyBasedArrayDataStream(connector))

  def startGame(): Unit = {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    Future {
      starter.start()
    }
    // Wait for RG to clear the message "just a moment while I dig the dungeon"
    // TODO More elegant way to do this?
    Thread.sleep(1000)
    screenReader.notify(Screen.makeScreen(buffer.getScreenLines))
  }

  def sendKeypress(keyPress: Char): Unit = {
    starter.sendBytes(Array(keyPress.toByte))
    //noinspection ZeroIndexToHead
    Iterator.continually({
      Thread.sleep(10)
      buffer.getScreenLines
    }).sliding(2).find((p: Seq[String]) => p(0) == p(1))
    screenReader.notify(Screen.makeScreen(buffer.getScreenLines)) // TODO Duplication
  }

  def close(): Unit = connector.close()
}

object RogueProcess {
  private val DEFAULT_CHARSET: Charset = Charset.forName("UTF-8")
}

class MinimalTerminalDisplay(buffer: TerminalTextBuffer) extends TerminalDisplay {
  private val selection: TerminalSelection = null

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
