package rogue

import gamedata.{Report, pCommand}

/** Interface for the actuator that performs high-level communication with the game of Rogue. */
trait IRogueActuator {
  /** Send a command to Rogue, then read all possible information from the screen and inventory. Returns Right(())
   * if all information successfully read, Left(error message) if we read information incompatible with information
   * already known. */
  def sendCommand(getCommand: pCommand): Either[String, Report]

  /** Start the game of Rogue and read the first screen and inventory. Returns Right(()) if all information successfully
   * read, Left(error message) if we read information incompatible with information already known. */
  def start(): Either[String, Report.GameOn]
}
