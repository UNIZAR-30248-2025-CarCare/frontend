package eina.unizar.frontend

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import java.security.MessageDigest
import eina.unizar.frontend.models.Usuario
import eina.unizar.frontend.models.LoginRequest
import eina.unizar.frontend.models.LoginResponse
import eina.unizar.frontend.network.RetrofitClient
import eina.unizar.frontend.viewmodels.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EleccionInicioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaEleccionInicio()
        }
    }
}

/**
 * Pantalla inicial de la aplicación donde el usuario elige entre:
 * - Iniciar sesión
 * - Registrarse
 * - Recuperar contraseña
 *
 * Contiene campos controlados (`email`, `password`) y callbacks
 * para manejar cada acción de navegación.
 *
 * Interactúa con `AuthViewModel` para validar las credenciales.
 */

@SuppressLint("ContextCastToActivity")
@Composable
fun PantallaEleccionInicio(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(viewModelStoreOwner = LocalContext.current as ComponentActivity)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.carcare_logo),
                contentDescription = "Logo CarCare",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )

            // Título
            Text(
                text = "Bienvenido a",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C)
            )

            Text(
                text = "CarCare",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935) // Rojo
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Gestiona tu vehículo compartido",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de texto
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Olvidé mi contraseña?",
                fontSize = 14.sp,
                color = Color(0xFFE53935),
                modifier = Modifier.clickable { onForgotPasswordClick() },
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(24.dp))

            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            var errorMessage by remember { mutableStateOf<String?>(null) }

            Button(
                onClick = {
                    val hashedPassword = hashPassword(password)
                    Log.d("EleccionInicio", "Hashed Password: $hashedPassword") // Depuración
                    val loginRequest = LoginRequest(email = email, contraseña = hashedPassword)

                    scope.launch(Dispatchers.IO) {
                        RetrofitClient.instance.iniciarSesion(loginRequest)
                            .enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                    if (response.isSuccessful) {
                                        val loginResponse = response.body()
                                        scope.launch(Dispatchers.Main) {
                                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                            if (loginResponse != null) {
                                                authViewModel.saveLoginData(loginResponse.userId, loginResponse.token)
                                            }
                                            Log.d("EleccionInicio", "UserID: ${loginResponse?.userId}, Token: ${loginResponse?.token}") // Depuración
                                            // Navegar a la pantalla principal
                                            onLoginClick()
                                        }
                                    } else {
                                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                                        scope.launch(Dispatchers.Main) {
                                            errorMessage = extractErrorMessage(errorBody)
                                        }
                                    }
                                }

                                // Función para extraer solo el mensaje de error
                                private fun extractErrorMessage(errorBody: String): String {
                                    // Intentar extraer el mensaje entre comillas después de "error":"
                                    val regex = "\"error\":\"(.*?)\"".toRegex()
                                    val matchResult = regex.find(errorBody)
                                    return matchResult?.groupValues?.getOrNull(1) ?: errorBody
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    scope.launch(Dispatchers.Main) {
                                        errorMessage = "Error de conexión: ${t.message}"
                                    }
                                }
                            })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Iniciar Sesión", color = Color.White, fontSize = 16.sp)
            }

            // Mostrar mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DividerWithText()

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Registrarse
            OutlinedButton(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, Color(0xFFE53935)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse", color = Color(0xFFE53935), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Eres nuevo por aquí?\nCrea tu cuenta y empieza a gestionar tu vehículo compartido",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
    @Composable
    fun DividerWithText() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("  o  ", fontSize = 14.sp, color = Color.Gray)
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
    }