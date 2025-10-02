// TELA PARA LOGIN DO USUÁRIO

package br.com.ibm.intelimed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.tooling.preview.Preview
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

// Activity principal de login
class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignIn()
        }
    }
}

// Composable da tela de login
@Composable
fun SignIn(modifier: Modifier = Modifier) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) } // Controla se a senha está visível
    val context = LocalContext.current // Para mudança de tela

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // TODO: IMPLEMENTAR AQUI A LÓGICA DO ÍCONE DO APLICATIVO - ESPERANDO GUILHERME FAZER O LOGO

        // Título principal da aplicação - nome do aplicativo
        Text(
            text = "INTELIMED",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card branco central com os campos de login
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp)
                .width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo de e-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            // TODO: VALIDAR SE O EMAIL ESTÁ EM FORMATO VÁLIDO

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de senha com botão para mostrar/ocultar
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },

                // Altera entre mostrar ou ocultar o texto da senha
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None // Exibe o texto normalmente
                } else {
                    PasswordVisualTransformation() // Substitui cada caracter por "."
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),

                // Ìcone "olho" para alternar visibilidade
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Filled.Visibility // Olho aberto
                            } else {
                                Icons.Filled.VisibilityOff // Olho fechado
                            },
                            contentDescription = if (passwordVisible) {
                                "Ocultar senha"
                            } else {
                                "Mostrar senha"
                            }
                        )
                    }
                },

                modifier = Modifier.fillMaxWidth()
            )

            // TODO: VALIDAR SE A SENHA NÃO ESTÁ VAZIA E TEM TAMANHO MÍNIMO

            Spacer(modifier = Modifier.height(20.dp))

            // Botão de login
            Button(
                onClick = { /* ação */ }, // TODO: IMPLEMENTAR LÓGICA DE AUTENTICAÇÃO
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

            // Link "Esqueci minha senha"
            Text(
                text = "Esqueci minha senha",
                color = Color(0xFF2FA49F),
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    // TODO: IMPLEMENTAR NAVEGAÇÃO PARA TELA DE RECUPERAÇÃO DE SENHA - ESPERANDO ARTHUR FAZER
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Link "Cadastrar-se"
            Text(
                text = "Cadastrar-se",
                color = Color(0xFF2FA49F),
                fontSize = 18.sp,
                modifier = Modifier.clickable {
                    // TODO: IMPLEMENTAR NAVEGAÇÃO PARA TELA DE CADASTRO
                }
            )
        }
    }
}

// Preview da tela de login
@Preview
@Composable
fun SignInPreview(){
    IntelimedTheme {
        SignIn()
    }
}
