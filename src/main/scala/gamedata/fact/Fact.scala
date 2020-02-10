package gamedata.fact

import gamedata.fact.ProvidesKnowledge._
import gamedata.fact.UsesKnowledge._
import gamedata.pCommand

trait Fact {
  final def after(command: pCommand): Either[String, Set[Fact]] =
    for (c <- command.infer(this)) yield c.implications
}
