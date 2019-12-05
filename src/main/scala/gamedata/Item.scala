package gamedata

import scala.util.matching.Regex

/** An item that the PC can pick up */
class Item

object Item {
  private val rationRegex: Regex = """(\d+) rations of food""".r
  private val armorRegex: Regex = """([+-]\d+) (\w+( \w+)?) \[\d+]""".r

  /** Given a description from a displayed inventory, return the corresponding [[Item]] */
  def parse(description: String): Item = description match {
    case "some food" => Food(1)
    case rationRegex(quantity) => Food(quantity.toInt)
    case armorRegex(bonus, armorType) => Armor(ArmorType.parse(armorType), bonus.toInt)
    case "+1 ring mail [4]" => Armor(ArmorType.RING_MAIL, +1)
  }
}
