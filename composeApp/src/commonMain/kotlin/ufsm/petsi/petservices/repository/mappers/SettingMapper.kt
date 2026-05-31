package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.database.SettingsEntity
import ufsm.petsi.petservices.models.Setting

fun SettingsEntity.toModel(): Setting {
    return Setting(
        settingName = settingName,
        value = value_ ?: true
    )
}


