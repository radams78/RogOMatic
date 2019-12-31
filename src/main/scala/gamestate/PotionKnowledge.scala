package gamestate

import domain.Domain
import domain.Domain._
import gamedata.items.Colour.Colour
import gamedata.items.PotionPower.PotionPower

/** Memory of which potion colours correspond to which powers */
case class PotionKnowledge(private val powers: Map[Colour, PotionPower]) {
  /** Given a power, return the corresponding colour, if known */
  def getColour(p: PotionPower): Option[Colour] = powers.find(_._2 == p).map(_._1)

  /** Given a colour, return the corresponding power, if any */
  def getPower(c: Colour): Option[PotionPower] = powers.get(c)
}

object PotionKnowledge {
  def apply(): PotionKnowledge = new PotionKnowledge(Map())

  implicit def domain: Domain[PotionKnowledge] = (x: PotionKnowledge, y: PotionKnowledge) => x.powers.merge(y.powers).map(new PotionKnowledge(_))
}