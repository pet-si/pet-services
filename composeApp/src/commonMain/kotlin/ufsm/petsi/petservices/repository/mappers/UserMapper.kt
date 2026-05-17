package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.User
import kotlinx.datetime.Instant
import ufsm.petsi.petservices.database.User as UserEntity

fun UserEntity.toModel(): User {
    return User(
        idUser = idUser,
        name = name,
        email = email,
        companyName = companyName,
        password = password,
        updatedAt = Instant.parse(updatedAt),
        deleted = deleted
    )
}