package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.database.UserEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun UserEntity.toModel(): User {
    return User(
        idUser = idUser,
        name = name,
        email = email,
        companyName = companyName,
        password = password,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}