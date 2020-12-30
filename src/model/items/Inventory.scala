package model.items

case class Inventory(items : Map[Slot, Item], wearing: Slot, wielding: Slot) {

}
