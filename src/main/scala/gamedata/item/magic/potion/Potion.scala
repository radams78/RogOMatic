package gamedata.item.magic.potion

import domain.{Domain, pLift}
import gamedata.item.magic.StackableMagicItemType
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

  def apply(quantity: Int, colour: Colour): Potion = PotionType.StackableMagicItem(pLift.Known(quantity), pLift.Known(colour), pLift.UNKNOWN)

  def apply(power: PotionPower): Potion = PotionType.StackableMagicItem(pLift.UNKNOWN, pLift.UNKNOWN, pLift.Known(power))

  val UNKNOWN: Potion = PotionType.StackableMagicItem(pLift.UNKNOWN, pLift.UNKNOWN, pLift.UNKNOWN)
}