package gamestate

import domain.Domain
import domain.Domain._
import gamedata.ProvidesKnowledge
import gamedata.items.ScrollPower.ScrollPower

/** Memory of which scroll titles map to which powers */
case class ScrollKnowledge(powers: Map[String, ScrollPower]) extends ProvidesKnowledge {
  override def scrollKnowledge: Either[String, ScrollKnowledge] = Right(this)

  // TODO Extract trait below
  def infer(item: ProvidesKnowledge): Either[String, ScrollKnowledge] = for {
    sk <- item.scrollKnowledge
    sk2 <- ScrollKnowledge.domain.merge(this, sk)
  } yield sk2


  /** Given a power, return the corresponding title, if known */
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  /** Given a title, return the corresponding power, if known */
  def getPower(title: String): Option[ScrollPower] = powers.get(title)
}

object ScrollKnowledge {
  def apply(): ScrollKnowledge = new ScrollKnowledge(Map())

  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}