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


/**
 * ViewModel para gestionar la autenticación y sesión del usuario.
 * 
 * Extiende AndroidViewModel para tener acceso al contexto de la aplicación
 * y poder utilizar SharedPreferences para persistencia de datos de sesión.
 * 
 * Responsabilidades:
 * - Almacenar y recuperar el token JWT y el ID de usuario
 * - Persistir la sesión usando SharedPreferences
 * - Proporcionar información de autenticación a toda la aplicación
 * - Gestionar el cierre de sesión
 * 
 * @param application Contexto de la aplicación para acceder a SharedPreferences
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Flow privado mutable para el ID del usuario autenticado.
     */
    private val _userId = MutableStateFlow<String?>(null)

    /**
     * StateFlow público del ID de usuario.
     * Las pantallas pueden observar este valor para reaccionar a cambios de autenticación.
     */
    val userId: StateFlow<String?> = _userId.asStateFlow()

    /**
     * Flow privado mutable para el token de autenticación JWT.
     */
    private val _token = MutableStateFlow<String?>(null)

    /**
     * StateFlow público del token de autenticación.
     * Se utiliza para autenticar peticiones al backend.
     */
    val token: StateFlow<String?> = _token.asStateFlow()

    /**
     * Instancia de SharedPreferences para persistir datos de autenticación
     * entre sesiones de la aplicación.
     */
    private val sharedPrefs = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        cargarDatosAlmacenados()
    }

    /**
     * Carga los datos de autenticación almacenados en SharedPreferences.
     * 
     * Se ejecuta automáticamente al inicializar el ViewModel para
     * restaurar la sesión si existe. Si encuentra datos válidos,
     * actualiza los StateFlows correspondientes.
     */
    private fun cargarDatosAlmacenados() {
        val storedUserId = sharedPrefs.getString("user_id", null)
        val storedToken = sharedPrefs.getString("token", null)

        if (storedUserId != null && storedToken != null) {
            _userId.value = storedUserId
            _token.value = storedToken
            Log.d("AuthViewModel", "Datos cargados desde SharedPreferences: userId=$storedUserId, token=$storedToken")
        }
    }

    /**
     * Guarda los datos de inicio de sesión del usuario.
     * 
     * Actualiza los StateFlows y persiste los datos en SharedPreferences
     * para mantener la sesión activa entre reinicios de la aplicación.
     * 
     * @param userId Identificador único del usuario autenticado
     * @param token Token JWT para autenticar futuras peticiones
     */
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

    /**
     * Cierra la sesión del usuario actual.
     * 
     * Limpia los StateFlows y elimina todos los datos almacenados
     * en SharedPreferences, efectivamente cerrando la sesión.
     */
    fun logout() {
        _userId.value = null
        _token.value = null

        sharedPrefs.edit().clear().apply()
    }
}