package gamedata

import gamedata.Colour.Colour
import gamedata.Domain._
import gamedata.PotionPower.PotionPower

case class PotionKnowledge(private val powers: Map[Colour, PotionPower]) {
  def getColour(p: PotionPower): Option[Colour] = powers.find(_._2 == p).map(_._1)

  def getPower(c: Colour): Option[PotionPower] = powers.get(c)
}

object PotionKnowledge {
  def apply(): PotionKnowledge = new PotionKnowledge(Map())

  implicit def domain: Domain[PotionKnowledge] = (x: PotionKnowledge, y: PotionKnowledge) => x.powers.merge(y.powers).map(new PotionKnowledge(_))
}