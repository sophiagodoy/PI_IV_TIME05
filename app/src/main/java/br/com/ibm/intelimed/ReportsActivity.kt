    package br.com.ibm.intelimed

    import android.content.Intent
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
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore

    // Paleta de cores utilizada na tela, mantendo o padrão do app
    private val Teal = Color(0xFF007C7A)
    private val CardBg = Color(0xFFFFFFFF)
    private val FeedbackGreen = Color(0xFF4CAF50)
    private val WaitingRed = Color(0xFFF44336)

    // Modelo que representa um relatório individual do paciente
    data class Report(
        val id: String = "",
        val pacienteId: String = "",
        val date: String = "",
        val symptoms: String = "",
        val feedback: String = ""
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

    fun convertTimestampToDate(value: Any?): String {
        return try {
            val millis = value.toString().toLong()
            val date = java.util.Date(millis)
            val format = java.text.SimpleDateFormat("dd/MM/yyyy")
            format.format(date)
        } catch (e: Exception) {
            ""
        }
    }


    @Composable
    fun ReportsScreen() {
        // Lista observável de relatórios exibidos na tela
        val reports = remember { mutableStateListOf<Report>() }

        // Guarda o filtro atualmente selecionado (Todos, Com, ou Sem feedback)
        var selectedFilter by remember { mutableStateOf("Todos") }


        LaunchedEffect(Unit) {
            val db = FirebaseFirestore.getInstance()
            val uidMedico = FirebaseAuth.getInstance().currentUser!!.uid

            db.collection("medico")
                .document(uidMedico)
                .collection("relatorios")
                .get()
                .addOnSuccessListener { task ->
                    reports.clear()

                    for (doc in task) {
                        val data = doc.data

                        val pacienteId = data["pacienteId"]?.toString() ?: ""

                        reports.add(
                            Report(
                                id = doc.id,
                                pacienteId = pacienteId,
                                date = convertTimestampToDate(data["dataRegistro"]),
                                symptoms = data["sentimento"]?.toString() ?: "",
                                feedback = data["feedback"]?.toString() ?: ""
                            )
                        )
                    }
                }
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
        val context = LocalContext.current

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
                    val intent = Intent(context, RespondingPatientActivity::class.java)
                    intent.putExtra("relatorioId", report.id)
                    intent.putExtra("pacienteId", report.pacienteId)
                    context.startActivity(intent)
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
                Text(
                    text = report.date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.Black
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(10.dp))

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