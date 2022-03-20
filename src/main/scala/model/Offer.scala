package model

import model.Currency.Currency

import java.time.LocalDateTime

case class Offer(contributor: Contributor, rate: BigDecimal, currency: Currency, isBuying: Boolean, val date: LocalDateTime = LocalDateTime.now())
