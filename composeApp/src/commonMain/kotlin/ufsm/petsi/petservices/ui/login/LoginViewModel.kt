package ufsm.petsi.petservices.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.UserRepository
import ufsm.petsi.petservices.session.SessionManager
import ufsm.petsi.petservices.util.hash
import java.security.MessageDigest
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LoginViewModel(private val repository: UserRepository, private val sessionManager: SessionManager) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    var state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.ChangeLogin -> {
                _state.update { it.copy(email = intent.email) }
            }

            is LoginIntent.ChangePassword -> {
                _state.update { it.copy(password = intent.password) }
            }

            LoginIntent.LoginClicked -> {
                executeLogin()
            }

            LoginIntent.SignupClicked -> {
                viewModelScope.launch {
                    _effect.emit(LoginEffect.NavigateToSignup)
                }
            }
        }
    }

    private fun executeLogin() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        val hashedPassword = hash(_state.value.password)

        when (val result = repository.loginUser(_state.value.email, hashedPassword)) {
            is DataResult.Error -> {
                _state.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
            DataResult.Loading -> {
                _state.update { it.copy(isLoading = true) }
            }
            is DataResult.Success -> {
                _effect.emit(LoginEffect.NavigateToHome)
                sessionManager.loginUser(result.data.idUser)
            }
        }
    }
}