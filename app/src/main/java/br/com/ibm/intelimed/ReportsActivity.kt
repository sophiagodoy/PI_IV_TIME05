package br.com.ibm.intelimed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta de cores utilizada na tela, mantendo o padrão do app
private val Teal = Color(0xFF007C7A)
private val CardBg = Color(0xFFFFFFFF)
private val FeedbackGreen = Color(0xFF4CAF50)
private val WaitingRed = Color(0xFFF44336)

// Modelo que representa um relatório individual do paciente
data class Report(
    val date: String = "",       // Data do relatório
    val symptoms: String = "",   // Sintomas informados pelo paciente
    val feedback: String = ""    // Feedback retornado pelo médico (caso exista)
)

// Activity responsável pela tela de relatórios
class ReportsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o conteúdo da tela usando Jetpack Compose
        setContent {
            ReportsScreen()
        }
    }
}

@Composable
fun ReportsScreen() {
    // Lista observável de relatórios exibidos na tela
    val reports = remember { mutableStateListOf<Report>() }

    // Guarda o filtro atualmente selecionado (Todos, Com, ou Sem feedback)
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Simula a carga inicial de dados (por enquanto usando dados mockados)

    // TODO: Substituir mock por busca real do Firestore

    LaunchedEffect(Unit) {
        reports.clear()

        // TODO: remover depois da integração com o banco
        reports.addAll(
            listOf(
                Report("16/10/2025", "Febre leve, dor de cabeça e cansaço.", ""),
                Report("15/10/2025", "Cansaço leve e dor nas costas.", "Recomendado repouso e hidratação adequada."),
                Report("14/10/2025", "Sem sintomas relevantes.", ""),
                Report("13/10/2025", "Tosse seca e leve dor de garganta.", ""),
                Report("12/10/2025", "Sintomas leves de fadiga.", "Boa evolução, continuar observação."),
                Report("11/10/2025", "Dor de cabeça persistente.", ""),
                Report("10/10/2025", "Sem sintomas relatados.", ""),
                Report("09/10/2025", "Febre baixa e dor muscular.", "Acompanhamento necessário se persistir."),
                Report("08/10/2025", "Cansaço moderado e tontura leve.", "")
            )
        )
    }

    // Aplica o filtro conforme a opção escolhida pelo usuário
    val filteredReports = when (selectedFilter) {
        "Com" -> reports.filter { it.feedback.isNotEmpty() }
        "Sem" -> reports.filter { it.feedback.isEmpty() }
        else -> reports
    }

    // Estrutura principal da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Cabeçalho superior com o nome do app
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Teal)
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "INTELIMED",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Conteúdo principal (título, filtros e lista)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Título da seção
            Text(
                text = "Meus relatórios",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Teal
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Linha de botões de filtro
            FilterRow(
                selected = selectedFilter,
                onSelect = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Caso não existam relatórios no filtro atual
            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum relatório encontrado.", color = Color.Gray)
                }
            } else {
                // Lista de relatórios rolável
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredReports) { report ->
                        ReportCard(report = report)
                    }
                }
            }
        }
    }
}

/**
 * Linha de botões de filtro (Todos / Com feedback / Sem feedback)
 */
@Composable
fun FilterRow(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val filters = listOf("Todos", "Com", "Sem")

        filters.forEach { option ->
            val isSelected = selected == option
            Button(
                onClick = { onSelect(option) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Teal else Color.White,
                    contentColor = if (isSelected) Color.White else Teal
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(Teal)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
            ) {
                Text(option, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            // Adiciona um espaçamento entre os botões, exceto no último
            if (option != filters.last()) Spacer(modifier = Modifier.width(10.dp))
        }
    }
}

/**
 * Exibe um card com as informações de um relatório específico
 */
@Composable
fun ReportCard(report: Report) {
    // Define o status de acordo com a presença de feedback
    val (statusText, statusColor) = if (report.feedback.isNotEmpty()) {
        "Feedback recebido" to FeedbackGreen
    } else {
        "Aguardando feedback" to WaitingRed
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
            .clickable {
                // Clique futuro pode abrir uma tela de detalhes do relatório
            }
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 24.dp,
                bottom = 16.dp
            )
        ) {
            // Data do relatório
            Text(
                text = report.date,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(12.dp))

            // Status do feedback (verde ou vermelho)
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(10.dp))

            // Exibição dos sintomas relatados
            Text(
                text = "Sintomas: ${report.symptoms}",
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportsScreenPreview() {
    ReportsScreen()
}