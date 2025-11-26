// TELA PARA O PACIENTE LER O FEEDBACK DO MÉDICO
package br.com.ibm.intelimed

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class DoctorFeedbackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val feedbackText = intent.getStringExtra("feedback") ?: ""
        val dataRegistro = intent.getStringExtra("dataRegistro") ?: ""
        val sentimento = intent.getStringExtra("sentimento") ?: ""

        setContent {
            IntelimedTheme {
                DoctorFeedbackScreen(
                    feedbackText = feedbackText,
                    dataRegistro = dataRegistro,
                    sentimento = sentimento
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorFeedbackScreen(
    feedbackText: String,
    dataRegistro: String,
    sentimento: String
) {
    val teal = Color(0xFF007C7A)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Resposta do médico",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
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

            Text(
                "Detalhes do seu relatório",
                fontWeight = FontWeight.Bold,
                color = teal,
                fontSize = 20.sp
            )

            if (dataRegistro.isNotBlank()) {
                Text(
                    text = "Data do registro: $dataRegistro",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }

            if (sentimento.isNotBlank()) {
                Text(
                    text = "Como você relatou que estava: $sentimento",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }

            // CARD DO FEEDBACK
            Text(
                "Feedback do médico",
                fontWeight = FontWeight.Bold,
                color = teal,
                fontSize = 18.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = feedbackText.ifBlank { "Este relatório ainda não possui feedback do médico." },
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
        DoctorFeedbackScreen(
            feedbackText = "Exemplo de feedback do médico para o paciente.",
            dataRegistro = "01/11/2025",
            sentimento = "Com dor de cabeça e febre leve"
        )
    }
}
