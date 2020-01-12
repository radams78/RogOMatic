package gamedata.items

import domain.Domain
import gamedata.items.ScrollPower.ScrollPower

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

  val UNKNOWN: Scroll = ScrollType.StackableMagicItem(None, None, None)

  def apply(power: ScrollPower): Scroll = ScrollType.StackableMagicItem(None, None, Some(power))

  def apply(quantity: Int, title: String): Scroll = ScrollType.StackableMagicItem(Some(quantity), Some(title), None)

  def apply(quantity: Int, title: String, power: ScrollPower): Scroll =
    ScrollType.StackableMagicItem(Some(quantity), Some(title), Some(power))

  type ScrollKnowledge = ScrollType.MagicItemKnowledge

  implicit class IsScrollKnowledge(self: ScrollKnowledge) {
    def title: String = self.attribute
  }

  def ScrollKnowledge(title: String, power: ScrollPower): ScrollKnowledge = ScrollType.MagicItemKnowledge(title, power)
}