package ufsm.petsi.petservices.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.UserRepository
import ufsm.petsi.petservices.util.hash

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(SignupState())
    var state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignupEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.ChangeName -> {
                _state.update { it.copy(name = intent.name) }
            }

            is SignupIntent.ChangeCompanyName -> {
                _state.update { it.copy(companyName = intent.companyName) }
            }

            is SignupIntent.ChangeEmail -> {
                _state.update { it.copy(email = intent.email) }
            }

            is SignupIntent.ChangePassword -> {
                _state.update { it.copy(password = intent.password) }
            }

            is SignupIntent.ChangeConfirmPassword -> {
                _state.update { it.copy(confirmPassword = intent.confirmPassword) }
            }

            SignupIntent.CancelClicked -> {
                viewModelScope.launch {
                    _effect.emit(SignupEffect.NavigateBack)
                }
            }

            SignupIntent.ConfirmClicked -> {
                executeSignup()
            }
        }
    }

    private fun executeSignup() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        val hashedPassword = hash(_state.value.password)

        val user = User(
            name = _state.value.name,
            companyName = _state.value.companyName,
            email = _state.value.email,
            password = hashedPassword
        )

        repository.insertUser(user)
        _effect.emit(SignupEffect.NavigateBack)
    }
}