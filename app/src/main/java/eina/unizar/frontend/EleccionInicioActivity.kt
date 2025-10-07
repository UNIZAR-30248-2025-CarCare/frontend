package eina.unizar.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

class EleccionInicioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PantallaEleccionInicio()
        }
    }
}

@Composable
fun PantallaEleccionInicio(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.carcare_logo),
                contentDescription = "Logo CarCare",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )

            // Título
            Text(
                text = "Bienvenido a",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1C)
            )

            Text(
                text = "CarCare",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935) // Rojo
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Gestiona tu vehículo compartido",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de texto
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "¿Olvidé mi contraseña?",
                fontSize = 14.sp,
                color = Color(0xFFE53935),
                modifier = Modifier.clickable { onForgotPasswordClick() },
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Iniciar Sesión
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Iniciar Sesión", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            DividerWithText()

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Registrarse
            OutlinedButton(
                onClick = onRegisterClick,
                shape = RoundedCornerShape(50),
                border = BorderStroke(2.dp, Color(0xFFE53935)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse", color = Color(0xFFE53935), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Eres nuevo por aquí?\nCrea tu cuenta y empieza a gestionar tu vehículo compartido",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
    @Composable
    fun DividerWithText() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text("  o  ", fontSize = 14.sp, color = Color.Gray)
            Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }
    }