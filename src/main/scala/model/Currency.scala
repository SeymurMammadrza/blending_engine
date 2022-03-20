package model

object Currency extends Enumeration {
  type Currency = Value
  val USD, EUR, GBP, JPY, CHF, CNY, DEFAULT = Value
}
