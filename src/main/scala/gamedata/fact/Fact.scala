package gamedata.fact

import gamedata.pCommand

trait Fact {
  def after(command: pCommand): Either[String, Set[Fact]]
}
