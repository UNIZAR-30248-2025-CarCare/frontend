package eina.unizar.frontend

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eina.unizar.frontend.models.RegistrarVehiculoRequest
import eina.unizar.frontend.models.Ubicacion
import eina.unizar.frontend.viewmodels.VehiculoViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import java.util.Calendar

/**
 * Pantalla para añadir un nuevo vehículo al sistema.
 *
 * - Permite introducir los datos del vehículo: nombre, fabricante, modelo, matrícula,
 *   año, tipo, combustible, capacidad de depósito y consumo medio.
 * - Valida los campos mostrando errores si están vacíos o contienen valores incorrectos.
 * - Usa `VehiculoViewModel` para enviar los datos al backend.
 *
 * Interfaz:
 * - Fondo gris claro con cabecera roja (`Surface`).
 * - Campos de texto controlados con estado `remember`.
 * - Selector desplegable para tipo de combustible.
 *
 * Callbacks:
 * - `onBackClick()` → Regresa a la pantalla anterior.
 * - `onAddClick()` → Envía los datos y crea el nuevo vehículo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehiculoScreen(
    userId: String,
    token: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
) {
    var nombreVehiculo by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf(TipoVehiculo.COCHE) }
    var fabricante by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var combustible by remember { mutableStateOf("Gasolina") }
    var capacidadDeposito by remember { mutableStateOf("") }
    var consumoMedio by remember { mutableStateOf("") }
    var expandedCombustible by remember { mutableStateOf(false) }

    val combustibles = listOf("Gasolina", "Diésel", "Eléctrico", "Híbrido", "GLP")

    val viewModel: VehiculoViewModel = viewModel()
    val token = token
    val usuarioId = userId.toInt()

    var nombreError by remember { mutableStateOf(false) }
    var fabricanteError by remember { mutableStateOf(false) }
    var modeloError by remember { mutableStateOf(false) }
    var matriculaError by remember { mutableStateOf(false) }
    var anioError by remember { mutableStateOf(false) }
    var capacidadError by remember { mutableStateOf(false) }
    var consumoError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEF4444)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Añadir Vehículo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Datos del vehículo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tipo de vehículo
            Text(
                text = "Tipo de vehículo",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TipoVehiculoOption(
                    tipo = TipoVehiculo.COCHE,
                    selected = tipoSeleccionado == TipoVehiculo.COCHE,
                    onClick = { tipoSeleccionado = TipoVehiculo.COCHE },
                    modifier = Modifier.weight(1f)
                )
                TipoVehiculoOption(
                    tipo = TipoVehiculo.MOTO,
                    selected = tipoSeleccionado == TipoVehiculo.MOTO,
                    onClick = { tipoSeleccionado = TipoVehiculo.MOTO },
                    modifier = Modifier.weight(1f)
                )
                TipoVehiculoOption(
                    tipo = TipoVehiculo.FURGONETA,
                    selected = tipoSeleccionado == TipoVehiculo.FURGONETA,
                    onClick = { tipoSeleccionado = TipoVehiculo.FURGONETA },
                    modifier = Modifier.weight(1f)
                )
                TipoVehiculoOption(
                    tipo = TipoVehiculo.CAMION,
                    selected = tipoSeleccionado == TipoVehiculo.CAMION,
                    onClick = { tipoSeleccionado = TipoVehiculo.CAMION },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nombre del vehículo",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = nombreVehiculo,
                onValueChange = {
                    nombreVehiculo = it
                    nombreError = false
                },
                isError = nombreError,
                placeholder = { Text("Ej: Mi coche, Furgoneta de trabajo...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (nombreError) Color.Red else Color(0xFFEF4444),
                    unfocusedBorderColor = if (nombreError) Color.Red else Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            // Fabricante
            Text(
                text = "Fabricante",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = fabricante,
                onValueChange = { fabricante = it
                                fabricanteError = false },
                    isError = fabricanteError,
                placeholder = { Text("Ej: Seat, Toyota, Ford...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (fabricanteError) Color.Red else Color(0xFFEF4444),
                    unfocusedBorderColor = if (fabricanteError) Color.Red else Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Modelo
            Text(
                text = "Modelo",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it
                                modeloError = false },
                isError = modeloError,
                placeholder = { Text("Ej: Ibiza, Corolla...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (modeloError) Color.Red else Color(0xFFEF4444),
                    unfocusedBorderColor = if (modeloError) Color.Red else Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Matrícula y Año
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Matrícula",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = matricula,
                        onValueChange = { matricula = it
                                        matriculaError = false },
                        isError = matriculaError,
                        placeholder = { Text("1234 BBC") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (matriculaError) Color.Red else Color(0xFFEF4444),
                            unfocusedBorderColor = if (matriculaError) Color.Red else Color(0xFFE5E7EB)
                        )
                    )
                    if (matriculaError) {
                        Text(
                            text = "Matrícula inválida",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Año",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = anio,
                        onValueChange = { anio = it
                                        anioError = false },
                        isError = anioError,
                        placeholder = { Text("2020") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (anioError) Color.Red else Color(0xFFEF4444),
                            unfocusedBorderColor = if (anioError) Color.Red else Color(0xFFE5E7EB)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (anioError) {
                        Text(
                            text = "Año inválido",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Combustible y Depósito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tipo de combustible",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedCombustible,
                        onExpandedChange = { expandedCombustible = it }
                    ) {
                        OutlinedTextField(
                            value = combustible,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCombustible)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFEF4444),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCombustible,
                            onDismissRequest = { expandedCombustible = false }
                        ) {
                            combustibles.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        combustible = item
                                        expandedCombustible = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Depósito (L)",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = capacidadDeposito,
                        onValueChange = { capacidadDeposito = it
                                        capacidadError = false },
                        isError = capacidadError,
                        placeholder = { Text("45.0") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (capacidadError) Color.Red else Color(0xFFEF4444),
                            unfocusedBorderColor = if (capacidadError) Color.Red else Color(0xFFE5E7EB)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (capacidadError) {
                        Text(
                            text = "Formato numérico incorrecto",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Consumo medio
            Text(
                text = "Consumo medio (L/100km)",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = consumoMedio,
                onValueChange = { consumoMedio = it
                                consumoError = false },
                isError = consumoError,
                placeholder = { Text("5.5") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (consumoError) Color.Red else Color(0xFFEF4444),
                    unfocusedBorderColor = if (consumoError) Color.Red else Color(0xFFE5E7EB)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            if (consumoError) {
                Text(
                    text = "Formato numérico incorrecto",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Nota informativa
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFDBEAFE)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Podrás vincular usuarios desde",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF)
                        )
                        Text(
                            text = "los detalles del vehículo",
                            fontSize = 12.sp,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            LaunchedEffect(viewModel.registroExitoso) {
                if (viewModel.registroExitoso) {
                    Toast.makeText(context, "Vehículo añadido correctamente", Toast.LENGTH_SHORT).show()
                    onAddClick()
                }
            }
            // Botón Añadir
            Button(
                onClick = {
                    // Validación
                    nombreError = nombreVehiculo.isBlank()
                    fabricanteError = fabricante.isBlank()
                    modeloError = modelo.isBlank()
                    matriculaError = matricula.isBlank()
                    anioError = anio.isBlank()
                    capacidadError = capacidadDeposito.isBlank()
                    consumoError = consumoMedio.isBlank()

                    val matriculaRegex = Regex("""^\d{4} [BCDFGHJKLMNPRSTVWXYZ]{3}$""")
                    matriculaError = !matriculaRegex.matches(matricula)

                    val anioActual = Calendar.getInstance().get(Calendar.YEAR)
                    val anioInt = anio.toIntOrNull()
                    anioError = anioInt == null || anioInt < 1900 || anioInt > anioActual

                    val capacidadRegex = Regex("""^\d+\.\d$""")
                    capacidadError = !capacidadRegex.matches(capacidadDeposito)

                    val consumoRegex = Regex("""^\d+\.\d$""")
                    consumoError = !consumoRegex.matches(consumoMedio)

                    val hayError = nombreError || fabricanteError || modeloError || matriculaError || anioError || capacidadError || consumoError
                    if (hayError) return@Button

                    val request = RegistrarVehiculoRequest(
                        usuarioId = usuarioId,
                        nombre = nombreVehiculo,
                        matricula = matricula,
                        modelo = modelo,
                        fabricante = fabricante,
                        antiguedad = if (anio.isNotEmpty()) (2025 - anio.toInt()) else 0,
                        tipo_combustible = combustible ?: "Gasolina",
                        litros_combustible = capacidadDeposito.toIntOrNull() ?: 45.0,
                        consumo_medio = consumoMedio.toDoubleOrNull() ?: 5.5,
                        ubicacion_actual = Ubicacion(40.4168, -4.7038), // Madrid
                        estado = "Activo",
                        tipo = when (tipoSeleccionado) {
                            TipoVehiculo.COCHE -> "Coche"
                            TipoVehiculo.MOTO -> "Moto"
                            TipoVehiculo.FURGONETA -> "Furgoneta"
                            TipoVehiculo.CAMION -> "Camion"
                            TipoVehiculo.OTRO -> "Otro"
                        }
                    )
                    viewModel.registrarVehiculo(token, request)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "Añadir Vehículo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun TipoVehiculoOption(
    tipo: TipoVehiculo,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Color(0xFFEF4444) else Color(0xFFE5E7EB)
    val borderWidth = if (selected) 2.dp else 1.dp

    Card(
        modifier = modifier
            .height(70.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = tipo.iconRes),
                contentDescription = tipo.name,
                tint = if (selected) tipo.color else Color(0xFF6B7280),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (tipo) {
                    TipoVehiculo.COCHE -> "Coche"
                    TipoVehiculo.MOTO -> "Moto"
                    TipoVehiculo.FURGONETA -> "Furgoneta"
                    TipoVehiculo.CAMION -> "Camion"
                    TipoVehiculo.OTRO -> TODO()
                },
                fontSize = 12.sp,
                color = if (selected) Color(0xFF1F2937) else Color(0xFF6B7280)
            )
        }
    }
}

data class VehiculoData(
    val tipo: TipoVehiculo,
    val fabricante: String,
    val modelo: String,
    val matricula: String,
    val anio: Int,
    val combustible: String,
    val capacidadDeposito: Int,
    val consumoMedio: Double
)