import OfferActor.{AddOffer, GetStateByCurrencyAndOperationType}
import akka.actor.typed.ActorSystem
import model.{Contributor, Currency, Offer}


object Main {
  def main(args: Array[String]): Unit = {


    val system = ActorSystem(OfferActor(), "offer-actor")

    val listOfOffers: LazyList[Offer] = LazyList(
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.55, currency = Currency.EUR, isBuying = true),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.58, currency =Currency.EUR, isBuying = true),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.60, currency =Currency.EUR, isBuying = true),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.64, currency =Currency.EUR, isBuying = true),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.67, currency =Currency.EUR, isBuying = true),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.67, currency =Currency.EUR, isBuying = false),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.72, currency =Currency.EUR, isBuying = false),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.78, currency =Currency.USD, isBuying = false),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.72, currency =Currency.USD, isBuying = false),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.82, currency =Currency.JPY, isBuying = false),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.85, currency =Currency.JPY, isBuying = false),
      Offer(contributor = Contributor(name = "deutsche_bank"), rate = 1.87, currency =Currency.JPY, isBuying = false)
    )

    system ! AddOffer(listOfOffers)
    system ! GetStateByCurrencyAndOperationType(Currency.EUR, isBuying = false)
    system ! GetStateByCurrencyAndOperationType(Currency.EUR, isBuying = true)
    system ! GetStateByCurrencyAndOperationType(Currency.USD, isBuying = false)
    system ! GetStateByCurrencyAndOperationType(Currency.JPY, isBuying = false)

  }


}