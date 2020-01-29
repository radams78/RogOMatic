package gamedata.item.magic.scroll

import domain.{Domain, pLift}
import gamedata.item.magic.StackableMagicItemType
import gamedata.item.magic.scroll.ScrollPower.ScrollPower

/** The magic scrolls */

object ScrollType extends StackableMagicItemType {
  /** For a scroll, the attribute is the title, which is just a string */
  override type Attribute = String

  override implicit def attributeDomain: Domain[String] = Domain.stringDomain

  override type Power = ScrollPower

  override implicit def powerDomain: Domain[ScrollPower] = ScrollPower.scrollPowerDomain

  override def singular: String = "scroll"

  override def plural: String = "scrolls"
}

object Scroll {
  type Scroll = ScrollType.StackableMagicItem

  val UNKNOWN: Scroll = ScrollType.StackableMagicItem(pLift.UNKNOWN, pLift.UNKNOWN, pLift.UNKNOWN)

  def apply(power: ScrollPower): Scroll = ScrollType.StackableMagicItem(pLift.UNKNOWN, pLift.UNKNOWN, pLift.Known(power))

  def apply(quantity: Int, title: String): Scroll = ScrollType.StackableMagicItem(pLift.Known(quantity), pLift.Known(title), pLift.UNKNOWN)

  def apply(quantity: Int, power: ScrollPower): Scroll = ScrollType.StackableMagicItem(pLift.Known(quantity), pLift.UNKNOWN, pLift.Known(power))

  def apply(quantity: Int, title: String, power: ScrollPower): Scroll =
    ScrollType.StackableMagicItem(pLift.Known(quantity), pLift.Known(title), pLift.Known(power))

  type ScrollKnowledge = ScrollType.MagicItemKnowledge

  implicit class IsScrollKnowledge(self: ScrollKnowledge) {
    def title: String = self.attribute
  }

  def ScrollKnowledge(title: String, power: ScrollPower): ScrollKnowledge = ScrollType.MagicItemKnowledge(title, power)
}