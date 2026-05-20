package ufsm.petsi.petservices.ui.login

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val isError = state.errorMessage != null

        Spacer(Modifier.height(48.dp))
        Text("Login", fontSize = 32.sp)
        Text("Insira seu usuário e senha")

        Spacer(Modifier.height(48.dp))

            AnimatedVisibility(isError) {
                Text(
                    state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        Text(
            "Email",
            modifier = Modifier.fillMaxWidth().padding(start = 66.dp),
            color = MaterialTheme.colorScheme.primary
        )
        OutlinedTextField(
            value = state.email,
            modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp, bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            onValueChange = {
                viewModel.handleIntent(LoginIntent.ChangeLogin(it))
            }
        )
        Text(
            "Senha",
            modifier = Modifier.fillMaxWidth().padding(start = 66.dp),
            color = MaterialTheme.colorScheme.primary
        )
        OutlinedTextField(
            value = state.password,
            modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = isError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {

            }),
            onValueChange = {
                viewModel.handleIntent(LoginIntent.ChangePassword(it))
            }
        )
        Text(
            "Esqueci minha senha",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(vertical = 12.dp),
            fontSize = 16.sp
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.handleIntent(LoginIntent.LoginClicked)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
        ) {
            Text("Efetuar Login")
        }
        Button(
            onClick = {
                viewModel.handleIntent(LoginIntent.SignupClicked)
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