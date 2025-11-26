/**
 * Tela de registro de sintomas do Paciente
 */

package br.com.ibm.intelimed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroSintomas() {

    val scrollState = rememberScrollState()
    val context = LocalContext.current

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
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainPatientActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        (context as? android.app.Activity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF007C7A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
            verticalArrangement = Arrangement.spacedBy(18.dp) // era 22.dp
        ) {

            Text(
                text = "As respostas desse registro serão enviadas ao médico que você escolheu para te acompanhar.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "DADOS GERAIS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

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

                            Toast.makeText(context, "Sintomas enviados ao médico!", Toast.LENGTH_LONG).show()

                            val intent = Intent(context, MainPatientActivity::class.java)
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
                        onSuccess = { pacienteId, relatorioId ->
                            Toast.makeText(context, "Sintomas enviados ao médico!", Toast.LENGTH_LONG).show()

                            val intent = Intent(context, MainPatientActivity::class.java)
                            context.startActivity(intent)
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
    onSuccess: (pacienteId: String, relatorioId: String) -> Unit
) {
    val auth = Firebase.auth
    val db = Firebase.firestore
    val pacienteId = auth.currentUser?.uid

    if (pacienteId == null) {
        Toast.makeText(context, "Error: patient not authenticated.", Toast.LENGTH_SHORT).show()
        return
    }

    // Gera um ID único e reutiliza em PACIENTE e MÉDICO
    val relatorioId = db.collection("paciente")
        .document(pacienteId)
        .collection("sintomas")
        .document().id

    val sintomas = hashMapOf(
        "pacienteId" to pacienteId,
        "relatorioId" to relatorioId,
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

    // Salva no médico (se houver médico vinculado)
    db.collection("solicitacoes")
        .whereEqualTo("pacienteId", pacienteId)
        .whereEqualTo("status", "aceito")
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val medicoId = result.documents.first().getString("medicoId")!!

                db.collection("medico")
                    .document(medicoId)
                    .collection("relatorios")
                    .document(relatorioId)
                    .set(sintomas)
            }
        }

    // Salva no paciente
    db.collection("paciente")
        .document(pacienteId)
        .collection("sintomas")
        .document(relatorioId)
        .set(sintomas)
        .addOnSuccessListener {
            onSuccess(pacienteId, relatorioId)
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}


// Função que busca as especialidades dos médicos no Firestore
fun getEspecialidades(
    onSucesso: (List<String>) -> Unit,   // Função que será chamada quando tudo der certo
    onErro: (Exception) -> Unit          // Função que será chamada caso dê erro
) {
    val db = Firebase.firestore          // Obtém a instância do Firestore

    db.collection("medico")              // Acessa a coleção "medico"
        .get()                           // Busca todos os documentos dessa coleção
        .addOnSuccessListener { task ->  // Quando a busca for bem-sucedida…

            val lista = mutableListOf<String>()  // Lista onde vamos colocar TODAS especialidades encontradas

            for (documento in task) {    // Para cada documento retornado…

                // Pega o campo "especialidade" — pode ser String OU Array
                val espCampo = documento.get("especialidade")

                // Converte o campo para uma lista de Strings independente do tipo
                val espList = when (espCampo) {

                    is String ->          // Caso o campo seja UMA STRING apenas
                        listOf(espCampo)  // transforma em lista com 1 item

                    is List<*> ->         // Caso seja uma lista/array
                        espCampo.map { it.toString() }  // converte cada item para String

                    else ->               // Caso seja nulo ou tipo inesperado
                        emptyList()       // retorna lista vazia
                }

                // Adiciona todas as especialidades desse médico à lista geral
                lista.addAll(espList)
            }

            // Remove duplicatas e retorna para quem chamou a função
            onSucesso(lista.distinct())
        }
        .addOnFailureListener { erro ->   // Se ocorrer erro na leitura do Firestore…
            onErro(erro)                  // passa o erro para quem chamou a função
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

@Preview(showBackground = true)
@Composable
fun RegistroSintomasPreview() {
    IntelimedTheme {
        RegistroSintomas()
    }
}
