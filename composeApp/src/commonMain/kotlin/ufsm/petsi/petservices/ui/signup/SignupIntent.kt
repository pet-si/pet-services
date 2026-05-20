package ufsm.petsi.petservices.ui.signup


// Intenções da UI, o que o usuário pode interagir
sealed interface SignupIntent {
    data class ChangeName(val name : String) : SignupIntent
    data class ChangeCompanyName(val companyName : String) : SignupIntent
    data class ChangeEmail(val email : String) : SignupIntent
    data class ChangePassword(val password : String) : SignupIntent
    data class ChangeConfirmPassword(val confirmPassword : String) : SignupIntent
    data object ConfirmClicked : SignupIntent
    data object CancelClicked : SignupIntent
}

// Estado da UI, campos de texto e/ou informações necessárias
data class SignupState (
    val name: String = "",
    val companyName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

// Efeitos únicos, notificação ou navegação, algo que ocorre apenas uma vez na tela
sealed interface SignupEffect {
    data object NavigateBack : SignupEffect
    data class ShowToast(val message : String) : SignupEffect
}