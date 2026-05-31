package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.MaterialEntity
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.IMaterialRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError

class MaterialRepository(database: AppDatabase) : IMaterialRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getMaterialById(id: String): DataResult<Material> {
        return try {
            val material = selectQueries.selectMaterialById(id).executeAsOneOrNull()?.toModel()
            if (material != null) {
                DataResult.Success(material)
            } else {
                DataResult.Error(notFoundError("Material"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllMaterials(): Flow<DataResult<List<Material>>> {
        return selectQueries.selectAllMaterials()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<MaterialEntity>, DataResult<List<Material>>> { list -> DataResult.Success(list.map { it.toModel() }) }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertMaterial(material: Material) : DataResult<Boolean> {
        return try {
            insertQueries.insertMaterial(
                idMaterial = material.idMaterial,
                name = material.name,
                quantity = material.quantity.toLong(),
                costPrice = material.costPrice,
                minimumStock = material.minimumStock.toLong(),
                metric = material.metric
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun updateMaterial(material: Material) : DataResult<Boolean> {
        return try {
            updateQueries.updateMaterial(
                name = material.name,
                quantity = material.quantity.toLong(),
                costPrice = material.costPrice,
                minimumStock = material.minimumStock.toLong(),
                metric = material.metric,
                idMaterial = material.idMaterial,
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun deleteMaterial(id: String) : DataResult<Boolean> {
        return try {
            deleteQueries.softDeleteMaterial(id)
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}

