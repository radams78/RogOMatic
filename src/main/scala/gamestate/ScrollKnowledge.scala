package gamestate

import domain.Domain
import domain.Domain._
import gamedata.items.ScrollPower.ScrollPower
import gamedata.items.{Item, Scroll}
import gamedata.pInventory
import rogue.Command

/** Memory of which scroll titles map to which powers */
case class ScrollKnowledge(powers: Map[String, ScrollPower]) {
  def infer(command: Command): Either[String, ScrollKnowledge] = ScrollKnowledge.domain.merge(this, command.scrollKnowledge)

  def infer(inventory: pInventory): Either[String, ScrollKnowledge] = inventory.items.values.flatten.foldLeft[Either[String, ScrollKnowledge]](Right(this))(
    { case (sk: Either[String, ScrollKnowledge], item: Item) => for {
      s <- sk
      ss <- s.infer(item)
    } yield ss
    })

  def infer(item: Item): Either[String, ScrollKnowledge] = item match {
    case scroll: Scroll => infer(scroll)
    case _ => Right(this)
  }

  def infer(scroll: Scroll): Either[String, ScrollKnowledge] = scroll match {
    case Scroll(_, Some(title), Some(power)) => getPower(title) match {
      case None => getTitle(power) match {
        case None => Right(ScrollKnowledge(powers.updated(title, power)))
        case Some(t) => Left(s"Incompatible information: scroll $t and $title are both $power")
      }
      case Some(p) => if (p == power) Right(this) else Left(s"Incompatible information: scroll $title is $p and $power")
    }
    case _ => Right(this)
  }

  /** Given a power, return the corresponding title, if known */
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  /** Given a title, return the corresponding power, if known */
  def getPower(title: String): Option[ScrollPower] = powers.get(title)
}

object ScrollKnowledge {
  def apply(): ScrollKnowledge = new ScrollKnowledge(Map())

  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}