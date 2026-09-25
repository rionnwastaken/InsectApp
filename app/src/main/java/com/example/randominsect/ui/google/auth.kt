package com.example.randominsect.ui.google

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val WEB_CLIENT_ID = "259978300542-8jlbum6173fdn0gj2ith360mfkbt717l.apps.googleusercontent.com"

@Composable
fun AuthScreen(
    onSignInSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showBypassOption by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Random Insect",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Discover fascinating insects from around the world.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                // Primary Sign-In Button
                OutlinedButton(
                    onClick = {
                        isLoading = true
                        performGoogleSignIn(
                            context = context,
                            scope = scope,
                            onSuccess = { idToken, name ->
                                isLoading = false
                                Toast.makeText(context, "Welcome $name!", Toast.LENGTH_SHORT).show()
                                onSignInSuccess()
                            },
                            onError = { errorMessage ->
                                isLoading = false
                                if (errorMessage != null) {
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                    // Enable the bypass option on error
                                    showBypassOption = true
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = if (showBypassOption) "Try Signing in Again" else "Sign in with Google",
                        fontSize = 16.sp
                    )
                }

                // Fallback Bypass Button
                if (showBypassOption) {
                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { onSignInSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Continue Anyway",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

private fun performGoogleSignIn(
    context: Context,
    scope: CoroutineScope,
    onSuccess: (idToken: String, displayName: String?) -> Unit,
    onError: (errorMessage: String?) -> Unit
) {
    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(WEB_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                onSuccess(
                    googleIdTokenCredential.idToken,
                    googleIdTokenCredential.displayName
                )
            } else {
                onError("Unrecognized credential type received.")
            }
        } catch (e: GetCredentialCancellationException) {
            // User intentionally closed/dismissed the prompt; no error message needed
            onError(null)
        } catch (e: GetCredentialException) {
            onError("Authentication failed: ${e.localizedMessage}")
        } catch (e: Exception) {
            onError("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
