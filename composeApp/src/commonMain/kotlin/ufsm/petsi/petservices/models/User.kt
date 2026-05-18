package ufsm.petsi.petservices.models
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User (
    val idUser: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val companyName: String,
    val password: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false,
)