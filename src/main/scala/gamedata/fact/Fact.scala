package gamedata.fact

import gamedata.fact.ProvidesKnowledge._
import gamedata.fact.UsesKnowledge._
import gamedata.pCommand

trait Fact {
  /** If this is true before command is performed, then every member of this.after(command) is true after command
   * is performed */
  final def after(command: pCommand): Either[String, Set[Fact]] =
    for (c <- command.infer(this)) yield c.implications
}
