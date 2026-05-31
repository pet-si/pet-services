package ufsm.petsi.petservices.repository

const val UNKNOWN_ERROR = "Ocorreu um erro inesperado."
fun notFoundError(name: String) : String {
    return "$name não encontrado."
}