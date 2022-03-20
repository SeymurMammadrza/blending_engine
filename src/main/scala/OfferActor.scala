import akka.actor.typed.Behavior
import akka.event.slf4j.Logger
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import model.Currency.{Currency, DEFAULT}
import model.Offer

import java.util.UUID

object OfferActor {

  val logger = Logger("offer_actor")

  sealed trait Command extends CborSerializable

  final case class AddOffer(offers: LazyList[Offer]) extends Command

  final case class GetStateByCurrencyAndOperationType(currency: Currency, isBuying: Boolean) extends Command

  sealed trait Event extends CborSerializable

  final case class StateAcquired(currency: Currency, isBuying: Boolean) extends Event

  final case class OfferAdded(offers: LazyList[Offer]) extends Event

  final case class State(summary: List[(LazyList[Offer], Currency, BigDecimal, BigDecimal)]) {

    def addToState(newOffers: LazyList[Offer]): State = {
      copy(summary = List((newOffers, DEFAULT, 0, 0)))
    }

    def returnLazyList: LazyList[Offer] = {
      val list = this.summary.head._1
      list
    }

    def calculateAverageAndLatestOffers(currency: Currency, isBuying: Boolean): State = {
      copy(summary = List((returnLazyList, currency, averageRate(currency, isBuying), latestRate(currency, isBuying))))
    }

    def filterOffers(currency: Option[Currency], isBuying: Option[Boolean]): List[Offer] = (currency, isBuying) match {
      case (Some(currency: Currency), Some(isBuying: Boolean)) =>
        val list = summary.flatMap(tuple => tuple._1.filter(offer => offer.currency == currency && offer.isBuying == isBuying))
        list
      case _ => List.empty

    }

    def averageRate(currency: Currency, isBuying: Boolean): BigDecimal = summary.flatMap(lazyList => lazyList._1).length match {
      case 0 => 0
      case _ =>
        val newList = filterOffers(Some(currency), Some(isBuying))
        val mean = newList.map(offer => offer.rate).sum / newList.length
        mean
    }

    def latestRate(currency: Currency, isBuying: Boolean): BigDecimal = summary.flatMap(lazyList => lazyList._1).length match {
      case 0 => 0
      case _ =>
        val newList = filterOffers(Some(currency), Some(isBuying))
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
        val newState = state.calculateAverageAndLatestOffers(currency, isBuying)
        logger.info(s"average rate for the currency : $currency is ${newState.summary.head._3} and latest rate is ${newState.summary.head._4} if it is  $isBuying for buying")
        state.calculateAverageAndLatestOffers(currency, isBuying)
    }
  }

  def apply(): Behavior[Command] =
    EventSourcedBehavior[Command, Event, State](
      persistenceId = PersistenceId.ofUniqueId(UUID.randomUUID().toString),
      emptyState = State(List.empty),
      commandHandler = commandHandler,
      eventHandler = eventHandler
    )
}
