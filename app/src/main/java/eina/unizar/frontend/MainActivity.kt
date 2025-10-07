package eina.unizar.frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaPrincipal {
                // Navegar a la siguiente pantalla
                startActivity(Intent(this, EleccionInicioActivity::class.java))
            }
        }
    }
}

@Composable
fun PantallaPrincipal(onContinuarClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(id = R.drawable.carcare_logo), // Reemplaza con tu recurso de logo
                contentDescription = "Logo de CarCare",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Mensaje de bienvenida
            Text(
                text = "Bienvenido a CarCare",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(40.dp))
            // Botón de continuar
            Button(
                onClick = onContinuarClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Continuar", color = Color.White)
            }
        }
    }
}