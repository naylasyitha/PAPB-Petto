package com.papb.petto.ui.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.registerSuccessEvent.collect { onRegisterSuccess() }
    }

    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            val rotating = (context as? Activity)?.isChangingConfigurations == true
            if (!rotating) viewModel.clearForm()
        }
    }

    RegisterContent(
        state = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onWhatsappChange = viewModel::onWhatsappChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onAgreeTermsChange = viewModel::onAgreeTermsChange,
        onRegisterClick = viewModel::onRegisterClicked,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterContent(
    state: RegisterState,
    onFullNameChange: (String) -> Unit,
    onWhatsappChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAgreeTermsChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
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
            Image(
                painter = painterResource(id = R.drawable.logo_petto),
                contentDescription = "Petto Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Create your\nPetto account!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Full Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = onFullNameChange,
                    placeholder = { Text("Enter your full name", color = Color.Gray) },
                    singleLine = true,
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "WhatsApp Number",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.whatsappNumber,
                    onValueChange = onWhatsappChange,
                    placeholder = { Text("Enter your whatsapp number", color = Color.Gray) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("name@gmail.com", color = Color.Gray) },
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

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("Create a password", color = Color.Gray) },
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isAgreeTerms,
                    onCheckedChange = onAgreeTermsChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = colors.primary,
                        uncheckedColor = colors.outline
                    )
                )
                Row(
                    modifier = Modifier.clickable { onAgreeTermsChange(!state.isAgreeTerms) }
                ) {
                    Text(
                        text = "I agree to the ",
                        fontSize = 13.sp,
                        color = colors.onBackground
                    )
                    Text(
                        text = "Terms & Conditions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            state.errorMessage?.let { errorMsg ->
                Spacer(modifier = Modifier.height(10.dp))
                AuthErrorMessage(message = errorMsg)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRegisterClick,
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
                        text = "Create Account",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = "Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    PettoTheme {
        RegisterContent(
            state = RegisterState(),
            onFullNameChange = {},
            onWhatsappChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onAgreeTermsChange = {},
            onRegisterClick = {},
            onNavigateToLogin = {}
        )
    }
}
