package ufsm.petsi.petservices.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager {
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    fun logoutUser() {
        _currentUserId.value = null
    }

    fun loginUser(userId : String) {
        _currentUserId.value = userId
    }
}