package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import br.com.ibm.intelimed.ui.theme.IntelimedTheme
import android.widget.Toast

class TermsOfUseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                TermsOfUse()
            }
        }
    }
}

// Composable principal da tela
@Composable
fun TermsOfUse(modifier: Modifier = Modifier) {

    var aceitoTermos by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // TODO: LOGO DO APP (Aguardando Guilherme)
        Text(
            text = "INTELIMED",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título
            Text(
                text = "Termos e Privacidade",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2FA49F)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = """
                    Ao acessar ou utilizar o aplicativo Intelimed, você concorda em cumprir os Termos de Uso e a Política de Privacidade. 
                    Este aplicativo é destinado a fins pessoais e informativos. É necessário aceitar os termos para continuar.
                """.trimIndent(),
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 18.sp,
                maxLines = 3 // Limita a 3 linhas
            )


            Spacer(modifier = Modifier.height(24.dp))

            // Checkbox com texto
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = aceitoTermos,
                    onCheckedChange = { aceitoTermos = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2FA49F))
                )
                Text(
                    text = "Li e aceito os termos",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão "Continuar"
            Button(
                onClick = {
                    if (aceitoTermos) {
                        Toast.makeText(context, "Termos aceitos com sucesso!", Toast.LENGTH_SHORT).show()
                        // TODO: IMPLEMENTAR AÇÃO DE CONTINUAÇÃO
                    } else {
                        Toast.makeText(context, "É necessário aceitar os termos para continuar", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = aceitoTermos,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2FA49F),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFB2DFDB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Continuar", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Link "Ler mais"
            Text(
                text = "Ler mais",
                color = Color(0xFF2FA49F),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, TermsFullActivity::class.java))
                }
            )
        }
    }
}

@Preview
@Composable
fun TermsAndPrivacyPreview() {
    IntelimedTheme {
        TermsOfUse()
    }
}