import akka.actor.typed.Behavior
import akka.event.slf4j.Logger
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import model.Currency.{Currency, DEFAULT}
import model.{Offer, Offers}

import java.util.UUID

object OfferActor {

  val logger = Logger("offer_actor")

  val offers: Offers.type = Offers

  sealed trait Command extends CborSerializable

  final case class AddOffer(offers: LazyList[Offer]) extends Command

  final case class GetStateByCurrencyAndOperationType(currency: Currency, isBuying: Boolean) extends Command

  sealed trait Event extends CborSerializable

  final case class StateAcquired(currency: Currency, isBuying: Boolean) extends Event

  final case class OfferAdded(offers: LazyList[Offer]) extends Event

  final case class State(summary: (Currency, BigDecimal, BigDecimal)) {

    val offers = Offers

    var listForOperations: List[Offer] = List.empty

    def addToOffers(newOffers: LazyList[Offer]): List[Offer] = {
      offers.addOffers(newOffers)
    }

    def addToState(newOffers: LazyList[Offer]): State = {
      listForOperations = addToOffers(newOffers)
      logger.info(s"this is list : $listForOperations")
      this

    }

    def filterOffers(list: Option[List[Offer]], currency: Option[Currency], isBuying: Option[Boolean]): List[Offer] = (list, currency, isBuying) match {
      case (Some(list: List[Offer]), Some(currency: Currency), Some(isBuying: Boolean)) =>
        list.filter(offer => offer.currency == currency && offer.isBuying == isBuying)
      case _ => List.empty

    }

    def averageRate(list: List[Offer], currency: Currency, isBuying: Boolean): BigDecimal = list.length match {
      case 0 => 0
      case _ =>
        val newList = filterOffers(Some(list), Some(currency), Some(isBuying))
        val mean = newList.map(offer => offer.rate).sum / newList.length
        mean
    }

    def latestRate(list: List[Offer], currency: Currency, isBuying: Boolean): BigDecimal = list.length match {
      case 0 => 0
      case _ =>
        val newList = filterOffers(Some(list), Some(currency), Some(isBuying))
        val latest = newList.last
        latest.rate
    }

  }

  val commandHandler: (State, Command) => Effect[Event, State] = { (state, command) =>
    command match {
      case AddOffer(offers) =>
        Effect.persist(OfferAdded(offers))
      case GetStateByCurrencyAndOperationType(currency, isBuying) => Effect.persist(StateAcquired(currency, isBuying))
    }
  }

  val eventHandler: (State, Event) => State = { (state, event) =>
    event match {
      case OfferAdded(offers) =>
        state.addToState(offers)
      case StateAcquired(currency, isBuying) =>
        val newState = state.copy(currency, state.averageRate(state.listForOperations, currency, isBuying), state.latestRate(state.listForOperations, currency, isBuying))
        logger.info(s"average rate for the currency : $currency is ${newState.summary._2} and latest rate is ${newState.summary._3} if it is  $isBuying for buying")
        state.copy(currency, state.averageRate(state.listForOperations, currency, isBuying), state.latestRate(state.listForOperations, currency, isBuying))
    }
  }

  def apply(): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = PersistenceId.ofUniqueId(UUID.randomUUID().toString),
      emptyState = State(Tuple3(DEFAULT, 0, 0)),
      commandHandler = commandHandler,
      eventHandler = eventHandler
    )
}
