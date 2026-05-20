package ufsm.petsi.petservices.ui.signup

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignupScreen(
    viewModel: SignupViewModel = koinViewModel(),
    onNavigateBack : () -> Unit
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SignupEffect.NavigateBack -> onNavigateBack()
                is SignupEffect.ShowToast -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(Modifier.height(48.dp))

        Header()
        Fields(state, onAction = {
            viewModel.handleIntent(it)
        })
        Buttons(
            onAction = {
                viewModel.handleIntent(it)
            }
        )

    }
}

@Composable
private fun Header() {
    Text("Cadastro", fontSize = 32.sp)
    Text("Insira os dados do seu negócio")
}

@Composable
private fun Fields(
    state : SignupState,
    onAction : (SignupIntent) -> Unit,
) {
    val isError = state.errorMessage != null
    Spacer(Modifier.height(48.dp))

    AnimatedVisibility(isError) {
        Text(
            state.errorMessage ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Text(
        "Nome",
        modifier = Modifier.fillMaxWidth().padding(start = 66.dp),
        color = MaterialTheme.colorScheme.primary
    )
    OutlinedTextField(
        value = state.name,
        modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp, bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        onValueChange = {
            onAction(SignupIntent.ChangeName(it))
        }
    )
    Text(
        "Nome da Empresa",
        modifier = Modifier.fillMaxWidth().padding(start = 66.dp),
        color = MaterialTheme.colorScheme.primary
    )
    OutlinedTextField(
        value = state.companyName,
        modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp, bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        onValueChange = {
            onAction(SignupIntent.ChangeCompanyName(it))
        }
    )
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
            onAction(SignupIntent.ChangeEmail(it))
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        onValueChange = {
            onAction(SignupIntent.ChangePassword(it))
        }
    )
    Text(
        "Confirmar senha",
        modifier = Modifier.fillMaxWidth().padding(start = 66.dp),
        color = MaterialTheme.colorScheme.primary
    )
    OutlinedTextField(
        value = state.confirmPassword,
        modifier = Modifier.fillMaxWidth().padding(start = 64.dp, end = 64.dp),
        shape = RoundedCornerShape(12.dp),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        isError = isError,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onAction(SignupIntent.ConfirmClicked)
        }),
        onValueChange = {
            onAction(SignupIntent.ChangeConfirmPassword(it))
        }
    )
}

@Composable
private fun Buttons(
    onAction : (SignupIntent) -> Unit,
) {
    Spacer(Modifier.height(32.dp))

    Button(
        onClick = {
            onAction(SignupIntent.ConfirmClicked)
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
    ) {
        Text("Cadastrar")
    }
    Button(
        onClick = {
            onAction(SignupIntent.CancelClicked)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
    ) {
        Text("Cancelar")
    }
}