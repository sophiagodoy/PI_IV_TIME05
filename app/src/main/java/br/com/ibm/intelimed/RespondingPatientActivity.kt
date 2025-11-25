package br.com.ibm.intelimed

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RespondingPatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val relatorioId = intent.getStringExtra("relatorioId") ?: ""
        val pacienteId = intent.getStringExtra("pacienteId") ?: ""

        setContent {
            IntelimedTheme {
                RespondingPatient(
                    pacienteId = pacienteId,
                    relatorioId = relatorioId
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RespondingPatient(
    pacienteId: String,
    relatorioId: String
) {
    val teal = Color(0xFF007C7A)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var sintomasMap by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var textoFeedback by remember { mutableStateOf("") }
    var nomePaciente by remember { mutableStateOf("") }

    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()

    // BUSCA NO FIRESTORE
    LaunchedEffect(pacienteId, relatorioId) {

        if (pacienteId.isBlank() || relatorioId.isBlank()) {
            erro = "Dados do relatório inválidos."
            carregando = false
            return@LaunchedEffect
        }

        // Nome do paciente
        db.collection("paciente")
            .document(pacienteId)
            .get()
            .addOnSuccessListener { doc ->
                nomePaciente = doc.getString("nome") ?: "Paciente"
            }

        // Sintomas do paciente
        db.collection("paciente")
            .document(pacienteId)
            .collection("sintomas")
            .document(relatorioId)
            .get()
            .addOnSuccessListener { doc ->
                val data = doc.data
                if (data == null) {
                    erro = "Respostas não encontradas."
                } else {
                    sintomasMap = data
                }
                carregando = false
            }
            .addOnFailureListener { e ->
                erro = "Erro ao carregar respostas: ${e.message}"
                carregando = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Respostas de $nomePaciente",
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Respostas do Paciente",
                fontWeight = FontWeight.Bold,
                color = teal,
                fontSize = 20.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    when {
                        carregando -> {
                            Text("Carregando respostas...", color = Color.Gray)
                        }
                        erro != null -> {
                            Text(erro ?: "Erro", color = Color.Red)
                        }
                        sintomasMap.isEmpty() -> {
                            Text("Nenhuma resposta encontrada.", color = Color.Gray)
                        }
                        else -> {
                            // Lista de campos em ordem + rótulo bonitinho
                            val camposExibicao = listOf(
                                "dataRegistro"       to "Data do registro",
                                "sentimento"         to "Como está se sentindo?",
                                "dormiuBem"          to "Dormiu bem na última noite?",
                                "cansaco"            to "Cansaço",
                                "alimentacao"        to "Alimentação",
                                "hidratacao"         to "Hidratação",
                                "sentiuDor"          to "Está sentindo dor?",
                                "intensidadeDor"     to "Intensidade da dor (0 a 10)",
                                "localDor"           to "Local da dor",
                                "tipoDorMudou"       to "O tipo da dor mudou?",
                                "febre"              to "Teve febre nas últimas 24h?",
                                "temperatura"        to "Temperatura (°C)",
                                "enjoo"              to "Enjoo, vômito ou diarreia",
                                "tontura"            to "Tontura ou fraqueza",
                                "sangramento"        to "Sangramento / secreção / inchaço",
                                "fezCicatrizacao"    to "Fez cicatrização ou procedimento recente?",
                                "estadoCicatrizacao" to "Como está a cicatrização?",
                                "tomouMedicacao"     to "Tomou medicação nas últimas 24h?",
                                "qualMedicacao"      to "Qual medicação?",
                                "horarioMedicacao"   to "Horário da medicação",
                                "observacoes"        to "Observações gerais",
                                "feedback"           to "Feedback do médico"
                            )

                            camposExibicao.forEach { (chave, label) ->
                                val valor = sintomasMap[chave] ?: return@forEach

                                val textoValor = if (chave == "dataRegistro") {
                                    convertTimestampToDate(valor)
                                } else {
                                    valor.toString()
                                }

                                if (textoValor.isNotBlank()) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = textoValor,
                                        color = Color.DarkGray,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // FEEDBACK
            Text(
                "Responder ao Paciente",
                fontWeight = FontWeight.Bold,
                color = teal,
                fontSize = 20.sp
            )

            OutlinedTextField(
                value = textoFeedback,
                onValueChange = { textoFeedback = it },
                label = { Text("Escreva seu feedback aqui") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Button(
                onClick = {
                    if (textoFeedback.isNotBlank()) {
                        db.collection("paciente")
                            .document(pacienteId)
                            .collection("sintomas")
                            .document(relatorioId)
                            .update("feedback", textoFeedback)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Feedback enviado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                textoFeedback = ""
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Erro ao enviar feedback.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {
                        Toast.makeText(
                            context,
                            "Escreva um feedback antes de enviar.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = teal),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Feedback", fontSize = 18.sp)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RespondingPatientPreview() {
    IntelimedTheme {
        RespondingPatient(
            pacienteId = "TESTE",
            relatorioId = "TESTE"
        )
    }
}
