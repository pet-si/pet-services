package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Setting
import ufsm.petsi.petservices.repository.DataResult

interface ISettingRepository {
    fun getSettingByName(settingName: String): DataResult<Setting>
    fun getAllSettings(): Flow<DataResult<List<Setting>>>
    suspend fun insertSetting(setting: Setting): DataResult<Boolean>
    suspend fun updateSetting(setting: Setting): DataResult<Boolean>
    suspend fun deleteSetting(settingName: String): DataResult<Boolean>
}

