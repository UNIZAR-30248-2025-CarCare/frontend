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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import eina.unizar.frontend.viewmodels.VehiculoViewModel
import kotlinx.coroutines.launch
import java.io.File


fun getIconoPorDefecto(tipoVehiculo: TipoVehiculo): Int {
    return when (tipoVehiculo) {
        TipoVehiculo.COCHE -> R.drawable.ic_coche
        TipoVehiculo.MOTO -> R.drawable.ic_moto
        TipoVehiculo.FURGONETA -> R.drawable.ic_furgoneta
        TipoVehiculo.CAMION -> R.drawable.ic_camion
        TipoVehiculo.OTRO -> R.drawable.ic_coche
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalizarIconoScreen(
    vehiculoId: String,
    vehiculoNombre: String,
    vehiculoTipo: String,
    navController: NavHostController,
    token: String,
    iconoActualUrl: String?,
    onIconoActualizado: (String) -> Unit
) {

    val tipoVehiculo = when (vehiculoTipo.uppercase()) {
        "COCHE" -> TipoVehiculo.COCHE
        "MOTO" -> TipoVehiculo.MOTO
        "FURGONETA" -> TipoVehiculo.FURGONETA
        "CAMION" -> TipoVehiculo.CAMION
        else -> TipoVehiculo.OTRO
    }
    val context = LocalContext.current
    val vehiculoViewModel: VehiculoViewModel = viewModel()
    val iconoUrl = vehiculoViewModel.iconoActualUrl

    var customImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var hasCustomImage by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var cargando by remember { mutableStateOf(false) }

    // Al entrar en la pantalla, carga el icono desde el backend
    LaunchedEffect(vehiculoId) {
        vehiculoViewModel.cargarIconoVehiculo(token, vehiculoId)
    }



    val createImageFile: () -> Uri? = {
        try {
            val timeStamp = System.currentTimeMillis()
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = File(context.cacheDir, "temp_images")
            storageDir.mkdirs()
            val file = File.createTempFile(imageFileName, ".jpg", storageDir)
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

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            imagenUri = photoUri
            customImageBitmap = LocalImageUtils.uriToBitmap(context, photoUri!!)
            hasCustomImage = true
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            customImageBitmap = LocalImageUtils.uriToBitmap(context, uri)
            hasCustomImage = true
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageFile()
            if (uri != null) {
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Error al crear archivo temporal", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de cámara necesario", Toast.LENGTH_SHORT).show()
        }
    }

    // Convierte Uri a MultipartBody.Part
    fun uriToMultipart(context: android.content.Context, uri: Uri): MultipartBody.Part? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)
        return MultipartBody.Part.createFormData("icono", "icono.jpg", requestFile)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personalizar icono",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(4.dp)
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

            Card(
                modifier = Modifier.size(200.dp),
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
                    } else if (vehiculoViewModel.iconoActualUrl != null && vehiculoViewModel.iconoActualUrl!!.isNotBlank()) {
                        VehiculoIcono(vehiculoViewModel.iconoActualUrl, tipoVehiculo)
                    } else {
                        Image(
                            painter = painterResource(id = getIconoPorDefecto(tipoVehiculo)),
                            contentDescription = "Icono por defecto",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (vehiculoViewModel.iconoActualUrl != null && vehiculoViewModel.iconoActualUrl!!.isNotBlank()) {
                OutlinedButton(
                    onClick = {
                        vehiculoViewModel.eliminarIconoVehiculo(token, vehiculoId) {
                            vehiculoViewModel.cargarIconoVehiculo(token, vehiculoId)
                            customImageBitmap = null
                            hasCustomImage = false
                            imagenUri = null
                            Toast.makeText(context, "Ahora usas el icono por defecto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Usar icono por defecto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
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
                        Icons.Default.Add,
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

                Button(
                    onClick = {
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
                        Icons.Default.Edit,
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

            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (imagenUri != null) {
                        cargando = true
                        val multipart = uriToMultipart(context, imagenUri!!)
                        if (multipart != null) {
                            vehiculoViewModel.subirIconoVehiculo(token, vehiculoId, multipart) { success, url ->
                                cargando = false
                                if (success && url != null) {
                                    Toast.makeText(context, "Icono subido correctamente", Toast.LENGTH_SHORT).show()
                                    vehiculoViewModel.cargarIconoVehiculo(token, vehiculoId) // Recarga la URL del icono
                                    onIconoActualizado(url)
                                } else {
                                    Toast.makeText(context, "Error al subir el icono", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            cargando = false
                            Toast.makeText(context, "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = imagenUri != null && !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar icono")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

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

// Utilidad para convertir Uri a Bitmap
object LocalImageUtils {
    fun uriToBitmap(context: android.content.Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            android.graphics.BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
fun VehiculoIcono(iconoUrl: String?, tipoVehiculo: TipoVehiculo) {
    var bitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(iconoUrl) {
        if (!iconoUrl.isNullOrBlank()) {
            val urlCompleta = if (iconoUrl.startsWith("/")) {
                "http://10.0.2.2:3000$iconoUrl"
            } else {
                iconoUrl
            }
            kotlinx.coroutines.Dispatchers.IO.let { dispatcher ->
                kotlinx.coroutines.CoroutineScope(dispatcher).launch {
                    try {
                        val url = java.net.URL(urlCompleta)
                        val bmp = android.graphics.BitmapFactory.decodeStream(url.openStream())
                        bitmap = bmp?.asImageBitmap()
                    } catch (e: Exception) {
                        bitmap = null
                    }
                }
            }
        } else {
            bitmap = null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = "Icono personalizado",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = getIconoPorDefecto(tipoVehiculo)),
            contentDescription = "Icono por defecto",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}