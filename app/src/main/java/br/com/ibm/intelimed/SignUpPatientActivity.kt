// TELA DE CADASTRO DO USUÁRIO
package br.com.ibm.intelimed

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import android.content.Intent
import android.util.Log
import androidx.compose.material.icons.filled.ArrowBackIosNew
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class SignUpPatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUp()
        }
    }
}

fun registerPatientAuth(email: String, password: String, nome: String, context: Context) {
    val auth = Firebase.auth

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                if (user != null) {
                    val uid = user.uid
                    Log.i("AUTH", "Conta criada com sucesso: UID = $uid")

                    // Envia o e-mail de verificação
                    user.sendEmailVerification()
                        .addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "E-mail de verificação enviado para ${user.email}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.i("AUTH", "E-mail de verificação enviado com sucesso.")
                            } else {
                                Log.w(
                                    "AUTH",
                                    "Falha ao enviar e-mail de verificação.",
                                    verifyTask.exception
                                )
                                Toast.makeText(
                                    context,
                                    "Erro ao enviar e-mail de verificação.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    // Salva os dados do paciente no Firestore
                    savePatientToFirestore(uid, nome, email, context)
                }
            } else {
                val exception = task.exception
                when (exception) {
                    is FirebaseAuthUserCollisionException -> {
                        Toast.makeText(
                            context,
                            "Este e-mail já está em uso. Faça login!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        Toast.makeText(
                            context,
                            "Erro ao criar conta: ${exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("AUTH", "Erro ao criar conta", exception)
                    }
                }
            }
        }
}


fun savePatientToFirestore(uid: String, nome: String, email: String, context: Context) {
    val db = Firebase.firestore

    val dadosPaciente = hashMapOf(
        "nome" to nome,
        "email" to email,
        "tipo" to "Paciente",
        "primeiroLogin" to true
    )

    db.collection("paciente")
        .document(uid) // usa o UID do Firebase Auth como ID do documento
        .set(dadosPaciente)
        .addOnSuccessListener {
            Toast.makeText(context, "Cadastro salvo com sucesso!", Toast.LENGTH_SHORT).show()

            // Redireciona pra tela de login (ou home, se preferir)
            val intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

@Composable
fun SignUp(modifier: Modifier = Modifier) {

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    val context = LocalContext.current
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenhaVisivel by remember { mutableStateOf(false) }

    // 🔹 Novo estado para tipo de usuário
    var tipoUsuario by remember { mutableStateOf("Paciente") }

    // Layout principal com Box para colocar a seta no topo e conteúdo centralizado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F))
    ) {

        // Ícone de voltar fixo no topo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 48.dp)
                .align(Alignment.TopStart)
                .clickable {
                    val intent = android.content.Intent(context, AuthChoiceActivity::class.java)
                    context.startActivity(intent)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

        // Conteúdo centralizado
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Nome do aplicativo
            Text(
                text = "INTELIMED",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card branco central
            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .width(320.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Cadastro",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2FA49F)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Nome
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Senha
                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Senha") },
                    visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                            Icon(
                                imageVector = if (senhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Confirmar Senha
                OutlinedTextField(
                    value = confirmarSenha,
                    onValueChange = { confirmarSenha = it },
                    label = { Text("Confirmar senha") },
                    visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                            Icon(
                                imageVector = if (confirmarSenhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (confirmarSenhaVisivel) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Seletor Paciente / Médico
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { tipoUsuario = "Paciente" },
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
                        onClick = {
                            tipoUsuario = "Médico"
                            val intent = android.content.Intent(context, SignUpDoctorActivity::class.java)
                            context.startActivity(intent)
                        },
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

                // Botão Cadastrar
                Button(
                    onClick = {
                        val nomePartes = nome.trim().split("\\s+".toRegex())
                        when {
                            nome.isBlank() -> {
                                Toast.makeText(context, "Por favor, insira seu nome.", Toast.LENGTH_SHORT).show()
                            }
                            nomePartes.size < 2 -> {
                                Toast.makeText(context, "Por favor, insira nome e sobrenome.", Toast.LENGTH_SHORT).show()
                            }
                            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                                Toast.makeText(context, "E-mail inválido.", Toast.LENGTH_SHORT).show()
                            }
                            senha.length < 6 -> {
                                Toast.makeText(context, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                            }
                            senha != confirmarSenha -> {
                                Toast.makeText(context, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                registerPatientAuth(email, senha, nome, context)
                            }
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
}

@Preview
@Composable
fun SignUpPreview() {
    IntelimedTheme {
        SignUp()
    }
}
