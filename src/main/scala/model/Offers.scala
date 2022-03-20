package model

object Offers {
  var list: LazyList[Offer] = LazyList.empty

  def addOffers(offers: LazyList[Offer]): List[Offer] = {
    (list concat offers).toList
  }

  def toList = {
    list.toList
  }

  def returnList = {
    this.list
  }
}