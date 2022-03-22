package model

import model.Currency.Currency

import java.time.LocalDateTime
import java.util.UUID

case class Offer(val id: UUID = UUID.randomUUID(),contributor: Contributor, rate: BigDecimal, currency: Currency, isBuying: Boolean, val date: LocalDateTime = LocalDateTime.now())
