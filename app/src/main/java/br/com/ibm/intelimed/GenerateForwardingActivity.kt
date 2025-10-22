package br.com.ibm.intelimed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

class GenerateForwardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    GenerateForwardingScreen(
                        onCancel = { finish() },
                        onGenerate = { /* somente UI por enquanto */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateForwardingScreen(
    onCancel: () -> Unit,
    onGenerate: () -> Unit,
) {
    val context = LocalContext.current
    var patientName by remember { mutableStateOf("") }
    var patientId by remember { mutableStateOf("") }

    val specialties = listOf("Ortopedia", "Cardiologia", "Dermatologia", "Neurologia", "Ginecologia")
    var specialtyExpanded by remember { mutableStateOf(false) }
    var specialty by remember { mutableStateOf(specialties.first()) }

    val priorities = listOf("Baixa", "Média", "Alta", "Urgente")
    var priorityIndex by remember { mutableStateOf(1) }

    var targetClinic by remember { mutableStateOf("") }
    var targetDoctor by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attachName by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gerar Encaminhamento", maxLines = 1, overflow = TextOverflow.Ellipsis) }) },
        bottomBar = {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                Button(onClick = {
                    if (patientName.isBlank() || patientId.isBlank()) {
                        Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                    } else {
                        onGenerate()
                    }
                }, modifier = Modifier.weight(2f)) { Text("Gerar encaminhamento") }
            }
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Dados do paciente", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                patientName, { patientName = it },
                label = { Text("Nome do paciente") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                patientId, { patientId = it },
                label = { Text("Documento / Prontuário") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Text("Destino", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = specialtyExpanded,
                onExpandedChange = { specialtyExpanded = !specialtyExpanded }
            ) {
                OutlinedTextField(
                    value = specialty, onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = specialtyExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = specialtyExpanded,
                    onDismissRequest = { specialtyExpanded = false }
                ) {
                    specialties.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { specialty = it; specialtyExpanded = false }
                        )
                    }
                }
            }
            OutlinedTextField(
                targetClinic, { targetClinic = it },
                label = { Text("Clínica/Hospital (opcional)") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                targetDoctor, { targetDoctor = it },
                label = { Text("Médico (opcional)") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )

            Text("Prioridade", style = MaterialTheme.typography.titleMedium)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                priorities.forEachIndexed { i, label ->
                    SegmentedButton(
                        selected = priorityIndex == i,
                        onClick = { priorityIndex = i },
                        shape = SegmentedButtonDefaults.itemShape(i, priorities.size)
                    ) { Text(label) }
                }
            }

            OutlinedTextField(
                dateText, { dateText = it },
                label = { Text("Data sugerida (DD/MM/AAAA) – opcional") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                notes, { notes = it },
                label = { Text("Justificativa / Observações") },
                minLines = 4, modifier = Modifier.fillMaxWidth()
            )

            OutlinedCard(
                Modifier.fillMaxWidth().clickable {
                    attachName = "exame_hemograma.pdf"
                }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Anexos (opcional)", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(attachName ?: "Toque para anexar (ex.: pedido, exame, foto)")
                }
            }

            Spacer(Modifier.height(56.dp))
        }
    }
}
