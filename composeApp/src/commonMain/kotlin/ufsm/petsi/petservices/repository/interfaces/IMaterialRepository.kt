package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.repository.DataResult

interface IMaterialRepository {
    fun getMaterialById(id: String): DataResult<Material>
    fun getAllMaterials(): Flow<DataResult<List<Material>>>
    suspend fun insertMaterial(material: Material) : DataResult<Boolean>
    suspend fun updateMaterial(material: Material) : DataResult<Boolean>
    suspend fun deleteMaterial(id: String) : DataResult<Boolean>
}

