package ufsm.petsi.petservices.ui.login


// Intenções da UI, o que o usuário pode interagir
sealed interface LoginIntent {
    data class ChangeLogin(val email : String) : LoginIntent
    data class ChangePassword(val password : String) : LoginIntent
    data object LoginClicked : LoginIntent
    data object SignupClicked : LoginIntent
}

// Estado da UI, campos de texto e/ou informações necessárias
data class LoginState (
    val email : String = "",
    val password : String = "",
    val isLoading : Boolean = false,
    val errorMessage : String? = null
)

// Efeitos únicos, notificação ou navegação, algo que ocorre apenas uma vez na tela
sealed interface LoginEffect {
    data object NavigateToSignup : LoginEffect
    data object NavigateToHome : LoginEffect
    data class ShowToast(val message : String) : LoginEffect
}