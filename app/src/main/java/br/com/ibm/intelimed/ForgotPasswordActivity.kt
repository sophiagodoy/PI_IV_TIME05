package br.com.ibm.intelimed

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.tooling.preview.Preview
import br.com.ibm.intelimed.ui.theme.IntelimedTheme

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntelimedTheme {
                ForgotPasswordScreen()
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(){

    var email by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        //Cabeçalho Superior (Colorido)
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color(0xFF2F7D7D)),
        contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // TODO: Colocar Icon
                // Esperando Guilherme

                Text(
                    text = "INTELIMED",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "RECUPERAR SENHA",
            color = Color(0xFF2F7D7D),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo para email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it},
            label = {Text("E-mail")},
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        //Botão que enviará o link via email para redefinição

        Button(
            onClick = {/* TODO: implementar lógica*/},
            colors = ButtonDefaults.buttonColors(containerColor = Color (0xFF2F7D7D)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .height(55.dp)
        ){
            Text(
                text = "Enviar link",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        //Texto clicável "Voltar pra aba de login (todo: implementar )"

        Text(
            text = "Voltar ao Login",
            color = Color(0xFF2F7D7D),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable{
                // TODO : ir pra outra tela
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordPreview(){
    IntelimedTheme{
        ForgotPasswordScreen()
    }
}