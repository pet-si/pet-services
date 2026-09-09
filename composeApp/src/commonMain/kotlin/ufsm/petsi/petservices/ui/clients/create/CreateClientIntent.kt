package ufsm.petsi.petservices.ui.clients.create

sealed interface CreateClientIntent {
    data class NameChanged(val name: String) : CreateClientIntent
    data class CpfChanged(val cpf: String) : CreateClientIntent
    data class EmailChanged(val email: String) : CreateClientIntent
    data class PhoneChanged(val phone: String) : CreateClientIntent
    data object Cancel : CreateClientIntent
    data object SaveClient : CreateClientIntent
}

data class CreateClientState(
    val clientId: String = "",
    val name: String = "",
    val cpf: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val nameError: String? = null,
)

sealed interface CreateClientEffects {
    data object NavigateBack : CreateClientEffects
    data class ShowMessage(val message: String) : CreateClientEffects
}