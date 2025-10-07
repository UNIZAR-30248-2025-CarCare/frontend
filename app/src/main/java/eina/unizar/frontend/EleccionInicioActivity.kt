package eina.unizar.frontend

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class EleccionInicioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaEleccionInicio()
        }
    }
}

@Composable
fun PantallaEleccionInicio() {
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
            // Texto de pregunta
            Text(
                text = "¿Eres nuevo por aquí?",
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Botones en la misma fila
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { /* Acción para iniciar sesión */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Iniciar sesión", color = Color.White)
                }
                Button(
                    onClick = { /* Acción para registrarse */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Registrarse", color = Color.White)
                }
            }
        }
    }
}