// ORIENTAÇÕES MÉDICAS PARA OS PACIENTES

package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class MedicalGuidanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                OrientacoesMedicas()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientacoesMedicas() {

    val teal = Color(0xFF007C7A)
    val cardBg = Color(0xFFF7FDFC)
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Orientações Médicas",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, MainPatientActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = teal)
            )
        }
    ) { padding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val orientacoes = listOf(
                "Mantenha uma alimentação equilibrada, rica em frutas e vegetais." to Icons.Default.Restaurant,
                "Hidrate-se: beba ao menos 2 litros de água por dia." to Icons.Default.LocalDrink,
                "Evite esforços físicos intensos sem liberação médica." to Icons.Default.FitnessCenter,
                "Durma de 7 a 8 horas por noite para ajudar na recuperação." to Icons.Default.Bedtime,
                "Tome a medicação conforme prescrição médica." to Icons.Default.MedicalInformation,
                "Evite o consumo de álcool e cigarro durante o tratamento." to Icons.Default.NoDrinks,
                "Lave bem as mãos e mantenha uma boa higiene pessoal." to Icons.Default.Wash,
                "Reduza o estresse: pratique respiração ou meditação." to Icons.Default.SelfImprovement,
                "Procure atendimento médico se houver piora dos sintomas." to Icons.Default.LocalHospital,
                "Evite se expor ao sol por longos períodos." to Icons.Default.WbSunny,
                "Faça pequenas pausas durante o dia para relaxar o corpo e a mente." to Icons.Default.AccessTime,
                "Evite automedicação e siga sempre a orientação do seu médico." to Icons.Default.WarningAmber
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                orientacoes.forEach { (texto, icone) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icone,
                                contentDescription = null,
                                tint = teal,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = texto,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrientacoesMedicasPreview() {
    IntelimedTheme {
        OrientacoesMedicas()
    }
}
