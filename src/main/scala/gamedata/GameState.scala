package gamedata

import gamedata.Colour.Colour
import gamedata.Domain._
import gamedata.PotionPower.PotionPower
import gamedata.ScrollPower.ScrollPower
import rogue.Command

import scala.util.matching.Regex

/** Partial information about the state of the game */
class GameState(val scrollKnowledge: ScrollKnowledge = new ScrollKnowledge(Map()),
                val potionKnowledge: PotionKnowledge = new PotionKnowledge(Map()),
                val lastCommand: Option[Command] = None) {
  private def complete: Either[String, GameState] = lastCommand match {
    case None => Right(this)
    case Some(cmd) => for {
      sp <- scrollKnowledge.infer(cmd)
      pp <- potionKnowledge.infer(cmd)
      command <- cmd.infer(scrollKnowledge)
      command2 <- command.infer(potionKnowledge)
    } yield new GameState(sp, pp, Some(command2))
  }
}

object GameState {
  private val removeCurseMessage: Regex = """you feel as though someone is watching over you""".r.unanchored
  private val healingMessage: Regex = """you begin to feel better""".r.unanchored
  private val blankLine: String = " " * 80

  def interpretMessage(messageLine: String): GameState = messageLine match {
    case removeCurseMessage() => new GameState(new ScrollKnowledge(Map()), new PotionKnowledge(Map()), Some(Command.Read(None, Scroll(None, None, Some(ScrollPower.REMOVE_CURSE)))))
    case healingMessage() => new GameState(new ScrollKnowledge(Map()), new PotionKnowledge(Map()), Some(Command.Quaff(None, Potion(None, None, Some(PotionPower.HEALING)))))
    case blankLine => new GameState()
    case _ => throw new Error(s"Unrecognised message: $messageLine")
  }

  def infer(scrollPowers: Map[String, ScrollPower], scroll: Scroll): Either[String, Map[String, ScrollPower]] =
    (scroll.title, scroll.power) match {
      case (Some(t), Some(p)) => scrollPowers.get(t) match {
        case Some(pp) => for (power <- p.merge(pp)) yield scrollPowers.updated(t, power)
        case None => Right(scrollPowers + (t -> p))
      }
      case _ => Right(scrollPowers)
    }

  def infer(potionPowers: Map[Colour, PotionPower], command: Command): Either[String, Map[Colour, PotionPower]] = command match {
    case Command.Throw(_, _, potion: Potion) => infer(potionPowers, potion)
    case Command.Quaff(_, potion) => infer(potionPowers, potion)
    case _ => Right(potionPowers)
  }

  def infer(potionPowers: Map[Colour, PotionPower], potion: Potion): Either[String, Map[Colour, PotionPower]] =
    (potion.colour, potion.power) match {
      case (Some(c), Some(p)) => potionPowers.get(c) match {
        case Some(pp) => for (power <- p.merge(pp)) yield potionPowers.updated(c, power)
        case None => Right(potionPowers + (c -> p))
      }
      case _ => Right(potionPowers)
    }

  implicit def domain: Domain[GameState] = (x: GameState, y: GameState) => for {
    scrollKnowledge <- x.scrollKnowledge.merge(y.scrollKnowledge)
    potionKnowledge <- y.potionKnowledge.merge(y.potionKnowledge)
    lastCommand <- x.lastCommand.merge(y.lastCommand)
    gameState <- new GameState(scrollKnowledge, potionKnowledge, lastCommand).complete
  } yield gameState
}

class ScrollKnowledge(private val powers: Map[String, ScrollPower]) {
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  def getPower(title: String): Option[ScrollPower] = powers.get(title)

  def infer(command: Command): Either[String, ScrollKnowledge] = command match {
    case Command.Read(_, scroll) => infer(scroll)
    case Command.Throw(_, _, scroll: Scroll) => infer(scroll)
    case _ => Right(this)
  }


  def infer(scroll: Scroll): Either[String, ScrollKnowledge] = scroll.title match {
    case Some(t) => for {
      power <- powers.get(t).merge(scroll.power)
    } yield power match {
      case Some(p) => new ScrollKnowledge(powers.updated(t, p))
      case None => this
    }
    case None => Right(this)
  }
}

object ScrollKnowledge {
  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}

class PotionKnowledge(private val powers: Map[Colour, PotionPower]) {
  def getColour(p: PotionPower): Option[Colour] = powers.find(_._2 == p).map(_._1)

  def getPower(c: Colour): Option[PotionPower] = powers.get(c)

  def infer(cmd: Command): Either[String, PotionKnowledge] = cmd match {
    case Command.Quaff(_, potion) => infer(potion)
    case Command.Throw(_, _, potion: Potion) => infer(potion)
    case _ => Right(this)
  }

  def infer(potion: Potion): Either[String, PotionKnowledge] = potion.colour match {
    case Some(c) => for {power <- powers.get(c).merge(potion.power)} yield new PotionKnowledge(powers ++ power.map((c, _)))
    case None => Right(this)
  }
}

object PotionKnowledge {
  implicit def domain: Domain[PotionKnowledge] = (x: PotionKnowledge, y: PotionKnowledge) => x.powers.merge(y.powers).map(new PotionKnowledge(_))
}