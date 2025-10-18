// TELA DE REGISTRO DE SINTOMAS DO PACIENTE

package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.auth

class SymptomLogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                RegistroSintomas()
            }
        }
    }
}

fun savePatientSymptoms(
    context: Context,
    sentimento: String,
    dormiuBem: String,
    cansaco: String,
    alimentacao: String,
    hidratacao: String,
    sentiuDor: String,
    intensidadeDor: String,
    localDor: String,
    tipoDorMudou: String,
    febre: String,
    temperatura: String,
    enjoo: String,
    tontura: String,
    sangramento: String,
    fezCicatrizacao: String,
    estadoCicatrizacao: String,
    tomouMedicacao: String,
    qualMedicacao: String,
    horarioMedicacao: String,
    observacoes: String,
    onSuccess: () -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val pacienteId = auth.currentUser?.uid

    if (pacienteId != null) {
        val sintomas = hashMapOf(
            "dataRegistro" to System.currentTimeMillis(),
            "sentimento" to sentimento,
            "dormiuBem" to dormiuBem,
            "cansaco" to cansaco,
            "alimentacao" to alimentacao,
            "hidratacao" to hidratacao,
            "sentiuDor" to sentiuDor,
            "intensidadeDor" to intensidadeDor,
            "localDor" to localDor,
            "tipoDorMudou" to tipoDorMudou,
            "febre" to febre,
            "temperatura" to temperatura,
            "enjoo" to enjoo,
            "tontura" to tontura,
            "sangramento" to sangramento,
            "fezCicatrizacao" to fezCicatrizacao,
            "estadoCicatrizacao" to estadoCicatrizacao,
            "tomouMedicacao" to tomouMedicacao,
            "qualMedicacao" to qualMedicacao,
            "horarioMedicacao" to horarioMedicacao,
            "observacoes" to observacoes
        )

        db.collection("paciente")
            .document(pacienteId)
            .collection("sintomas")
            .add(sintomas)
            .addOnSuccessListener {
                Toast.makeText(context, "Symptoms saved successfully!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Error: patient not authenticated.", Toast.LENGTH_SHORT).show()
    }
}

// Função que busca as especialidades dos médicos no Firestore
fun getEspecialidades(
    onSucesso: (List<String>) -> Unit,
    onErro: (Exception) -> Unit
) {
    val db = Firebase.firestore

    db.collection("medico")
        .get()
        .addOnSuccessListener { task ->
            val lista = mutableListOf<String>()

            for (documento in task) {
                val esp = documento.getString("especialidade")
                if (!esp.isNullOrEmpty()) lista.add(esp)
            }

            onSucesso(lista.distinct()) // remove duplicadas
        }
        .addOnFailureListener { erro ->
            onErro(erro)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroSintomas() {

    val scrollState = rememberScrollState()

    // ===== Estados do formulário =====
    var sentimento by remember { mutableStateOf(TextFieldValue("")) }
    var dormiuBem by remember { mutableStateOf("") }
    var cansaco by remember { mutableStateOf("") }
    var alimentacao by remember { mutableStateOf("") }
    var hidratacao by remember { mutableStateOf("") }

    var sentiuDor by remember { mutableStateOf("") }
    var intensidadeDor by remember { mutableStateOf(TextFieldValue("")) }
    var localDor by remember { mutableStateOf(TextFieldValue("")) }
    var tipoDorMudou by remember { mutableStateOf("") }

    var febre by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf(TextFieldValue("")) }
    var enjoo by remember { mutableStateOf("") }
    var tontura by remember { mutableStateOf("") }
    var sangramento by remember { mutableStateOf("") }

    var fezCicatrizacao by remember { mutableStateOf("") }
    var estadoCicatrizacao by remember { mutableStateOf(TextFieldValue("")) }

    var tomouMedicacao by remember { mutableStateOf("") }
    var qualMedicacao by remember { mutableStateOf(TextFieldValue("")) }
    var horarioMedicacao by remember { mutableStateOf(TextFieldValue("")) }

    var observacoes by remember { mutableStateOf(TextFieldValue("")) }

    // ===== Estados para especialidades =====
    var expanded by remember { mutableStateOf(false) }
    var selectedEspecialidade by remember { mutableStateOf("") }
    var especialidades by remember { mutableStateOf(listOf<String>()) }
    var carregando by remember { mutableStateOf(true) }
    var erroFirebase by remember { mutableStateOf("") }

    // Quando a tela abrir, busca as especialidades do Firestore
    LaunchedEffect(Unit) {
        getEspecialidades(
            onSucesso = { lista ->
                especialidades = lista
                carregando = false
            },
            onErro = { erro ->
                erroFirebase = "Erro ao carregar: ${erro.message}"
                carregando = false
            }
        )
    }

    // ===== Layout =====
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registro de Sintomas",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF009688),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            // ===================== DADOS GERAIS =====================
            Text(
                text = "DADOS GERAIS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // 🔹 Dropdown de especialidades
            if (carregando) {
                CircularProgressIndicator(
                    color = Color(0xFF007C7A),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else if (erroFirebase.isNotEmpty()) {
                Text(
                    text = erroFirebase,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedEspecialidade,
                        onValueChange = {},
                        label = { Text("Selecione a especialidade") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded)
                                        Icons.Default.KeyboardArrowUp
                                    else
                                        Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        especialidades.forEach { especialidade ->
                            DropdownMenuItem(
                                text = { Text(especialidade) },
                                onClick = {
                                    selectedEspecialidade = especialidade
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ===== Campo Sentimento =====
            OutlinedTextField(
                value = sentimento,
                onValueChange = { sentimento = it },
                label = { Text("Como você está se sentindo hoje? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===== Perguntas gerais =====
            PerguntaSimNao("Dormiu bem na última noite?", dormiuBem) { dormiuBem = it }
            PerguntaSimNao("Sentiu cansaço excessivo?", cansaco) { cansaco = it }
            PerguntaSimNao("Está se alimentando normalmente?", alimentacao) { alimentacao = it }
            PerguntaSimNao("Está se hidratando bem?", hidratacao) { hidratacao = it }

            // ===================== DOR E DESCONFORTO =====================
            Text(
                text = "DOR E DESCONFORTO",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao("Está sentindo dor atualmente?", sentiuDor) { sentiuDor = it }

            OutlinedTextField(
                value = intensidadeDor,
                onValueChange = { intensidadeDor = it },
                label = { Text("Qual a intensidade da dor (0 a 10)? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = localDor,
                onValueChange = { localDor = it },
                label = { Text("Onde está a dor? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            PerguntaSimNao("O tipo de dor mudou desde a última vez?", tipoDorMudou) { tipoDorMudou = it }

            // ===================== SINTOMAS FÍSICOS =====================
            Text(
                text = "SINTOMAS FÍSICOS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao("Teve febre nas últimas 24h?", febre) { febre = it }

            OutlinedTextField(
                value = temperatura,
                onValueChange = { temperatura = it },
                label = { Text("Temperatura medida (°C) (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            PerguntaSimNao("Teve enjoo, vômito ou diarreia?", enjoo) { enjoo = it }
            PerguntaSimNao("Apresentou tontura ou fraqueza?", tontura) { tontura = it }
            PerguntaSimNao("Teve sangramento, secreção ou inchaço?", sangramento) { sangramento = it }

            // ===================== CICATRIZAÇÃO =====================
            Text(
                text = "CICATRIZAÇÃO / FERIMENTOS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao("Fez algum procedimento ou cicatrização recente?", fezCicatrizacao) { fezCicatrizacao = it }

            OutlinedTextField(
                value = estadoCicatrizacao,
                onValueChange = { estadoCicatrizacao = it },
                label = { Text("Como está a cicatrização? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===================== MEDICAÇÃO =====================
            Text(
                text = "MEDICAÇÃO",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao("Tomou algum medicamento nas últimas 24h?", tomouMedicacao) { tomouMedicacao = it }

            OutlinedTextField(
                value = qualMedicacao,
                onValueChange = { qualMedicacao = it },
                label = { Text("Qual medicação? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = horarioMedicacao,
                onValueChange = { horarioMedicacao = it },
                label = { Text("Em qual horário? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===================== OBSERVAÇÕES =====================
            Text(
                text = "OBSERVAÇÕES GERAIS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = observacoes,
                onValueChange = { observacoes = it },
                label = { Text("Observações adicionais (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // ===================== BOTÃO FINAL =====================
            var mostrarDialogo by remember { mutableStateOf(false) }
            val context = LocalContext.current

            if (mostrarDialogo) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogo = false },
                    title = { Text("Confirmar envio") },
                    text = { Text("Deseja confirmar o envio das respostas?") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarDialogo = false
                            val intent = Intent(context, ConfirmationActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Text("Sim")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogo = false }) {
                            Text("Não")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    savePatientSymptoms(
                        context = context,
                        sentimento = sentimento.text,
                        dormiuBem = dormiuBem,
                        cansaco = cansaco,
                        alimentacao = alimentacao,
                        hidratacao = hidratacao,
                        sentiuDor = sentiuDor,
                        intensidadeDor = intensidadeDor.text,
                        localDor = localDor.text,
                        tipoDorMudou = tipoDorMudou,
                        febre = febre,
                        temperatura = temperatura.text,
                        enjoo = enjoo,
                        tontura = tontura,
                        sangramento = sangramento,
                        fezCicatrizacao = fezCicatrizacao,
                        estadoCicatrizacao = estadoCicatrizacao.text,
                        tomouMedicacao = tomouMedicacao,
                        qualMedicacao = qualMedicacao.text,
                        horarioMedicacao = horarioMedicacao.text,
                        observacoes = observacoes.text,
                        onSuccess = {
                            // Mostra o diálogo de confirmação só depois que salvar no Firestore
                            mostrarDialogo = true
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007C7A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Enviar respostas", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PerguntaSimNao(
    pergunta: String,
    resposta: String,
    onResposta: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(pergunta)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = resposta == "Sim",
                onClick = { onResposta("Sim") },
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF007C7A))
            )
            Text("Sim")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = resposta == "Não",
                onClick = { onResposta("Não") },
                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF007C7A))
            )
            Text("Não")
        }
    }
}

@Preview
@Composable
fun RegistroSintomasPreview() {
    IntelimedTheme {
        RegistroSintomas()
    }
}
