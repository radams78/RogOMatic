package expert

import gamedata.pCommand
import gamestate.History

/** An Expert is an object that takes the current state of the game, including its full history, and chooses the
 * next command to play. */
trait Expert {
  def advice(history: History.GameOn): pCommand
}
