package integration

trait IController {
  def performCommand(command: Command): Unit
}
