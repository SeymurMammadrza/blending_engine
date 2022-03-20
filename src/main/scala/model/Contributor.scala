package model

import java.util.UUID

case class Contributor(val id: UUID = UUID.randomUUID(), name: String)
