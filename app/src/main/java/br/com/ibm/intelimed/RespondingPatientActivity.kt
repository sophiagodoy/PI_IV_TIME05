package br.com.ibm.intelimed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.app.Activity
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RespondingPatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val relatorioId = intent.getStringExtra("relatorioId") ?: ""
        val pacienteId = intent.getStringExtra("pacienteId") ?: ""
        val somenteVisualizar = intent.getBooleanExtra("somenteVisualizar", false)

        setContent {
            IntelimedTheme {
                RespondingPatient(
                    pacienteId = pacienteId,
                    relatorioId = relatorioId,
                    somenteVisualizar = somenteVisualizar
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RespondingPatient(
    pacienteId: String,
    relatorioId: String,
    somenteVisualizar: Boolean
) {
    val teal = Color(0xFF007C7A)
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var sintomasMap by remember { mutableStateOf<Map<String, Any>>(emptyMap()) }
    var textoFeedback by remember { mutableStateOf("") }
    var nomePaciente by remember { mutableStateOf("") }
    var feedbackExistente by remember { mutableStateOf("") }

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
                    val fb = data["feedback"]?.toString() ?: ""
                    feedbackExistente = fb
                    if (somenteVisualizar && fb.isNotBlank()) {
                        textoFeedback = fb
                    }
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
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
                                "observacoes"        to "Observações gerais"
                                // não coloco "feedback" aqui porque ele é tratado separado abaixo
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

            // ==== FEEDBACK ====
            Text(
                text = if (somenteVisualizar) "Feedback enviado" else "Responder ao Paciente",
                fontWeight = FontWeight.Bold,
                color = teal,
                fontSize = 20.sp
            )

            OutlinedTextField(
                value = if (somenteVisualizar) feedbackExistente else textoFeedback,
                onValueChange = {
                    if (!somenteVisualizar) textoFeedback = it
                },
                label = {
                    Text(
                        if (somenteVisualizar)
                            "Feedback enviado"
                        else
                            "Escreva seu feedback aqui"
                    )
                },
                enabled = !somenteVisualizar,
                readOnly = somenteVisualizar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            if (!somenteVisualizar) {
                Button(
                    onClick = {
                        if (textoFeedback.isNotBlank()) {
                            val uidMedico = FirebaseAuth.getInstance().currentUser?.uid
                            if (uidMedico == null) {
                                Toast.makeText(
                                    context,
                                    "Usuário não autenticado.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            // 1) Atualiza no documento do paciente
                            db.collection("paciente")
                                .document(pacienteId)
                                .collection("sintomas")
                                .document(relatorioId)
                                .update("feedback", textoFeedback)
                                .addOnSuccessListener {
                                    // 2) Atualiza também na coleção do médico,
                                    // para que entre em "Respondidos"
                                    db.collection("medico")
                                        .document(uidMedico)
                                        .collection("relatorios")
                                        .document(relatorioId)
                                        .update("feedback", textoFeedback)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Feedback enviado com sucesso!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            // Volta para "Meus relatórios"
                                            (context as? Activity)?.finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Erro ao salvar feedback na lista de relatórios.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RespondingPatientPreview() {
    IntelimedTheme {
        RespondingPatient(
            pacienteId = "TESTE",
            relatorioId = "TESTE",
            somenteVisualizar = false
        )
    }
}
