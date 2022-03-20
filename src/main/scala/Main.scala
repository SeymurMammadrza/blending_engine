import OfferActor.{AddOffer, GetStateByCurrencyAndOperationType}
import akka.actor.typed.ActorSystem
import model.{Contributor, Currency, Offer}


object Main {
  def main(args: Array[String]): Unit = {


    val system = ActorSystem(OfferActor(), "offer-actor")

    val listOfOffers: LazyList[Offer] = LazyList(
      Offer(Contributor(name = "deutsche_bank"), 1.55, Currency.EUR, isBuying = true),
      Offer(Contributor(name = "deutsche_bank"), 1.58, Currency.EUR, isBuying = true),
      Offer(Contributor(name = "deutsche_bank"), 1.60, Currency.EUR, isBuying = true),
      Offer(Contributor(name = "deutsche_bank"), 1.64, Currency.EUR, isBuying = true),
      Offer(Contributor(name = "deutsche_bank"), 1.67, Currency.EUR, isBuying = true),
      Offer(Contributor(name = "deutsche_bank"), 1.67, Currency.EUR, isBuying = false),
      Offer(Contributor(name = "deutsche_bank"), 1.72, Currency.EUR, isBuying = false),
      Offer(Contributor(name = "deutsche_bank"), 1.78, Currency.USD, isBuying = false),
      Offer(Contributor(name = "deutsche_bank"), 1.72, Currency.USD, isBuying = false)
    )

    system ! AddOffer(listOfOffers)
    system ! GetStateByCurrencyAndOperationType(Currency.EUR, isBuying = false)

  }


}