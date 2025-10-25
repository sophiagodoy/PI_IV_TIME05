// TELA PARA LOGIN DO USUÁRIO
package br.com.ibm.intelimed

import android.os.Bundle
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
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignIn()
        }
    }
}

fun loginUser(email: String, password: String, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                if (user != null) {
                    val uid = user.uid

                    // 🔍 Primeiro, tenta achar o usuário na coleção "medico"
                    db.collection("medico").document(uid).get()
                        .addOnSuccessListener { medicoDoc ->
                            if (medicoDoc.exists()) {
                                val isFirstLogin = medicoDoc.getBoolean("primeiroLogin") ?: false;
                                if (isFirstLogin) {
                                    Toast.makeText(context, "Bem-vindo ao seu primeiro acesso!", Toast.LENGTH_LONG).show();
                                    val intent = Intent(context, TermsOfUseActivity::class.java)
                                    context.startActivity(intent)
                                    db.collection("medico").document(uid)
                                        .update("primeiroLogin", false)
                                } else {
                                    Toast.makeText(context, "Bem-vindo, médico!", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, MainDoctorActivity::class.java)
                                    context.startActivity(intent)
                                }

                            } else {
                                // 🔍 Se não for médico, tenta na coleção "paciente"
                                db.collection("paciente").document(uid).get()
                                    .addOnSuccessListener { pacienteDoc ->
                                        if (pacienteDoc.exists()) {
                                            val isFirstLogin = pacienteDoc.getBoolean("primeiroLogin") ?: false;
                                            if (isFirstLogin) {
                                                Toast.makeText(context, "Bem-vindo ao seu primeiro acesso!", Toast.LENGTH_LONG).show();
                                                val intent = Intent(context, TermsOfUseActivity::class.java)
                                                context.startActivity(intent)
                                                db.collection("paciente").document(uid)
                                                    .update("primeiroLogin", false)
                                            } else {
                                                Toast.makeText(context, "Bem-vindo, paciente!", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(context, MainPatientActivity::class.java)
                                                context.startActivity(intent)
                                            }

                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Usuário não encontrado no banco de dados.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                        }
                } else {
                    Toast.makeText(context,
                        "Por favor, verifique seu e-mail antes de entrar.",
                        Toast.LENGTH_LONG
                    ).show()
                    auth.signOut()
                }
            } else {
                val errorMessage = task.exception?.message ?: "Erro desconhecido ao fazer login."
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
}

@Composable
fun SignIn(modifier: Modifier = Modifier) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "E-mail não pode estar vazio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-mail inválido"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Senha não pode estar vazia"
            password.length < 6 -> "Senha deve ter no mínimo 6 caracteres"
            else -> null
        }
    }

    // Layout principal
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
                imageVector = Icons.Filled.ArrowBack,
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
            Text(
                text = "INTELIMED",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(24.dp)
                    .width(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("E-mail") },
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it, color = Color.Red) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Senha") },
                    isError = passwordError != null,
                    supportingText = { passwordError?.let { Text(it, color = Color.Red) } },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Filled.Visibility
                                else
                                    Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible)
                                    "Ocultar senha"
                                else
                                    "Mostrar senha"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        emailError = validateEmail(email)
                        passwordError = validatePassword(password)
                        if (emailError == null && passwordError == null) {
                            loginUser(email, password, context)
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
                    Text("Entrar", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Esqueci minha senha",
                    color = Color(0xFF2FA49F),
                    fontSize = 18.sp,
                    modifier = Modifier.clickable {
                        val intent = android.content.Intent(context, ForgotPasswordActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cadastrar-se",
                    color = Color(0xFF2FA49F),
                    fontSize = 18.sp,
                    modifier = Modifier.clickable {
                        val intent = android.content.Intent(context, SignUpPatientActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    IntelimedTheme {
        SignIn()
    }
}
