package br.com.ibm.intelimed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// VALIDAÇÕES
fun nomeValido(nome: String): Boolean {
    val partes = nome.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return partes.size >= 2 && partes.all { it.length >= 2 }
}

fun crmValido(crm: String): Boolean {
    val regex = Regex("^\\d{4,6}-[A-Z]{2}\$")
    return regex.matches(crm.trim().uppercase())
}

fun formatarCrmEntrada(raw: String): String {
    val cleaned = raw.filter { it.isLetterOrDigit() }
    val digits = cleaned.takeWhile { it.isDigit() }
    val letters = cleaned.drop(digits.length)
    val d = digits.take(6)
    val uf = letters.filter { it.isLetter() }.take(2).uppercase()
    return if (uf.isNotEmpty()) "$d-$uf" else d
}

fun senhaForte(s: String): Boolean {
    val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}\$")
    return regex.matches(s)
}

fun mensagemForcaSenha(senha: String): String {
    if (senha.isEmpty()) return ""
    val checks = listOf(
        "8+" to (senha.length >= 8),
        "Maiúscula" to senha.any { it.isUpperCase() },
        "Minúscula" to senha.any { it.isLowerCase() },
        "Número" to senha.any { it.isDigit() },
        "Especial" to senha.any { !it.isLetterOrDigit() }
    )
    val faltando = checks.filter { !it.second }.map { it.first }
    return if (faltando.isEmpty()) "Senha forte" else "Faltando: " + faltando.joinToString(", ")
}

// LISTA DE ESPECIALIDADES
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

    var tipoUsuario by remember { mutableStateOf("Médico") }

    // SENHA VISIBILIDADE
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }

    // ESPECIALIDADES (MULTI-SELEÇÃO)
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(mutableListOf<String>()) }
    var outraEspecialidade by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val filtradas = remember(query, selected) {
        ESPECIALIDADES.filter {
            it.contains(query, ignoreCase = true) || it == "Outras"
        }
    }

    fun toggleItem(item: String) {
        if (selected.contains(item)) {
            selected.remove(item)
        } else {
            selected.add(item)
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F))
    ) {

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {

            Text(
                "Cadastro Médico",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2FA49F)
            )

            Spacer(Modifier.height(16.dp))

            // NOME
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // CRM (MÁSCARA AUTOMÁTICA)
            OutlinedTextField(
                value = crm,
                onValueChange = { crm = formatarCrmEntrada(it) },
                label = { Text("CRM (ex: 12345-SP)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ESPECIALIDADES MULTI-SELEÇÃO
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        expanded = true
                    },
                    label = { Text("Especialidades") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filtradas.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selected.contains(item)) {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF2FA49F)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    Text(item)
                                }
                            },
                            onClick = {
                                toggleItem(item)
                            }
                        )
                    }
                }
            }

            // CAMPO EXTRA (OUTRAS)
            if (selected.contains("Outras")) {
                Spacer(Modifier.height(8.dp))
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

            val feedback = mensagemForcaSenha(senha)
            if (feedback.isNotEmpty()) {
                Text(
                    feedback,
                    fontSize = 12.sp,
                    color = if (senhaForte(senha)) Color(0xFF2FA49F) else Color.Red
                )
            }

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(24.dp))

            // Seletor Paciente / Médico
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        tipoUsuario = "Paciente"
                        val intent = android.content.Intent(context, SignUpPatientActivity::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipoUsuario == "Paciente") Color(0xFF2FA49F) else Color.LightGray,
                        contentColor = if (tipoUsuario == "Paciente") Color.White else Color.DarkGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Paciente", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { tipoUsuario = "Médico" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipoUsuario == "Médico") Color(0xFF2FA49F) else Color.LightGray,
                        contentColor = if (tipoUsuario == "Médico") Color.White else Color.DarkGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Médico", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÃO CADASTRAR
            Button(
                onClick = {

                    // ESPECIALIDADES FINAIS
                    val finais = selected.toMutableList()

                    if (finais.contains("Outras")) {
                        if (outraEspecialidade.isBlank()) {
                            Toast.makeText(context, "Digite a outra especialidade.", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        finais.remove("Outras")
                        finais.add(outraEspecialidade.trim())
                    }

                    // VALIDAÇÕES
                    when {
                        !nomeValido(nome) ->
                            Toast.makeText(context, "Digite nome e sobrenome.", Toast.LENGTH_LONG).show()

                        !crmValido(crm) ->
                            Toast.makeText(context, "CRM inválido.", Toast.LENGTH_LONG).show()

                        finais.isEmpty() ->
                            Toast.makeText(context, "Escolha ao menos 1 especialidade.", Toast.LENGTH_LONG).show()

                        !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                            Toast.makeText(context, "E-mail inválido.", Toast.LENGTH_LONG).show()

                        !senhaForte(senha) ->
                            Toast.makeText(context, "Senha fraca.", Toast.LENGTH_LONG).show()

                        senha != confirmarSenha ->
                            Toast.makeText(context, "Senhas não coincidem.", Toast.LENGTH_LONG).show()

                        else -> registerDoctorAuth(
                            email.trim(),
                            senha,
                            nome.trim(),
                            crm.trim().uppercase(),
                            finais,
                            context
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2FA49F),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                    shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cadastrar", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Link "Fazer login"
            Text(
                text = "Fazer login",
                color = Color(0xFF2FA49F),
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    val intent = android.content.Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}