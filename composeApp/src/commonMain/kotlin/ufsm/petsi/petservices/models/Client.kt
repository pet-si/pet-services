package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Client @OptIn(ExperimentalTime::class) constructor(
    val idClient: String,
    val name: String,
    val cpf: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val points: Int = 0,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

