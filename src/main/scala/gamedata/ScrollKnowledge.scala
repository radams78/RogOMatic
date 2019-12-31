package gamedata

import gamedata.Domain._
import gamedata.ScrollPower.ScrollPower

/** Memory of which scroll titles map to which powers */
case class ScrollKnowledge(private val powers: Map[String, ScrollPower]) {
  /** Given a power, return the corresponding title, if known */
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  /** Given a title, return the corresponding power, if known */
  def getPower(title: String): Option[ScrollPower] = powers.get(title)
}

object ScrollKnowledge {
  def apply(): ScrollKnowledge = new ScrollKnowledge(Map())

  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}