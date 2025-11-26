/**
 * Tela que lista os pacientes para o Chat do Médico
 */
package br.com.ibm.intelimed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AcceptedChatsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isMedico = intent.getBooleanExtra("isMedico", true)

        setContent {
            AcceptedChatsScreen(
                isMedico = isMedico,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptedChatsScreen(
    isMedico: Boolean,
    onBack: (() -> Unit)? = null
) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var listaPacientes by remember { mutableStateOf<List<PacienteInfo>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LaunchedEffect(userId, isMedico) {

        if (userId == null) {
            // sem usuário logado → nada pra listar
            listaPacientes = emptyList()
            loading = false
        } else {
            loading = true

            if (isMedico) {
                // MÉDICO → listar pacientes aceitos
                db.collection("solicitacoes")
                    .whereEqualTo("medicoId", userId)
                    .whereEqualTo("status", "aceito")
                    .addSnapshotListener { result, _ ->

                        if (result == null) {
                            listaPacientes = emptyList()
                            loading = false
                        } else {
                            val solicitacoes = result.documents

                            if (solicitacoes.isEmpty()) {
                                listaPacientes = emptyList()
                                loading = false
                            } else {
                                val temp = mutableListOf<PacienteInfo>()
                                var loaded = 0

                                for (sol in solicitacoes) {
                                    val pacienteId = sol.getString("pacienteId")
                                    if (pacienteId != null) {
                                        db.collection("paciente")
                                            .document(pacienteId)
                                            .get()
                                            .addOnSuccessListener { pDoc ->
                                                temp.add(
                                                    PacienteInfo(
                                                        uid = pacienteId,
                                                        nome = pDoc.getString("nome") ?: "Paciente"
                                                    )
                                                )
                                                loaded++
                                                if (loaded == solicitacoes.size) {
                                                    listaPacientes = temp
                                                    loading = false
                                                }
                                            }
                                    } else {
                                        // se pacienteId for nulo, só ignora esse doc
                                        loaded++
                                        if (loaded == solicitacoes.size) {
                                            listaPacientes = temp
                                            loading = false
                                        }
                                    }
                                }
                            }
                        }
                    }

            } else {
                // PACIENTE → listar médicos aceitos
                db.collection("solicitacoes")
                    .whereEqualTo("pacienteId", userId)
                    .whereEqualTo("status", "aceito")
                    .addSnapshotListener { result, _ ->

                        if (result == null) {
                            listaPacientes = emptyList()
                            loading = false
                        } else {
                            val solicitacoes = result.documents

                            if (solicitacoes.isEmpty()) {
                                listaPacientes = emptyList()
                                loading = false
                            } else {
                                val temp = mutableListOf<PacienteInfo>()
                                var loaded = 0

                                for (sol in solicitacoes) {
                                    val medicoId = sol.getString("medicoId")
                                    if (medicoId != null) {
                                        db.collection("medico")
                                            .document(medicoId)
                                            .get()
                                            .addOnSuccessListener { mDoc ->
                                                temp.add(
                                                    PacienteInfo(
                                                        uid = medicoId,
                                                        nome = mDoc.getString("nome") ?: "Médico"
                                                    )
                                                )
                                                loaded++
                                                if (loaded == solicitacoes.size) {
                                                    listaPacientes = temp
                                                    loading = false
                                                }
                                            }
                                    } else {
                                        // se medicoId for nulo, só ignora esse doc
                                        loaded++
                                        if (loaded == solicitacoes.size) {
                                            listaPacientes = temp
                                            loading = false
                                        }
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

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
                    Text("Conversas", color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF007C7A)
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
        ) {

            Text(
                if (isMedico) "Pacientes em atendimento" else "Médicos em atendimento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007C7A)
            )

            Spacer(Modifier.height(12.dp))

            when {
                loading -> CircularProgressIndicator(color = Color(0xFF007C7A))

                listaPacientes.isEmpty() ->
                    Text("Nenhuma conversa disponível no momento. Aguarde um médico aceitar você ou solicite um novo atendimento.", color = Color.Gray)

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaPacientes) { paciente ->
                        ChatListItem(
                            paciente = paciente,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    paciente: PacienteInfo,
    context: Context
) {
    val uidAtual = FirebaseAuth.getInstance().currentUser?.uid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(context, ChatActivity::class.java).apply {
                    putExtra("uidAtual", uidAtual)
                    putExtra("uidOutro", paciente.uid)
                    putExtra("nomeOutro", paciente.nome)
                }
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(Color(0xFFF7FDFC))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                paciente.nome,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007C7A)
            )
            Text(
                "Clique para abrir o chat",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

data class PacienteInfo(
    val uid: String,
    val nome: String
)
