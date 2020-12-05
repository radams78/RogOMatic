package integration

/** A controller for Rog-O-Matic */
trait IController {
  def performCommand(command: Command): Unit
}
