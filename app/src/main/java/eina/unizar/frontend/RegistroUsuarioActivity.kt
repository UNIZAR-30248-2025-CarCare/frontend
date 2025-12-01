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
    Log.d("HashPassword", "Password hashed to: ${bytes.joinToString("") { "%02x".format(it) }}")
    return bytes.joinToString("") { "%02x".format(it) }
}

fun esEmailValido(email: String): Boolean {
    val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    return regex.matches(email)
}

fun obtenerEdad(fechaNacimiento: String): Int? {
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false
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

    val context = LocalContext.current
    val calendario = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, año, mes, dia ->
            val fechaSeleccionada = String.format("%02d/%02d/%04d", dia, mes + 1, año)
            fechaNacimiento = fechaSeleccionada
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.maxDate = calendario.timeInMillis

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Crear Cuenta",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                placeholder = { Text("Tu nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("ejemplo@email.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                placeholder = { Text("Clave") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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
                            contentDescription = "Seleccionar fecha",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Acepto los términos y condiciones",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
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
                        val hashedPassword = hashPassword(password)
                        Log.d("RegistroUsuario", "Hashed Password: $hashedPassword")
                        Log.d(
                            "RegistroUsuario",
                            "Fecha de Nacimiento: $fechaNacimiento"
                        )
                        val usuario = Usuario(
                            nombre = nombre,
                            email = email,
                            contraseña = hashedPassword,
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

                                    private fun extractErrorMessage(errorBody: String): String {
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
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Registrarse", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}