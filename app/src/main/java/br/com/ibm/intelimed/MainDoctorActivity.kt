package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

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
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("INTELIMED", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal),
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Nenhuma notificação nova", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
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
                    onClick = { showLogoutDialog = true },
                    icon = { Icon(Icons.Default.Logout, contentDescription = null) },
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
                text = "Bem-vindo, Doutor(a)!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Olá, Dr. Arthur", // pode ser dinâmico depois
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            // Cards de opções
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Pacientes", Icons.Default.Person) {
                    val intent = Intent(context, PatientListActivity::class.java)
                    context.startActivity(intent)
                }
                OptionCard("Chat", Icons.Default.Chat) {
                    Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Feedbacks", Icons.Default.ThumbUp) {
                    Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                }
                OptionCard("Encaminhamentos", Icons.Default.ArrowForward) {
                    Toast.makeText(context, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Popup de confirmação de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmar saída") },
            text = { Text("Deseja realmente sair da sua conta?") },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                    showLogoutDialog = false
                    val intent = Intent(context, AuthChoiceActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }) {
                    Text("Sim", color = teal)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Não", color = Color.Gray)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorHomePreview() {
    IntelimedTheme {
        DoctorHome()
    }
}
