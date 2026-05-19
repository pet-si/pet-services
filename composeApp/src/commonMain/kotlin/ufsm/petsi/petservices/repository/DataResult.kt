package ufsm.petsi.petservices.repository


// Classe para tratamento de erros
sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val message : String, val exception : Throwable? = null) : DataResult<Nothing>()
    object Default : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()
}