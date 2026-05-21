package ufsm.petsi.petservices.models
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class User @OptIn(ExperimentalTime::class) constructor(
    val idUser: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val companyName: String,
    val password: String? = null,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false,
)
