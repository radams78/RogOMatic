package model

import gamedata.Command

/** Interface for sending commands to Rogue */
trait IRoguePlayer {
  /** Send the given command to Rogue
   *
   * @param command Command to be sent to Rogue */
  def performCommand(command: Command): Unit
}
