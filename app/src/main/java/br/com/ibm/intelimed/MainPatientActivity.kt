package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import android.widget.Toast

class MainPatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                PatientHome()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHome() {
    val teal = Color(0xFF007C7A)
    val cardBg = Color(0xFFF7FDFC)
    val context = LocalContext.current

    // Estado do diálogo de confirmação de saída
    var mostrarDialogoSair by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("INTELIMED", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal),
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Nenhuma notificação nova", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Início") }
                )

                // Ícone de saída no lugar do perfil
                NavigationBarItem(
                    selected = false,
                    onClick = { mostrarDialogoSair = true },
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Sair") },
                    label = { Text("Sair") }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Home",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Olá, Arthur",
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            // Cards de opções
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Chat", Icons.Default.Chat) {
                    Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                }
                OptionCard("Escolher Médico", Icons.Default.Description) {
                    val intent = Intent(context, SymptomLogActivity::class.java)
                    context.startActivity(intent)
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Orientação Fixa", Icons.Default.ArrowForward) {
                    val intent = Intent(context, MedicalGuidanceActivity::class.java)
                    context.startActivity(intent)
                }
                OptionCard("Feedback Médico", Icons.Default.Warning) {
                    Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = teal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Registrar sintomas hoje", color = Color.White, fontSize = 16.sp)
            }

            Button(
                onClick = {
                    val intent = Intent(context, MedicalGuidanceActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Ver orientações médicas", color = teal, fontSize = 16.sp)
            }
        }

        // Diálogo de confirmação de saída
        if (mostrarDialogoSair) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoSair = false },
                title = { Text("Confirmar saída") },
                text = { Text("Deseja realmente sair da sua conta?") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogoSair = false
                        val intent = Intent(context, AuthChoiceActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }) {
                        Text("Sim", color = teal)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoSair = false }) {
                        Text("Não", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun RowScope.OptionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val teal = Color(0xFF007C7A)
    val cardBg = Color(0xFFF7FDFC)

    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = teal, modifier = Modifier.size(30.dp))
            Text(title, color = Color.Black, fontSize = 14.sp)
        }
    }
}

@Preview
@Composable
fun MainPatientActivityPreview() {
    IntelimedTheme {
        PatientHome()
    }
}
