package gamedata.items

import domain.Domain
import gamedata.items.ScrollPower.ScrollPower

object ScrollType extends MagicItemType {
  override type Attribute = String

  override implicit def attributeDomain: Domain[String] = Domain.stringDomain

  override type Power = ScrollPower

  override implicit def powerDomain: Domain[ScrollPower] = ScrollPower.scrollPowerDomain

  override def singular: String = "scroll"

  override def plural: String = "scrolls"
}

object Scroll {
  type Scroll = ScrollType.MagicItem

  def apply(quantity: Int, title: String, power: ScrollPower): Scroll =
    ScrollType.MagicItem(Some(quantity), Some(title), Some(power))

  def apply(quantity: Int, title: String): Scroll = ScrollType.MagicItem(Some(quantity), Some(title), None)

  def apply(power: ScrollPower): Scroll = ScrollType.MagicItem(None, None, Some(power))

  val UNKNOWN: Scroll = ScrollType.MagicItem(None, None, None) // TODO Duplication

  type ScrollKnowledge = ScrollType.MagicItemKnowledge

  implicit class IsScrollKnowledge(self: ScrollKnowledge) {
    def title: String = self.attribute
  }

  def ScrollKnowledge(title: String, power: ScrollPower): ScrollKnowledge = ScrollType.MagicItemKnowledge(title, power)
}