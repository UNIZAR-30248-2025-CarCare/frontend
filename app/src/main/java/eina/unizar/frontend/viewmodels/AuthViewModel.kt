package eina.unizar.frontend.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val sharedPrefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        cargarDatosAlmacenados()
    }

    private fun cargarDatosAlmacenados() {
        val storedUserId = sharedPrefs.getString("user_id", null)
        val storedToken = sharedPrefs.getString("token", null)

        if (storedUserId != null && storedToken != null) {
            _userId.value = storedUserId
            _token.value = storedToken
            Log.d("AuthViewModel", "Datos cargados desde SharedPreferences: userId=$storedUserId, token=$storedToken")
        }
    }

    fun saveLoginData(userId: String, token: String) {
        Log.d("AuthViewModel", "Guardando userId: $userId, token: $token")

        // Actualiza los flujos
        _userId.value = userId
        _token.value = token

        // Guarda en SharedPreferences
        sharedPrefs.edit().apply {
            putString("user_id", userId)
            putString("token", token)
            apply()
        }
    }

    fun logout() {
        _userId.value = null
        _token.value = null

        sharedPrefs.edit().clear().apply()
    }
}