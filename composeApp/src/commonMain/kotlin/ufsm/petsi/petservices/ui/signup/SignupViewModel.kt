package ufsm.petsi.petservices.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.UserRepository
import ufsm.petsi.petservices.util.hash
import kotlin.time.ExperimentalTime

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(SignupState())
    var state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignupEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.ChangeName -> {
                _state.update { it.copy(name = intent.name, nameError = null) }
            }

            is SignupIntent.ChangeCompanyName -> {
                _state.update { it.copy(companyName = intent.companyName, companyNameError = null) }
            }

            is SignupIntent.ChangeEmail -> {
                _state.update { it.copy(email = intent.email, emailError = null) }
            }

            is SignupIntent.ChangePassword -> {
                _state.update { it.copy(password = intent.password, passwordError = null) }
            }

            is SignupIntent.ChangeConfirmPassword -> {
                _state.update { it.copy(confirmPassword = intent.confirmPassword, confirmPasswordError = null) }
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

    @OptIn(ExperimentalTime::class)
    private fun executeSignup() = viewModelScope.launch {

        if (!validateFields()) return@launch

        _state.update { it.copy(isLoading = true, signupError = null) }

        val hashedPassword = hash(_state.value.password)

        val user = User(
            name = _state.value.name,
            companyName = _state.value.companyName,
            email = _state.value.email,
            password = hashedPassword
        )

        when (val result = repository.insertUser(user)) {
            is DataResult.Error -> {
                _state.update { it.copy(signupError = result.message) }
            }
            DataResult.Loading -> TODO()
            is DataResult.Success -> {
                _effect.emit(SignupEffect.NavigateBack)
            }
        }
    }

    private fun validateFields() : Boolean{
        val state = _state.value
        var validity = true

        var nameError : String? = null
        var companyNameError : String? = null
        var emailError : String? = null
        var passwordError : String? = null
        var confirmPasswordError : String? = null

        if(state.name.isBlank()) {
            nameError = "Por favor insira seu nome"
            validity = false
        }

        if (state.companyName.isBlank()) {
            companyNameError = "Por favor insira o nome da empresa"
            validity = false
        }

        if(state.email.isBlank()) {
            emailError = "Por favor insira seu email"
            validity = false
        } else if (!state.email.contains("@")) {
            emailError = "Por favor insira um email válido"
            validity = false
        }

        if (state.password.isBlank()) {
            passwordError = "Por favor insira uma senha"
            validity = false
        } else if (state.password.length < 4) {
            passwordError = "A senha deve ter pelo menos 4 caracteres"
            validity = false
        }

        if (state.confirmPassword.isBlank()){
            confirmPasswordError = "Por favor confirme sua senha"
            validity = false
        }else if (state.password != state.confirmPassword) {
            confirmPasswordError = "As senhas não coincidem"
            validity = false
        }

        _state.update {
            it.copy(
                nameError = nameError,
                companyNameError = companyNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        return validity
    }
}