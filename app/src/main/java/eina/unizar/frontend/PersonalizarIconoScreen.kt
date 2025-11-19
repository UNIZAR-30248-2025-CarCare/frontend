package eina.unizar.frontend

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizarIconoScreen(
    vehiculoId: String,
    vehiculoNombre: String,
    vehiculoTipo: String,
    navController: NavHostController
) {
    val context = LocalContext.current
    val iconPreferences = remember { IconPreferences(context) }

    // Estados para la imagen personalizada
    var customImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var hasCustomImage by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Cargar imagen existente al iniciar
    LaunchedEffect(Unit) {
        Log.d("PersonalizarIcono", "=== INICIO PANTALLA FOTOS ===")
        Log.d("PersonalizarIcono", "Vehículo ID: $vehiculoId")
        Log.d("PersonalizarIcono", "Vehículo Nombre: $vehiculoNombre")
        Log.d("PersonalizarIcono", "Vehículo Tipo: $vehiculoTipo")

        val bitmap = iconPreferences.loadCustomImageBitmap(vehiculoId)
        customImageBitmap = bitmap
        hasCustomImage = bitmap != null

        Log.d("PersonalizarIcono", "Imagen existente: ${if (bitmap != null) "SÍ" else "NO"}")
        Log.d("PersonalizarIcono", "========================")
    }

    // Convertir string a TipoVehiculo
    val tipoVehiculo = when (vehiculoTipo.uppercase()) {
        "COCHE" -> TipoVehiculo.COCHE
        "MOTO" -> TipoVehiculo.MOTO
        "FURGONETA" -> TipoVehiculo.FURGONETA
        "CAMION" -> TipoVehiculo.CAMION
        else -> TipoVehiculo.OTRO
    }

    // Función para crear archivo temporal para la cámara
    val createImageFile: () -> Uri? = {
        try {
            val timeStamp = System.currentTimeMillis()
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = File(context.cacheDir, "temp_images")
            storageDir.mkdirs()

            val file = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            photoUri = uri
            uri
        } catch (e: Exception) {
            Log.e("PersonalizarIcono", "Error creando archivo temporal: ${e.message}")
            null
        }
    }

    // Launcher para tomar foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            isProcessingImage = true

            Log.d("PersonalizarIcono", "Foto tomada exitosamente, procesando...")

            // Procesar la imagen
            val savedPath = ImageUtils.processImageForVehicle(
                context, photoUri!!, vehiculoId.toInt()
            )

            if (savedPath != null) {
                // Guardar la ruta en preferencias
                iconPreferences.saveCustomImageForVehicle(vehiculoId, savedPath)

                // Actualizar UI
                customImageBitmap = iconPreferences.loadCustomImageBitmap(vehiculoId)
                hasCustomImage = true

                Toast.makeText(context, "Foto guardada exitosamente", Toast.LENGTH_SHORT).show()
                Log.d("PersonalizarIcono", "Foto desde cámara guardada: $savedPath")
            } else {
                Toast.makeText(context, "Error al procesar la foto", Toast.LENGTH_SHORT).show()
                Log.e("PersonalizarIcono", "Error procesando foto desde cámara")
            }

            isProcessingImage = false
        } else {
            Log.w("PersonalizarIcono", "Foto no tomada o URI nulo")
        }
    }

    // Launcher para seleccionar de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isProcessingImage = true

            Log.d("PersonalizarIcono", "Imagen seleccionada de galería, procesando...")

            // Procesar la imagen
            val savedPath = ImageUtils.processImageForVehicle(
                context, uri, vehiculoId.toInt()
            )

            if (savedPath != null) {
                // Guardar la ruta en preferencias
                iconPreferences.saveCustomImageForVehicle(vehiculoId, savedPath)

                // Actualizar UI
                customImageBitmap = iconPreferences.loadCustomImageBitmap(vehiculoId)
                hasCustomImage = true

                Toast.makeText(context, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show()
                Log.d("PersonalizarIcono", "Imagen desde galería guardada: $savedPath")
            } else {
                Toast.makeText(context, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                Log.e("PersonalizarIcono", "Error procesando imagen desde galería")
            }

            isProcessingImage = false
        } else {
            Log.w("PersonalizarIcono", "No se seleccionó imagen de galería")
        }
    }

    // Launcher para permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageFile()
            if (uri != null) {
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Error al crear archivo temporal", Toast.LENGTH_SHORT).show()
                Log.e("PersonalizarIcono", "No se pudo crear archivo temporal para cámara")
            }
        } else {
            Toast.makeText(context, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
            Log.w("PersonalizarIcono", "Permiso de cámara denegado")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personalizar Ícono",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d("PersonalizarIcono", "Volviendo atrás...")
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
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
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card con info del vehículo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        vehiculoNombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Tipo: $vehiculoTipo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Preview del ícono actual
            Card(
                modifier = Modifier.size(150.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isProcessingImage) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (customImageBitmap != null) {
                        Image(
                            bitmap = customImageBitmap!!.asImageBitmap(),
                            contentDescription = "Imagen del vehículo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                id = iconPreferences.getDefaultIconForTipo(tipoVehiculo)
                            ),
                            contentDescription = "Ícono por defecto",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto descriptivo
            Text(
                text = if (hasCustomImage) "Tu imagen personalizada" else "Usando ícono por defecto",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón tomar foto
                Button(
                    onClick = {
                        Log.d("PersonalizarIcono", "Solicitando permiso de cámara...")
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !isProcessingImage,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Tomar Foto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botón seleccionar de galería
                Button(
                    onClick = {
                        Log.d("PersonalizarIcono", "Abriendo galería...")
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    enabled = !isProcessingImage,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Seleccionar de Galería",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botón eliminar imagen (solo si tiene imagen personalizada)
                if (hasCustomImage) {
                    OutlinedButton(
                        onClick = {
                            Log.d("PersonalizarIcono", "Eliminando imagen personalizada...")
                            iconPreferences.removeCustomImageForVehicle(vehiculoId)
                            customImageBitmap = null
                            hasCustomImage = false

                            Toast.makeText(
                                context,
                                "Imagen eliminada. Usando ícono por defecto.",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d("PersonalizarIcono", "Imagen personalizada eliminada para vehículo $vehiculoId")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enabled = !isProcessingImage,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Eliminar Imagen",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Texto de ayuda
            Text(
                text = "La imagen se mostrará como ícono circular en el mapa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}