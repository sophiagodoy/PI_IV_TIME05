/**
 * Texto dos termos de uso
 */
package br.com.ibm.intelimed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class TermsFullActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tipoUsuario = intent.getStringExtra("tipoUsuario")
        setContent {
            IntelimedTheme {
                TermsFullScreen(tipoUsuario = tipoUsuario)
            }
        }
    }
}

@Composable
fun TermsFullScreen(
    tipoUsuario: String?
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val termosCompletos = remember {
        """
        1. Aceitação dos Termos
        Ao acessar ou utilizar o aplicativo Intelimed, você concorda em cumprir estes Termos de Uso e a nossa Política de Privacidade. Se você não concorda, não utilize o aplicativo.

        2. Uso do Aplicativo
        O Intelimed é destinado exclusivamente a fins pessoais e informativos. É proibido utilizar o aplicativo para atividades ilegais, divulgação de informações falsas, distribuição de vírus ou interferir no funcionamento do aplicativo.

        3. Cadastro e Conta de Usuário
        Para acessar certas funcionalidades, pode ser necessário criar uma conta. Você é responsável por manter a confidencialidade das informações de login.

        4. Privacidade e Coleta de Dados
        Coletamos informações pessoais e de uso para melhorar nossos serviços. Seus dados são armazenados com segurança e não são compartilhados sem consentimento.

        5. Conteúdo do Usuário
        Você é responsável por todo conteúdo enviado. Reservamo-nos o direito de remover conteúdo que viole estes termos.

        6. Propriedade Intelectual
        Todo conteúdo do aplicativo é propriedade do Intelimed ou de terceiros licenciantes. É proibida reprodução sem autorização.

        7. Limitação de Responsabilidade
        O Intelimed é fornecido “como está”. Não garantimos que estará livre de erros. O uso é de sua responsabilidade.

        8. Alterações nos Termos
        Podemos modificar estes termos a qualquer momento. O uso contínuo após alterações significa aceitação.

        9. Rescisão
        Podemos suspender ou encerrar sua conta se você violar estes termos. Você pode encerrar sua conta a qualquer momento.

        10. Contato
        Dúvidas sobre estes Termos de Uso ou Política de Privacidade: contato@intelimed.com
        """.trimIndent()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F))
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)          // centraliza o card
                .padding(horizontal = 24.dp)       // margem lateral
                .fillMaxWidth()
                .fillMaxHeight(0.75f),             // **ocupa só 75% da altura da tela**
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Seta + título
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            (context as? android.app.Activity)?.finish()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF2FA49F)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Termos e Privacidade",
                        fontSize = 22.sp,
                        color = Color(0xFF2FA49F)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Texto rolável dentro do card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = termosCompletos,
                        fontSize = 16.sp,
                        color = Color.Black,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Bem-vindo", Toast.LENGTH_SHORT).show()
                        if (tipoUsuario == "medico") {
                            context.startActivity(Intent(context, MainDoctorActivity::class.java))
                        } else {
                            context.startActivity(Intent(context, MainPatientActivity::class.java))
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
                    Text("Aceitar e Continuar", fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TermsFullPreview() {
    IntelimedTheme {
        TermsFullScreen(tipoUsuario = "medico")
    }
}
