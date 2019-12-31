package gamedata

import gamedata.Domain._
import gamedata.ScrollPower.ScrollPower

case class ScrollKnowledge(private val powers: Map[String, ScrollPower]) {
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  def getPower(title: String): Option[ScrollPower] = powers.get(title)
}

object ScrollKnowledge {
  def apply(): ScrollKnowledge = new ScrollKnowledge(Map())

  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}