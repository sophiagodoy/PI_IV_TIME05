package br.com.ibm.intelimed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

/**
 * Activity principal responsável pela tela de Onboarding.
 */
class OnBoardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    OnBoardingScreen()
                }
            }
        }
    }
}

// Paleta de cores utilizada
private val Teal = Color(0xFF2F7D7D)
private val CardBg = Color(0xFFFFFFFF)
private val LightTeal = Color(0xFFB5D8D8)

/**
 * Tela principal do Onboarding.
 */
@Composable
fun OnBoardingScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Cabeçalho

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Teal)
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "INTELIMED",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }


        // Conteúdo principal

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Onboarding",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Teal
            )

            Spacer(Modifier.height(32.dp))

            // Cards informativos
            OnBoardingCard(Icons.Filled.Add, "Passo 1", "Registre seus sintomas")
            Spacer(Modifier.height(20.dp))
            OnBoardingCard(Icons.AutoMirrored.Filled.Chat, "Passo 2", "Converse com seu médico")
            Spacer(Modifier.height(20.dp))
            OnBoardingCard(Icons.Filled.Description, "Passo 3", "Acompanhe seus relatórios")

            Spacer(Modifier.height(36.dp))


            // Botões

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão "Pular"
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Pulando onboarding", Toast.LENGTH_SHORT).show()
                        // TODO: ação Pular
                    },
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, Teal),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text("Pular", color = Teal, fontSize = 16.sp)
                }

                Spacer(Modifier.width(16.dp))

                // Botão "Próximo"
                Button(
                    onClick = {
                        Toast.makeText(context, "Avançando para próximo passo", Toast.LENGTH_SHORT).show()
                        // TODO: ação Próximo
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Text("Próximo", fontSize = 16.sp)
                }
            }
        }
    }
}

/**
 * Composable responsável por exibir cada card do Onboarding.
 */
@Composable
fun OnBoardingCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            // Ícone à esquerda
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .border(2.dp, Teal.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(20.dp))

            // Textos (título e descrição)
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.Black)
                Text(description, color = Color.Gray, fontSize = 15.sp)
            }
        }
    }
}

/**
 * Preview do layout no Android Studio.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnBoardingPreview() {
    OnBoardingScreen()
}
