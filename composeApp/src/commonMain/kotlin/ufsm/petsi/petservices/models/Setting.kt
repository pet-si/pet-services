package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable

@Serializable
data class Setting(
    val settingName: String,
    val value: Boolean = true
)

