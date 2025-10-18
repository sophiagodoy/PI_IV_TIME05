package br.com.ibm.intelimed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class MainDoctorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IntelimedTheme {
                DoctorHome()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHome() {
    val teal = Color(0xFF007C7A)
    val cardBg = Color(0xFFF7FDFC)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("INTELIMED", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal),
                actions = {
                    IconButton(onClick = { /* Notificações */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
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
                NavigationBarItem(
                    selected = false,
                    onClick = { /* perfil */ },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Perfil") }
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
                text = "Bem-vindo, Doutor(a)!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Olá, Dr. Arthur", // ou nome do doutor dinamicamente
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            // Cards de opções
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Pacientes", Icons.Default.Person) {
                    val intent = android.content.Intent(context, PatientListActivity::class.java)
                    context.startActivity(intent)
                }
                OptionCard("Chat", Icons.Default.Chat) { /* abrir chat com paciente */ }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Feedbacks", Icons.Default.ThumbUp) { /* abrir feedbacks */ }
                OptionCard("Encaminhamentos", Icons.Default.ArrowForward) { /* abrir encaminhamentos */ }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorHomeActivityPreview() {
    IntelimedTheme {
        DoctorHome()
    }
}
