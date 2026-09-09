package ufsm.petsi.petservices.ui.clients.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ClientRepository
import kotlin.time.ExperimentalTime

class CreateClientViewModel(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateClientState())
    val state = _state.asStateFlow()

    private val _effects = Channel<CreateClientEffects>()
    val effects = _effects.receiveAsFlow()

    fun handleIntent(intent: CreateClientIntent) {
        when (intent) {
            is CreateClientIntent.NameChanged ->
                _state.update { it.copy(name = intent.name, nameError = null) }

            is CreateClientIntent.CpfChanged ->
                _state.update { it.copy(cpf = intent.cpf) }

            is CreateClientIntent.EmailChanged ->
                _state.update { it.copy(email = intent.email) }

            is CreateClientIntent.PhoneChanged ->
                _state.update { it.copy(phoneNumber = intent.phone) }

            CreateClientIntent.Cancel -> {
                viewModelScope.launch {
                    _effects.send(CreateClientEffects.NavigateBack)
                }
            }

            CreateClientIntent.SaveClient ->
                saveClient()
        }
    }

    fun getClient(clientId: String) {
        when (val result = clientRepository.getClientById(clientId)) {
            is DataResult.Success -> {
                _state.update {
                    it.copy(
                        clientId = clientId,
                        name = result.data.name,
                        cpf = result.data.cpf.orEmpty(),
                        email = result.data.email.orEmpty(),
                        phoneNumber = result.data.phoneNumber.orEmpty()
                    )
                }
            }

            is DataResult.Loading -> { /* no-op */ }

            is DataResult.Error -> {
                viewModelScope.launch {
                    _effects.send(CreateClientEffects.ShowMessage(result.message))
                }
            }
        }
    }

    fun resetForCreate() {
        _state.value = CreateClientState()
    }

    @OptIn(ExperimentalTime::class)
    private fun saveClient() = viewModelScope.launch {
        if (!validateFields()) return@launch

        val state = _state.value
        val client = Client(
            idClient = state.clientId.ifEmpty { java.util.UUID.randomUUID().toString() },
            name = state.name.trim(),
            cpf = state.cpf.trim().ifBlank { null },
            email = state.email.trim().ifBlank { null },
            phoneNumber = state.phoneNumber.trim().ifBlank { null }
        )

        val result = if (state.clientId.isNotEmpty()) {
            clientRepository.updateClient(client)
        } else {
            clientRepository.insertClient(client)
        }

        when (result) {
            is DataResult.Success -> {
                _state.value = CreateClientState()
                _effects.send(CreateClientEffects.NavigateBack)
            }
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> _effects.send(CreateClientEffects.ShowMessage(result.message))
        }
    }

    private fun validateFields(): Boolean {
        val state = _state.value
        val nameError = if (state.name.isBlank()) "Nome é obrigatório" else null

        _state.update { it.copy(nameError = nameError) }

        return nameError == null
    }
}