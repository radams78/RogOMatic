package model.rogue

import com.pty4j.PtyProcess

/** Start an instance of Rogue running.
 * 
 * This starts Rogue running in a separate process and wraps it in a [[RogueProcess]] object. This is a humble object. */
object Rogue {
  // Command to launch the Rogue process
  private val DEFAULT_COMMAND: Array[String] = Array("/usr/games/rogue")
  
  // Environment for the Rogue process
  private val DEFAULT_ENVIRONMENT: java.util.Map[String, String] = new java.util.HashMap[String, String]
  DEFAULT_ENVIRONMENT.put("TERM", "xterm")

  /** Start a Rogue process running and wrap it in a [[RogueProcess]] object that notifies the given screenReader
   * every time the screen updates. */
  def apply(screenReader: ScreenReader): IRogue = {
    val pty: PtyProcess = PtyProcess.exec(DEFAULT_COMMAND, DEFAULT_ENVIRONMENT)
    new RogueProcess(screenReader, pty)
  }
}
