package gamedata

import gamestate.{PotionKnowledge, ScrollKnowledge}

trait ProvidesKnowledge { // TODO Not compositional
  def scrollKnowledge: Either[String, ScrollKnowledge] = Right(ScrollKnowledge()) // TODO Demand Right?

  def potionKnowledge: Either[String, PotionKnowledge] = Right(PotionKnowledge()) // TODO Demand Right?
}
