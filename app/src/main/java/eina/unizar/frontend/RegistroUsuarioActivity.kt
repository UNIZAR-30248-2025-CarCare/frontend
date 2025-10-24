package eina.unizar.frontend

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import eina.unizar.frontend.models.Usuario
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext
import java.security.MessageDigest
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// Función para validar el email
fun esEmailValido(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return regex.matches(email)
}

// Función para validar la fecha y calcular la edad
fun obtenerEdad(fechaNacimiento: String): Int? {
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false // Solo acepta el formato exacto
        val fecha = formato.parse(fechaNacimiento)
        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance()
        nacimiento.time = fecha!!
        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }
        edad
    } catch (e: Exception) {
        null
    }
}


/**
 * Pantalla de registro de nuevos usuarios.
 *
 * - Permite introducir nombre, email, contraseña y fecha de nacimiento.
 * - Incluye checkbox para aceptar los términos y condiciones.
 * - Muestra un `DatePickerDialog` para seleccionar la fecha de nacimiento.
 *
 * Estados controlados con `remember` para cada campo.
 * Valida que todos los campos estén completos antes de enviar el formulario.
 *
 * Callbacks:
 * - `onBackClick()` → Regresa a la pantalla anterior.
 * - `onRegisterClick()` → Envía los datos de registro.
 * - `onLoginClick()` → Navega a la pantalla de inicio de sesión.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroUsuarioScreen(
    onBackClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Obtener el contexto para mostrar el DatePickerDialog
    val context = LocalContext.current

    // Crear calendario para la fecha actual
    val calendario = Calendar.getInstance()

    // Configurar el DatePickerDialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, año, mes, dia ->
            // Formato de la fecha seleccionada (DD/MM/AAAA)
            val fechaSeleccionada = String.format("%02d/%02d/%04d", dia, mes + 1, año)
            fechaNacimiento = fechaSeleccionada
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    // Configurar fecha máxima (hoy)
    datePickerDialog.datePicker.maxDate = calendario.timeInMillis

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear Cuenta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE53935)
                )
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cuéntanos un poco\nsobre ti",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campos de entrada
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                placeholder = { Text("Tu nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("ejemplo@email.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                placeholder = { Text("Clave") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = fechaNacimiento,
                onValueChange = { fechaNacimiento = it },
                label = { Text("Fecha de nacimiento") },
                placeholder = { Text("DD/MM/AAAA") },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { aceptoTerminos = !aceptoTerminos },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = aceptoTerminos,
                    onCheckedChange = { aceptoTerminos = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFE53935)
                    )
                )
                Text(
                    text = "Acepto los términos y condiciones",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            fun convertirFormatoFecha(fechaOriginal: String): String {
                try {
                    val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val fecha = formatoEntrada.parse(fechaOriginal)
                    return formatoSalida.format(fecha!!)
                } catch (e: Exception) {
                    Log.e("RegistroUsuario", "Error al convertir fecha: ${e.message}")
                    return fechaOriginal
                }
            }

            Button(
                onClick = {
                    errorMessage = null

                    if (nombre.isBlank() || email.isBlank() || password.isBlank() || fechaNacimiento.isBlank()) {
                        errorMessage = "Todos los campos son obligatorios"
                        return@Button
                    }
                    if (!esEmailValido(email)) {
                        errorMessage = "Formato de email incorrecto"
                        return@Button
                    }
                    if (password.length < 5) {
                        errorMessage = "La contraseña debe tener al menos 5 caracteres"
                        return@Button
                    }
                    val edad = obtenerEdad(fechaNacimiento)
                    if (edad == null) {
                        errorMessage = "Formato de fecha incorrecto (DD/MM/AAAA)"
                        return@Button
                    }
                    if (edad < 16) {
                        errorMessage = "Debes ser mayor de 16 años"
                        return@Button
                    }
                    if (!aceptoTerminos) {
                        errorMessage = "Debes aceptar los términos y condiciones"
                        return@Button
                    }
                    if (aceptoTerminos) {
                        val hashedPassword = hashPassword(password) // Hashear la contraseña
                        Log.d("RegistroUsuario", "Hashed Password: $hashedPassword") // Depuración
                        Log.d(
                            "RegistroUsuario",
                            "Fecha de Nacimiento: $fechaNacimiento"
                        ) // Depuración
                        val usuario = Usuario(
                            nombre = nombre,
                            email = email,
                            contraseña = hashedPassword, // Usar la contraseña hasheada
                            fecha_nacimiento = convertirFormatoFecha(fechaNacimiento)
                        )

                        scope.launch(Dispatchers.IO) {
                            RetrofitClient.instance.registrarUsuario(usuario)
                                .enqueue(object : Callback<Void> {
                                    override fun onResponse(
                                        call: Call<Void>,
                                        response: Response<Void>
                                    ) {
                                        if (response.isSuccessful) {
                                            scope.launch(Dispatchers.Main) {
                                                Toast.makeText(
                                                    context,
                                                    "Registro exitoso",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onRegisterClick()
                                            }
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                                ?: "Error desconocido"
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

                                    override fun onFailure(call: Call<Void>, t: Throwable) {
                                        scope.launch(Dispatchers.Main) {
                                            errorMessage = "Error de conexión: ${t.message}"
                                        }
                                    }
                                })
                        }
                    }
                },
                enabled = aceptoTerminos,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrarse", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 14.sp,
                    color = Color(0xFFE53935),
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}

