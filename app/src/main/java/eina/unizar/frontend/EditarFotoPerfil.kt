package eina.unizar.frontend

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import eina.unizar.frontend.viewmodels.PerfilViewModel
import eina.unizar.frontend.viewmodels.SubidaEstado

// Necesitarás una forma de inyectar el ViewModel.
// Si usas Hilt, simplemente usarías 'viewModel()'. Si no,
// tendrás que crear un Factory o pasar el ApiService aquí.
// ASUMO que puedes obtener una instancia de PerfilViewModel (ej. con un Factory o inyección).

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarFotoPerfilScreen(
    navController: androidx.navigation.NavHostController,
    token: String,
    // Asegúrate de inyectar PerfilViewModel correctamente
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    // 1. Estado para almacenar la URI de la imagen seleccionada
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Lanzador para abrir la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // Contrato para obtener contenido (imagen)
    ) { uri: Uri? ->
        selectedImageUri = uri // Guarda la URI seleccionada
    }

    // 3. Observar el estado de la subida
    val estadoSubida by perfilViewModel.estadoSubida.collectAsState()

    // 4. Efecto para manejar el resultado de la subida
    LaunchedEffect(estadoSubida) {
        when (estadoSubida) {
            is SubidaEstado.Exito -> {
                Toast.makeText(context, "Foto actualizada con éxito.", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Vuelve a la pantalla anterior (Home)
            }
            is SubidaEstado.Error -> {
                val errorMsg = (estadoSubida as SubidaEstado.Error).mensaje
                Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
            }
            SubidaEstado.Cargando, SubidaEstado.Inicial -> {
                // No hacer nada, la UI maneja estos estados
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Foto de Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Previsualización y Botón de Selección
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(150.dp)
            ) {
                // Previsualización de la Imagen
                val painter = rememberAsyncImagePainter(
                    model = coil.request.ImageRequest.Builder(context)
                        .data(selectedImageUri) // Carga la URI seleccionada
                        .error(R.drawable.carcare_logo) // Icono de persona por defecto si no hay foto/error
                        .build()
                )

                Image(
                    painter = painter,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(Color.LightGray)
                )

                // Botón para seleccionar imagen
                FloatingActionButton(
                    onClick = { imagePickerLauncher.launch("image/*") }, // Abre la galería
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = "Seleccionar foto",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Botón de Guardar
            Button(
                onClick = {
                    val uri = selectedImageUri
                    if (uri != null) {
                        // Llama al ViewModel para subir la foto
                        perfilViewModel.subirFotoPerfil(context, uri, token)
                    } else {
                        Toast.makeText(context, "Primero selecciona una foto.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = selectedImageUri != null && estadoSubida != SubidaEstado.Cargando // Deshabilita si está cargando
            ) {
                if (estadoSubida == SubidaEstado.Cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("GUARDAR FOTO", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mensaje de estado (opcional, para claridad)
            if (estadoSubida is SubidaEstado.Error) {
                Text(
                    text = "Fallo: ${(estadoSubida as SubidaEstado.Error).mensaje}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}