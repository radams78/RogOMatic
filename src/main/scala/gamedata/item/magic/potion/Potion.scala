package gamedata.item.magic.potion

import domain.Domain
import gamedata.item.StackableMagicItemType
import gamedata.item.magic.potion.Colour.Colour
import gamedata.item.magic.potion.PotionPower.PotionPower


object PotionType extends StackableMagicItemType {
  override type Attribute = Colour

  override implicit def attributeDomain: Domain[Attribute] = Colour.domain

  override type Power = PotionPower

  override implicit def powerDomain: Domain[Power] = PotionPower.domain

  override def singular: String = "potion"

  override def plural: String = "potions"
}

object Potion {
  type Potion = PotionType.StackableMagicItem
  type PotionKnowledge = PotionType.MagicItemKnowledge

  implicit def domain: Domain[Potion] = PotionType.StackableMagicItem.domain

  def apply(quantity: Int, colour: Colour): Potion = PotionType.StackableMagicItem(Some(quantity), Some(colour), None)

  def apply(power: PotionPower): Potion = PotionType.StackableMagicItem(None, None, Some(power))

  val UNKNOWN: Potion = PotionType.StackableMagicItem(None, None, None)
}