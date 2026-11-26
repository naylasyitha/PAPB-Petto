package com.papb.petto.ui.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.R
import com.papb.petto.ui.theme.PettoTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    infoMessage: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loginSuccessEvent.collect { onNavigateToHome() }
    }

    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            val rotating = (context as? Activity)?.isChangingConfigurations == true
            if (!rotating) viewModel.clearForm()
        }
    }

    LoginContent(
        state = uiState,
        infoMessage = infoMessage,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRememberMeChange = viewModel::onRememberMeChange,
        onLoginClick = viewModel::onLoginClicked,
        onGoogleLoginClick = viewModel::onGoogleLoginClicked,
        onForgotPasswordClick = onForgotPasswordClick,
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginContent(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    infoMessage: String? = null
) {
    val colors = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PettoLogoHeader()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sign in to your Petto account",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("Enter your email", color = Color.Gray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = colors.outline,
                        unfocusedBorderColor = colors.outline,
                        focusedTextColor = colors.onBackground,
                        unfocusedTextColor = colors.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Password",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Forgot password?",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("Enter your password", color = Color.Gray) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) R.drawable.ic_visibility_off
                                    else R.drawable.ic_visibility
                                ),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = colors.onSurfaceVariant
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant,
                        focusedBorderColor = colors.outline,
                        unfocusedBorderColor = colors.outline,
                        focusedTextColor = colors.onBackground,
                        unfocusedTextColor = colors.onBackground
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isRememberMe,
                    onCheckedChange = onRememberMeChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.primary,
                        uncheckedColor = colors.outline
                    )
                )
                Text(
                    text = "Remember me?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                    modifier = Modifier.clickable { onRememberMeChange(!state.isRememberMe) }
                )
            }

            state.errorMessage?.let { errorMsg ->
                Spacer(modifier = Modifier.height(10.dp))
                AuthErrorMessage(message = errorMsg)
            }

            infoMessage?.let { info ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = info,
                    color = colors.primary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onLoginClick,
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGoogleLoginClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = colors.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .border(1.dp, colors.outline, RoundedCornerShape(12.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account yet? ",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}

@Composable
fun PettoLogoHeader() {
    Image(
        painter = painterResource(id = R.drawable.logo_petto),
        contentDescription = "Petto Logo",
        modifier = Modifier.size(130.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    PettoTheme {
        LoginContent(
            state = LoginState(),
            onEmailChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onForgotPasswordClick = {},
            onNavigateToRegister = {}
        )
    }
}
