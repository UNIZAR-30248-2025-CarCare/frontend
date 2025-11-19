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
import java.util.Locale

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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "Añadir Vehículo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
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
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tipo de vehículo
            Text(
                text = "Tipo de vehículo",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = nombreVehiculo,
                onValueChange = {
                    nombreVehiculo = it
                    nombreError = false
                },
                isError = nombreError,
                placeholder = {
                    Text(
                        "Ej: Mi coche, Furgoneta de trabajo...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (nombreError) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (nombreError) Color.Red else MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(20.dp))


            // Fabricante
            Text(
                text = "Fabricante",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = fabricante,
                onValueChange = { fabricante = it
                    fabricanteError = false },
                isError = fabricanteError,
                placeholder = {
                    Text(
                        "Ej: Seat, Toyota, Ford...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (fabricanteError) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (fabricanteError) Color.Red else MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Modelo
            Text(
                text = "Modelo",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it
                    modeloError = false },
                isError = modeloError,
                placeholder = {
                    Text(
                        "Ej: Ibiza, Corolla...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (modeloError) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (modeloError) Color.Red else MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = matricula,
                        onValueChange = { matricula = it
                            matriculaError = false },
                        isError = matriculaError,
                        placeholder = {
                            Text(
                                "1234 BBC",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (matriculaError) Color.Red else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (matriculaError) Color.Red else MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = anio,
                        onValueChange = { anio = it
                            anioError = false },
                        isError = anioError,
                        placeholder = {
                            Text(
                                "2020",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (anioError) Color.Red else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (anioError) Color.Red else MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCombustible,
                            onDismissRequest = { expandedCombustible = false },
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            combustibles.forEach { item ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            item,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = capacidadDeposito,
                        onValueChange = { capacidadDeposito = it
                            capacidadError = false },
                        isError = capacidadError,
                        placeholder = {
                            Text(
                                "45.0",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (capacidadError) Color.Red else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (capacidadError) Color.Red else MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            OutlinedTextField(
                value = consumoMedio,
                onValueChange = { consumoMedio = it
                    consumoError = false },
                isError = consumoError,
                placeholder = {
                    Text(
                        "5.5",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (consumoError) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (consumoError) Color.Red else MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Podrás vincular usuarios desde",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "los detalles del vehículo",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
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
                    matriculaError = matricula.isBlank() && !esMatriculaValida(matricula)
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
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Añadir Vehículo",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
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
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val borderWidth = if (selected) 2.dp else 1.dp

    Card(
        modifier = modifier
            .height(70.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = tipo.iconRes),
                contentDescription = tipo.name,
                tint = if (selected) tipo.color else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (tipo) {
                    TipoVehiculo.COCHE -> "Coche"
                    TipoVehiculo.MOTO -> "Moto"
                    TipoVehiculo.FURGONETA -> "Furgoneta"
                    TipoVehiculo.CAMION -> "Camion"
                    TipoVehiculo.OTRO -> "Otro"
                },
                fontSize = 12.sp,
                color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
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

// Letras permitidas en el sistema actual (sin vocales, Ñ, Q)
val letrasPermitidas = "BCDFGHJKLMNPRSTVWXYZ"

// Última matrícula emitida (actualízala según la DGT)
val ultimaMatriculaEmitida = "9999 NGG"

fun esMatriculaValida(matricula: String): Boolean {
    val matriculaActualRegex = Regex("""^\d{4}[$letrasPermitidas]{3}$""")
    val matriculaProvincialRegex = Regex("""^[A-Z]{1,2}\s?\d{4}\s?[A-Z]{1,2}$""")

    val limpia = matricula.replace(" ", "").uppercase(Locale.getDefault())

    // Formato actual
    if (matriculaActualRegex.matches(limpia)) {
        // Comprobar que no es posterior a la última emitida
        if (limpia > ultimaMatriculaEmitida.replace(" ", "")) return false
        return true
    }
    // Formato provincial antiguo
    if (matriculaProvincialRegex.matches(limpia)) {
        return true
    }
    return false
}