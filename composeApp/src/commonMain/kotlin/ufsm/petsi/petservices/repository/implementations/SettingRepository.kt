package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.SettingsEntity
import ufsm.petsi.petservices.models.Setting
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.ISettingRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError

class SettingRepository(database: AppDatabase) : ISettingRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getSettingByName(settingName: String): DataResult<Setting> {
        return try {
            val setting = selectQueries.selectSettingByName(settingName).executeAsOneOrNull()?.toModel()
            if (setting != null) {
                DataResult.Success(setting)
            } else {
                DataResult.Error(notFoundError("Configuração"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllSettings(): Flow<DataResult<List<Setting>>> {
        return selectQueries.selectAllSettings()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<SettingsEntity>, DataResult<List<Setting>>> { list -> DataResult.Success(list.map { it.toModel() }) }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertSetting(setting: Setting): DataResult<Boolean> {
        return try {
            insertQueries.insertSetting(
                settingName = setting.settingName,
                value_ = setting.value
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun updateSetting(setting: Setting): DataResult<Boolean> {
        return try {
            updateQueries.updateSetting(
                value_ = setting.value,
                settingName = setting.settingName
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun deleteSetting(settingName: String): DataResult<Boolean> {
        return try {
            deleteQueries.deleteSetting(settingName)
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}


