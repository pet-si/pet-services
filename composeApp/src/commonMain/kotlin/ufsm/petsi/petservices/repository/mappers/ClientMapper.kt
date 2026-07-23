package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.database.ClientEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun ClientEntity.toModel(): Client {
    return Client(
        idClient = idClient,
        idUser = idUser,
        name = name,
        cpf = cpf,
        email = email,
        phoneNumber = phoneNumber,
        points = points!!.toInt(),
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}
