package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

/**
 * Dialog de anuncio que se muestra a usuarios gratuitos.
 *
 * Funcionalidad:
 * - Bloquea el uso durante 5 segundos
 * - Promociona la suscripción Premium
 * - Permite cerrar tras el countdown
 */
@Composable
fun AnuncioDialog(
    onDismiss: () -> Unit,
    navController: NavHostController? = null
) {
    var segundosRestantes by remember { mutableStateOf(5) }

    LaunchedEffect(Unit) {
        while (segundosRestantes > 0) {
            delay(1000)
            segundosRestantes--
        }
    }

    Dialog(
        onDismissRequest = { if (segundosRestantes == 0) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = segundosRestantes == 0,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(500.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Contenido del anuncio
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF667EEA),
                                    Color(0xFF764BA2)
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "🚗",
                        fontSize = 80.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "¿Cansado de los anuncios?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Hazte Premium y disfruta de una experiencia sin interrupciones",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Beneficios Premium
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        BeneficioItem("✓ Sin anuncios")
                        Spacer(modifier = Modifier.height(8.dp))
                        BeneficioItem("✓ Acceso prioritario")
                        Spacer(modifier = Modifier.height(8.dp))
                        BeneficioItem("✓ Funciones exclusivas")

                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            onDismiss()
                            navController?.navigate("premium")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            "Quiero ser Premium",
                            color = Color(0xFF000000),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                // Botón cerrar (solo visible después de 5 segundos)
                if (segundosRestantes == 0) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                } else {
                    // Contador de tiempo
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "$segundosRestantes s",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BeneficioItem(texto: String) {
    Text(
        text = texto,
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.95f),
        fontWeight = FontWeight.Medium
    )
}

/**
 * Gestor de anuncios para controlar cuándo mostrarlos.
 */
object AnuncioManager {
    private var contadorAcciones = 0
    private const val ACCIONES_ENTRE_ANUNCIOS = 10 // Mostrar cada 3 acciones

    /**
     * Determina si debe mostrarse un anuncio según el número de acciones.
     */
    fun deberMostrarAnuncio(esPremium: Boolean): Boolean {
        if (esPremium) return false

        contadorAcciones++
        return contadorAcciones >= ACCIONES_ENTRE_ANUNCIOS
    }

    /**
     * Resetea el contador después de mostrar un anuncio.
     */
    fun resetearContador() {
        contadorAcciones = 0
    }
}