// TELA PARA O PACIENTE LER O FEEDBACK DO MÉDICO
package br.com.ibm.intelimed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorFeedback() {

    val teal = Color(0xFF007C7A)
    val scrollState = rememberScrollState()

    // Aqui você vai puxar do Firestore futuramente
    val nomeMedico = "Dr. João"
    val respostaMedico =
        "Olá Maria, observei seus sintomas e recomendo repouso hoje. " +
                "Continue se hidratando bem e, caso a febre suba acima de 38.5°C, procure atendimento. " +
                "Vou acompanhar sua evolução!"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Resposta do Médico",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(scrollState)
                .fillMaxSize()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ===== TÍTULO =====
            Text(
                "Feedback de $nomeMedico",
                fontWeight = FontWeight.Bold,
                color = teal,
                fontSize = 20.sp
            )

            // ===== CARD DO FEEDBACK =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = respostaMedico,
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DoctorFeedbackPreview() {
    IntelimedTheme {
        DoctorFeedback()
    }
}
