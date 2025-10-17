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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroSintomas() {

    val scrollState = rememberScrollState()

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
                    containerColor = Color(0xFF009688), // tom teal mais moderno
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


            Text(
                text = "DADOS GERAIS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            var expanded by remember { mutableStateOf(false) }
            var selectedEspecialidade by remember { mutableStateOf("") }

            // Lista vazia (depois você vai preencher do banco)
            val especialidades = listOf<String>()

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

            OutlinedTextField(
                value = sentimento,
                onValueChange = { sentimento = it },
                label = { Text("Como você está se sentindo hoje? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            PerguntaSimNao(
                pergunta = "Dormiu bem na última noite?",
                resposta = dormiuBem,
                onResposta = { dormiuBem = it }
            )

            PerguntaSimNao(
                pergunta = "Sentiu cansaço excessivo?",
                resposta = cansaco,
                onResposta = { cansaco = it }
            )

            PerguntaSimNao(
                pergunta = "Está se alimentando normalmente?",
                resposta = alimentacao,
                onResposta = { alimentacao = it }
            )

            PerguntaSimNao(
                pergunta = "Está se hidratando bem?",
                resposta = hidratacao,
                onResposta = { hidratacao = it }
            )

            Text(
                text = "DOR E DESCONFORTO",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao(
                pergunta = "Está sentindo dor atualmente?",
                resposta = sentiuDor,
                onResposta = { sentiuDor = it }
            )

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

            PerguntaSimNao(
                pergunta = "O tipo de dor mudou desde a última vez?",
                resposta = tipoDorMudou,
                onResposta = { tipoDorMudou = it }
            )

            Text(
                text = "SINTOMAS FÍSICOS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao(
                pergunta = "Teve febre nas últimas 24h?",
                resposta = febre,
                onResposta = { febre = it }
            )

            OutlinedTextField(
                value = temperatura,
                onValueChange = { temperatura = it },
                label = { Text("Temperatura medida (°C) (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            PerguntaSimNao(
                pergunta = "Teve enjoo, vômito ou diarreia?",
                resposta = enjoo,
                onResposta = { enjoo = it }
            )

            PerguntaSimNao(
                pergunta = "Apresentou tontura ou fraqueza?",
                resposta = tontura,
                onResposta = { tontura = it }
            )

            PerguntaSimNao(
                pergunta = "Teve sangramento, secreção ou inchaço?",
                resposta = sangramento,
                onResposta = { sangramento = it }
            )

            Text(
                text = "CICATRIZAÇÃO / FERIMENTOS",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao(
                pergunta = "Fez algum procedimento ou cicatrização recente?",
                resposta = fezCicatrizacao,
                onResposta = { fezCicatrizacao = it }
            )

            OutlinedTextField(
                value = estadoCicatrizacao,
                onValueChange = { estadoCicatrizacao = it },
                label = { Text("Como está a cicatrização? (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "MEDICAÇÃO",
                color = Color(0xFF007C7A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PerguntaSimNao(
                pergunta = "Tomou algum medicamento nas últimas 24h?",
                resposta = tomouMedicacao,
                onResposta = { tomouMedicacao = it }
            )

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
                onClick = { mostrarDialogo = true },
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
