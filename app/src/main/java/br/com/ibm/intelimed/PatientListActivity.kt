// TELA DA LISTA DE PACIENTES PARA O MÉDICO

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

class PatientListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                PatientList()
            }
        }
    }
}

// Modelo de dados
data class Paciente(
    val nome: String = "",
    val email: String = ""
)

fun getPatients(onResult: (List<Paciente>) -> Unit) {
    val db = Firebase.firestore

    db.collection("paciente")
        .get()
        .addOnSuccessListener { task ->
            val lista = mutableListOf<Paciente>()

            for (doc in task) {
                val nome = doc.getString("nome") ?: ""
                val email = doc.getString("email") ?: ""
                lista.add(Paciente(nome, email))
            }
            onResult(lista)
        }
        .addOnFailureListener { e ->
            onResult(emptyList())
        }
}

@Composable
fun PatientList() {
    val context = LocalContext.current
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    // Busca os pacientes do Firestore assim que a tela abrir
    LaunchedEffect(Unit) {
        getPatients { lista ->
            pacientes = lista
            carregando = false
            if (lista.isEmpty()) {
                Toast.makeText(context, "Nenhum paciente encontrado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${lista.size} paciente(s) carregado(s)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título
        Text(
            text = "Pacientes",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2FA49F)
        )

        Text(
            text = "Lista de pacientes disponíveis para acompanhamento",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            carregando -> {
                CircularProgressIndicator(color = Color(0xFF2FA49F))
            }

            pacientes.isEmpty() -> {
                Text("Nenhum paciente encontrado.", color = Color.Gray)
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pacientes) { paciente ->
                        PacienteCardModern(paciente)
                    }
                }
            }
        }
    }
}

@Composable
fun PacienteCardModern(paciente: Paciente) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FDFC)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // TODO: MUDAR PARA A TELA QUE APARECE OS SINTOMAS DO PACIENTE
            }
            .padding(horizontal = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = paciente.nome,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2FA49F)
                )
                Text(
                    text = paciente.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF2FA49F)
            )
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

