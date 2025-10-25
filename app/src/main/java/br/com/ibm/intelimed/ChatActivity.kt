package br.com.ibm.intelimed

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextFieldDefaults
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                ChatScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val teal = Color(0xFF007C7A)
    val msgBg = Color(0xFFF7FDFC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("INTELIMED", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Chat", color = Color.White.copy(0.9f), fontSize = 14.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* voltar */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal)
            )
        },
        bottomBar = {
            ChatInputBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val mensagens = listOf(
                    Mensagem("Olá, doutor!", true),
                    Mensagem("Olá, Ryan! Como está se sentindo hoje?", false),
                    Mensagem("Melhor, mas ainda com dor de cabeça.", true),
                    Mensagem("Entendido. Mantenha o repouso e continue a medicação prescrita.", false)
                )

                items(mensagens) { msg ->
                    MensagemBubble(msg.texto, msg.enviadaPeloUsuario)
                }
            }
        }
    }
}

@Composable
fun ChatInputBar() {
    val teal = Color(0xFF007C7A)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* abrir câmera */ }) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Abrir câmera", tint = teal)
        }

        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Enviar mensagem") },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7FDFC),
                unfocusedContainerColor = Color(0xFFF7FDFC),
                disabledContainerColor = Color(0xFFF7FDFC),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { /* enviar áudio */ },
            modifier = Modifier
                .size(45.dp)
                .background(teal, CircleShape)
        ) {
            Icon(Icons.Default.Mic, contentDescription = "Gravar áudio", tint = Color.White)
        }
    }
}

@Composable
fun MensagemBubble(texto: String, enviadaPeloUsuario: Boolean) {
    val teal = Color(0xFF007C7A)
    val bg = if (enviadaPeloUsuario) teal else Color(0xFFEFEFEF)
    val textoCor = if (enviadaPeloUsuario) Color.White else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (enviadaPeloUsuario) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(bg, RoundedCornerShape(16.dp))
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(texto, color = textoCor, fontSize = 16.sp)
        }
    }
}

data class Mensagem(val texto: String, val enviadaPeloUsuario: Boolean)

@Preview
@Composable
fun ChatPacientePreview() {
    IntelimedTheme {
        ChatScreen()
    }
}
