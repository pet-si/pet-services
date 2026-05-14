package ufsm.petsi.petservices

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

import petservices.composeapp.generated.resources.Res
import petservices.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var usuario by remember { mutableStateOf("") }
            var senha by remember { mutableStateOf("") }

            Spacer(Modifier.height(48.dp))
            Text("Login", fontSize = 32.sp)
            Text("Insira seu usuário e senha")

            Spacer(Modifier.height(48.dp))
            Text("Usuário", modifier = Modifier.fillMaxWidth().padding(start = 66.dp), color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = usuario,
                modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp, bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                onValueChange = {
                    usuario = it
                }
            )
            Text("Senha", modifier = Modifier.fillMaxWidth().padding(start = 66.dp), color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = senha,
                modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    
                }),
                onValueChange = {
                    senha = it
                }
            )
            Text("Esqueci minha senha =(", color = Color.Gray, modifier = Modifier.padding(vertical = 12.dp), fontSize = 16.sp)

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {

                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
            ) {
                Text("Efetuar Login")
            }
            Button(onClick = {

            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
            ) {
                Text("Cadastrar")
            }
        }
    }
}