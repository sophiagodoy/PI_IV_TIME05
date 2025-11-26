package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class MainPatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                PatientHome()
            }
        }
    }
}

/**
 * Busca o nome do usuário logado no Firebase:
 *  - Se existir na coleção "paciente", usa esse nome.
 *  - Caso contrário, tenta na coleção "medico".
 *  - Se der erro em qualquer ponto, retorna "Usuário".
 */
fun buscarNomeUsuario(onResult: (String) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser ?: return
    val uid = currentUser.uid

    db.collection("paciente").document(uid).get()
        .addOnSuccessListener { doc ->
            if (doc.exists()) {
                val nome = doc.getString("nome") ?: "Paciente"
                // Exibo só o primeiro nome
                onResult(nome.split(" ").firstOrNull() ?: nome)
            } else {
                // Se não for paciente, tento como médico
                db.collection("medico").document(uid).get()
                    .addOnSuccessListener { medicoDoc ->
                        val nome = medicoDoc.getString("nome") ?: "Médico"
                        onResult(nome.split(" ").firstOrNull() ?: nome)
                    }
                    .addOnFailureListener { onResult("Usuário") }
            }
        }
        .addOnFailureListener {
            onResult("Usuário")
        }
}

/**
 * Verifica se o paciente já tem um médico vinculado.
 *
 * Critério:
 *  - Existe registro em "solicitacoes" com:
 *      pacienteId = uid atual
 *      status = "aceito"
 *
 * Se tiver pelo menos uma, considero que ele já tem médico.
 */
fun verificarVinculoMedico(onResult: (Boolean) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val uid = currentUser?.uid

    if (uid == null) {
        onResult(false)
        return
    }

    db.collection("solicitacoes")
        .whereEqualTo("pacienteId", uid)
        .whereEqualTo("status", "aceito")
        .get()
        .addOnSuccessListener { snapshot ->
            onResult(!snapshot.isEmpty)
        }
        .addOnFailureListener {
            // Em caso de erro, por segurança não deixo passar
            onResult(false)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientHome() {
    val teal = Color(0xFF007C7A)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Nome exibido no "Olá, {nome}"
    var nome by remember { mutableStateOf("Usuário") }

    // Flags de vínculo com médico
    var temMedicoVinculado by remember { mutableStateOf(false) }
    var carregandoVinculoMedico by remember { mutableStateOf(true) }

    // Diálogo de confirmação de saída (bottom nav)
    var mostrarDialogoSair by remember { mutableStateOf(false) }

    // Diálogo avisando que precisa escolher médico antes de registrar sintomas
    var mostrarDialogoPrecisaMedico by remember { mutableStateOf(false) }

    /**
     * Carrega nome e vínculo na primeira montagem da tela.
     * Isso roda uma vez quando o Composable entra na composição.
     */
    LaunchedEffect(Unit) {
        buscarNomeUsuario { resultado ->
            nome = resultado
        }

        verificarVinculoMedico { temMedico ->
            temMedicoVinculado = temMedico
            carregandoVinculoMedico = false
        }
    }

    /**
     * Observa o ciclo de vida da Activity.
     * Sempre que a tela volta para o estado RESUMED (por exemplo,
     * depois de ir para SelectDoctorActivity e voltar),
     * eu revalido o vínculo com o médico.
     */
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                carregandoVinculoMedico = true
                verificarVinculoMedico { temMedico ->
                    temMedicoVinculado = temMedico
                    carregandoVinculoMedico = false
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        // Limpeza do observer quando o Composable sai da composição
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "INTELIMED",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal),
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = {}, // por enquanto só existe essa aba
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Início") }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { mostrarDialogoSair = true },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair") },
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
                text = "Home",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Olá, $nome",
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            // Linha com os dois primeiros cards (Chat / Escolher Médico)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Chat", Icons.AutoMirrored.Filled.Chat) {
                    context.startActivity(
                        Intent(context, AcceptedChatsActivity::class.java)
                            .putExtra("isMedico", false)
                    )
                }

                OptionCard("Escolher Médico", Icons.Default.Description) {
                    val intent = Intent(context, SelectDoctorActivity::class.java)
                    context.startActivity(intent)
                }
            }

            // Linha com Orientação Fixa / Feedback Médico
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OptionCard("Orientação Fixa", Icons.AutoMirrored.Filled.ArrowForward) {
                    val intent = Intent(context, MedicalGuidanceActivity::class.java)
                    context.startActivity(intent)
                }

                OptionCard("Feedback Médico", Icons.Default.Warning) {
                    val intent = Intent(context, PatientFeedbackListActivity::class.java)
                    context.startActivity(intent)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            /**
             * Botão principal do fluxo do paciente.
             * Só libera o registro de sintomas se:
             *  - Já tiver carregado a info do vínculo, e
             *  - Houver médico aceito.
             */
            Button(
                onClick = {
                    // Evito clicar enquanto ainda estou consultando o Firestore
                    if (carregandoVinculoMedico) return@Button

                    if (!temMedicoVinculado) {
                        // Não tem médico ainda: aviso que precisa escolher primeiro
                        mostrarDialogoPrecisaMedico = true
                    } else {
                        // Tem médico vinculado: segue para o formulário de sintomas
                        val intent = Intent(context, SymptomLogActivity::class.java)
                        context.startActivity(intent)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = teal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Registrar sintomas hoje", color = Color.White, fontSize = 16.sp)
            }

            // Mensagem de orientação extra, só aparece se já sei que não tem médico
            if (!carregandoVinculoMedico && !temMedicoVinculado) {
                Text(
                    text = "Para registrar seus sintomas, primeiro escolha um médico para te acompanhar.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        // Diálogo de confirmação de logout
        if (mostrarDialogoSair) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoSair = false },
                title = { Text("Confirmar saída") },
                text = { Text("Deseja realmente sair da sua conta?") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogoSair = false
                        val intent = Intent(context, AuthChoiceActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }) {
                        Text("Sim", color = teal)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoSair = false }) {
                        Text("Não", color = Color.Gray)
                    }
                }
            )
        }

        // Diálogo avisando que precisa escolher médico antes de registrar sintomas
        if (mostrarDialogoPrecisaMedico) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoPrecisaMedico = false },
                title = { Text("Escolha um médico primeiro") },
                text = {
                    Text(
                        "Para registrar seus sintomas, você precisa primeiro escolher um médico " +
                                "que ficará responsável pelo seu acompanhamento."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogoPrecisaMedico = false
                        // Levo direto para a tela de seleção de médico
                        val intent = Intent(context, SelectDoctorActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("Escolher médico", color = teal)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoPrecisaMedico = false }) {
                        Text("Agora não", color = Color.Gray)
                    }
                }
            )
        }
    }
}

/**
 * Card de opção usado na Home (Chat, Escolher Médico, etc).
 * Usei RowScope para poder aplicar weight na Row.
 */
@Composable
fun RowScope.OptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val teal = Color(0xFF007C7A)
    val cardBg = Color(0xFFF7FDFC)

    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = teal, modifier = Modifier.size(30.dp))
            Text(title, color = Color.Black, fontSize = 14.sp)
        }
    }
}

@Preview
@Composable
fun MainPatientActivityPreview() {
    IntelimedTheme {
        PatientHome()
    }
}
