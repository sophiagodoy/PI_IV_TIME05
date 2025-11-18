package br.com.ibm.intelimed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

// FIREBASE REGISTRO
fun registerDoctorAuth(
    email: String,
    password: String,
    nome: String,
    crm: String,
    especialidade: List<String>,
    context: Context
) {
    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = auth.currentUser
                if (user != null) {
                    val uid = user.uid

                    user.sendEmailVerification()
                    saveDoctorToFirestore(uid, nome, email, crm, especialidade, context)
                }
            } else {
                val exception = task.exception
                if (exception is FirebaseAuthUserCollisionException) {
                    Toast.makeText(context, "Este e-mail já está em uso.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Erro: ${exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
}

fun saveDoctorToFirestore(
    uid: String,
    nome: String,
    email: String,
    crm: String,
    especialidade: List<String>,
    context: Context
) {
    val db = Firebase.firestore

    val dados = hashMapOf(
        "nome" to nome,
        "email" to email,
        "crm" to crm,
        "especialidade" to especialidade,
        "tipo" to "Médico",
        "primeiroLogin" to true
    )

    db.collection("medico")
        .document(uid)
        .set(dados)
        .addOnSuccessListener {
            Toast.makeText(context, "Cadastro concluído!", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(context, SignInActivity::class.java))
        }
        .addOnFailureListener {
            Toast.makeText(context, "Erro ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
        }
}

/** Lista de Especialidades */
val ESPECIALIDADES = listOf(
    "Cardiologia",
    "Dermatologia",
    "Ginecologia",
    "Ortopedia",
    "Pediatria",
    "Psiquiatria",
    "Neurologia",
    "Urologia",
    "Endocrinologia",
    "Oftalmologia",
    "Otorrinolaringologia",
    "Oncologia",
    "Radiologia",
    "Reumatologia",
    "Outras"
)

/** Verifica se já existe esse CRM no banco */
fun checkCRMExists(
    crm: String,
    callback: (Boolean) -> Unit
) {
    val db = Firebase.firestore

    db.collection("medico")
        .whereEqualTo("crm", crm)
        .get()
        .addOnSuccessListener { result ->
            callback(!result.isEmpty) // TRUE = já existe
        }
        .addOnFailureListener {
            callback(false)
        }
}

class SignUpDoctorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SignUpDoctor() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpDoctor() {

    val context = LocalContext.current

    var nome by remember { mutableStateOf("") }
    var crm by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }

    // Multi seleção
    val selected = remember { mutableStateListOf<String>() }
    var expanded by remember { mutableStateOf(false) }
    var outraEspecialidade by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .wrapContentHeight()
                .padding(top = 24.dp)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // TÍTULO
            Text(
                text = "Cadastro Médico",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2FA49F)
            )

            Spacer(Modifier.height(20.dp))

            // NOME
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // CRM
            OutlinedTextField(
                value = crm,
                onValueChange = { crm = it },
                label = { Text("CRM") },
                placeholder = { Text("Ex: 12345-SP") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // DROPDOWN ESPECIALIDADES
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = if (selected.isEmpty()) "Selecione..." else selected.joinToString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidades") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ESPECIALIDADES.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selected.contains(item)) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF2FA49F)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text(item)
                                }
                            },
                            onClick = {
                                if (selected.contains(item)) selected.remove(item)
                                else selected.add(item)

                                expanded = false
                            }
                        )
                    }
                }
            }

            // CAMPO "OUTRAS"
            if ("Outras" in selected) {
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = outraEspecialidade,
                    onValueChange = { outraEspecialidade = it },
                    label = { Text("Digite outra especialidade") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // SENHA
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(
                            if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            /*
            val feedback = mensagemForcaSenha(senha)
            if (feedback.isNotEmpty()) {
                Text(
                    text = feedback,
                    fontSize = 12.sp,
                    color = if (senhaForte(senha)) Color(0xFF2FA49F) else Color.Red,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
            */

            Spacer(Modifier.height(12.dp))

            // CONFIRMAR SENHA
            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                label = { Text("Confirmar senha") },
                visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                        Icon(
                            if (confirmarSenhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(22.dp))

            // Botão Cadastrar
            Button(
                onClick = {

                    var temErro = false

                    // CAMPOS EM BRANCO
                    if (
                        nome.isBlank() ||
                        crm.isBlank() ||
                        email.isBlank() ||
                        senha.isBlank() ||
                        confirmarSenha.isBlank() ||
                        selected.isEmpty() ||
                        (selected.contains("Outras") && outraEspecialidade.isBlank())
                    ) {
                        Toast.makeText(context, "Preencha todos os campos obrigatórios.", Toast.LENGTH_LONG).show()
                        temErro = true
                    }

                    // SENHAS DIFERENTES
                    if (senha != confirmarSenha) {
                        Toast.makeText(context, "As senhas não coincidem.", Toast.LENGTH_LONG).show()
                        temErro = true
                    }

                    // EMAIL INVÁLIDO
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(context, "E-mail inválido.", Toast.LENGTH_LONG).show()
                        temErro = true
                    }

                    // CRM INVÁLIDO
                    if (!crm.matches(Regex("^\\d{4,6}-[A-Z]{2}$"))) {
                        Toast.makeText(context, "CRM inválido. Use o formato 12345-SP.", Toast.LENGTH_LONG).show()
                        temErro = true
                    }

                    if (!temErro) {

                        val especialidadesFinal = selected.toMutableList()

                        if (selected.contains("Outras")) {
                            especialidadesFinal.remove("Outras")
                            especialidadesFinal.add(outraEspecialidade)
                        }

                        checkCRMExists(crm) { crmExiste ->

                            if (crmExiste) {
                                Toast.makeText(context, "Este CRM já está cadastrado.", Toast.LENGTH_LONG).show()

                            } else {
                                registerDoctorAuth(
                                    email = email,
                                    password = senha,
                                    nome = nome,
                                    crm = crm,
                                    especialidade = especialidadesFinal,
                                    context = context
                                )
                            }
                        }
                    }
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2FA49F),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cadastrar", fontSize = 18.sp)
            }

            Spacer(Modifier.height(14.dp))

            // Link Login
            Text(
                text = "Fazer login",
                color = Color(0xFF2FA49F),
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSignUpDoctor() {
    IntelimedTheme {
        SignUpDoctor()
    }
}
