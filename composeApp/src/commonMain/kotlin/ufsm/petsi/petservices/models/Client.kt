package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val idClient: String,
    val name: String,
    val cpf: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val points: Int = 0,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

