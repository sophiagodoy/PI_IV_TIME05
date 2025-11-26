/**
 * Tela para aceitar os termos de uso
 */
package br.com.ibm.intelimed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class TermsOfUseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val tipoUsuario = intent.getStringExtra("tipoUsuario")
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                TermsOfUse(tipoUsuario)
            }
        }
    }
}

// Composable principal da tela
@Composable
fun TermsOfUse(tipoUsuario: String?, modifier: Modifier = Modifier) {

    var aceitoTermos by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2FA49F))
            .padding(horizontal = 24.dp, vertical = 32.dp), // margem nas bordas
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_intelimed),
            contentDescription = "Logo Intelimed",
            modifier = Modifier
                .size(260.dp)
                .padding(bottom = 4.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "INTELIMED",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Card centralizado, sem encostar nas bordas
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

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
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

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

                Button(
                    onClick = {
                        Toast.makeText(context, "Bem-vindo", Toast.LENGTH_SHORT).show()
                        if (tipoUsuario == "medico") {
                            context.startActivity(Intent(context, MainDoctorActivity::class.java))
                        } else {
                            context.startActivity(Intent(context, MainPatientActivity::class.java))
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

                Text(
                    text = "Ler mais",
                    color = Color(0xFF2FA49F),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable {
                        val intent = Intent(context, TermsFullActivity::class.java)
                        intent.putExtra("tipoUsuario", tipoUsuario)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun TermsOfUsePreview() {
    IntelimedTheme {
        TermsOfUse("medico")
    }
}