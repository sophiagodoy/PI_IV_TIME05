/**
 * Tela de lista de pacientes do médico e solicitações
 */

package br.com.ibm.intelimed

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class PatientListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                PatientList(onBack = { finish() })
            }
        }
    }
}

// ---------------------- MODELOS ----------------------

data class Paciente(
    val solicitacaoId: String = "",
    val pacienteId: String = "",
    val nome: String = "",
    val email: String = ""
)

data class Solicitacao(
    val solicitacaoId: String = "",
    val pacienteId: String = "",
    val nome: String = "",
    val email: String = "",
    val status: String = ""
)

// ---------------------- FIREBASE ----------------------

fun listenAcceptedPatients(medicoId: String, onResult: (List<Paciente>) -> Unit) {
    val db = Firebase.firestore

    db.collection("solicitacoes")
        .whereEqualTo("medicoId", medicoId)
        .whereEqualTo("status", "aceito")
        .addSnapshotListener { task, _ ->
            if (task == null || task.isEmpty) {
                onResult(emptyList())
                return@addSnapshotListener
            }

            val lista = mutableListOf<Paciente>()
            var contador = task.size()

            task.forEach { solicitacao ->

                val pacienteId = solicitacao.getString("pacienteId") ?: ""
                val solicitacaoId = solicitacao.id

                db.collection("paciente").document(pacienteId).get()
                    .addOnSuccessListener { paciente ->

                        lista.add(
                            Paciente(
                                solicitacaoId = solicitacaoId,
                                pacienteId = pacienteId,
                                nome = paciente.getString("nome") ?: "",
                                email = paciente.getString("email") ?: ""
                            )
                        )

                        contador--
                        if (contador == 0) onResult(lista)
                    }
            }
        }
}

fun listenSolicitacoesPendentes(medicoId: String, onResult: (List<Solicitacao>) -> Unit) {
    val db = Firebase.firestore

    db.collection("solicitacoes")
        .whereEqualTo("medicoId", medicoId)
        .whereEqualTo("status", "pendente")
        .addSnapshotListener { task, _ ->

            if (task == null || task.isEmpty) {
                onResult(emptyList())
                return@addSnapshotListener
            }

            val lista = mutableListOf<Solicitacao>()
            var contador = task.size()

            task.forEach { doc ->

                val pacienteId = doc.getString("pacienteId") ?: ""

                db.collection("paciente").document(pacienteId).get()
                    .addOnSuccessListener { paciente ->

                        lista.add(
                            Solicitacao(
                                solicitacaoId = doc.id,
                                pacienteId = pacienteId,
                                nome = paciente.getString("nome") ?: "",
                                email = paciente.getString("email") ?: "",
                                status = doc.getString("status") ?: ""
                            )
                        )

                        contador--
                        if (contador == 0) onResult(lista)
                    }
            }
        }
}

fun aceitarSolicitacao(solicitacaoId: String) {
    Firebase.firestore.collection("solicitacoes")
        .document(solicitacaoId)
        .update("status", "aceito")
}

fun recusarSolicitacao(solicitacaoId: String) {
    Firebase.firestore.collection("solicitacoes")
        .document(solicitacaoId)
        .update("status", "recusado")
}

// encerrar atendimento depois de aceito
fun encerrarAtendimento(solicitacaoId: String) {
    Firebase.firestore.collection("solicitacoes")
        .document(solicitacaoId)
        .update("status", "encerrado")
}

// ---------------------- TELA PRINCIPAL ----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientList(onBack: (() -> Unit)? = null) {

    val teal = Color(0xFF007C7A)
    var abaSelecionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        "Meus Pacientes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
        ) {

            // -------- ABAS PERSONALIZADAS --------
            TabRow(
                selectedTabIndex = abaSelecionada,
                containerColor = Color.White,
                contentColor = teal,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[abaSelecionada]),
                        color = teal
                    )
                }
            ) {

                Tab(
                    selected = abaSelecionada == 0,
                    onClick = { abaSelecionada = 0 },
                    text = {
                        Text(
                            "Pacientes",
                            fontWeight = FontWeight.Bold,
                            color = if (abaSelecionada == 0) teal else Color.Gray
                        )
                    }
                )

                Tab(
                    selected = abaSelecionada == 1,
                    onClick = { abaSelecionada = 1 },
                    text = {
                        Text(
                            "Solicitações",
                            fontWeight = FontWeight.Bold,
                            color = if (abaSelecionada == 1) teal else Color.Gray
                        )
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            when (abaSelecionada) {
                0 -> ListaPacientesAceitos()
                1 -> ListaSolicitacoesPendentes()
            }
        }
    }
}

// ---------------------- LISTA PACIENTES ----------------------

@Composable
fun ListaPacientesAceitos() {
    val medicoId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        listenAcceptedPatients(medicoId) { lista ->
            pacientes = lista
            carregando = false
        }
    }

    when {
        carregando -> CircularProgressIndicator(color = Color(0xFF007C7A))

        pacientes.isEmpty() ->
            Text("Nenhum paciente aceito ainda.", color = Color.Gray)

        else -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(pacientes) { paciente ->
                PacienteCardModern(paciente)
            }
        }
    }
}

// ---------------------- LISTA SOLICITAÇÕES ----------------------

@Composable
fun ListaSolicitacoesPendentes() {
    val medicoId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    var solicitacoes by remember { mutableStateOf<List<Solicitacao>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        listenSolicitacoesPendentes(medicoId) { lista ->
            solicitacoes = lista
            carregando = false
        }
    }

    when {
        carregando -> CircularProgressIndicator(color = Color(0xFF007C7A))

        solicitacoes.isEmpty() ->
            Text("Nenhuma solicitação pendente.", color = Color.Gray)

        else -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(solicitacoes) { solicitacao ->
                CardSolicitacao(solicitacao)
            }
        }
    }
}

// ---------------------- CARD SOLICITAÇÃO ----------------------

@Composable
fun CardSolicitacao(solicitacao: Solicitacao) {

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.padding(
                start = 18.dp,
                top = 18.dp,
                end = 18.dp,
                bottom = 24.dp
            )
        ) {
            Column {
                Text(
                    solicitacao.nome,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007C7A)
                )
                Text(solicitacao.email, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { aceitarSolicitacao(solicitacao.solicitacaoId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007C7A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Aceitar")
                }

                Button(
                    onClick = { recusarSolicitacao(solicitacao.solicitacaoId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Recusar", color = Color.White)
                }
            }
        }
    }
}

// ---------------------- CARD PACIENTE ----------------------

@Composable
fun PacienteCardModern(paciente: Paciente) {
    val context = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Dialog de confirmação para encerrar atendimento
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Encerrar atendimento") },
            text = {
                Text("Tem certeza que deseja encerrar o atendimento de ${paciente.nome}?")
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    encerrarAtendimento(paciente.solicitacaoId)
                    Toast.makeText(
                        context,
                        "Atendimento encerrado para ${paciente.nome}.",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text("Sim")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = paciente.nome,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF007C7A)
                    )
                    Text(
                        text = paciente.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (paciente.solicitacaoId.isNotBlank()) {
                    TextButton(
                        onClick = { mostrarDialogo = true }
                    ) {
                        Text(
                            "Encerrar",
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PatientListPreview() {
    IntelimedTheme {
        PatientList()
    }
}
