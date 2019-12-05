package gamedata

import scala.util.matching.Regex

/** An item that the PC can pick up */
class Item

object Item {
  private val rationRegex: Regex = """(\d+) rations of food""".r

  /** Given a description from a displayed inventory, return the corresponding [[Item]] */
  def parse(description: String): Item = description match {
    case "some food" => Food(1)
    case rationRegex(quantity) => Food(quantity.toInt)
  }
}
