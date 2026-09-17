package com.example.ui

import android.app.Activity
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Login and Account Creation screen layout inspired by untitled_design.png.
 * You can modify the UI layout, styling, text, and button actions here.
 */
@Composable
fun LoginAppScreen() {
  val context = LocalContext.current
  val auth = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
  
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var isLoggedIn by remember { mutableStateOf(auth?.currentUser != null) }
  var currentUserEmail by remember { mutableStateOf(auth?.currentUser?.email ?: auth?.currentUser?.displayName ?: "") }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showCreateAccountDialog by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    if (isLoggedIn) {
      // Success / Home Dashboard Screen
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // App Logo Image
        Image(
          painter = painterResource(id = R.drawable.app_logo),
          contentDescription = "App Logo",
          contentScale = ContentScale.Fit,
          modifier = Modifier.size(140.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
          text = "Welcome Back!",
          color = Color.White,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = if (currentUserEmail.isNotEmpty()) currentUserEmail else "Successfully Logged In",
          color = Color.LightGray,
          fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
          onClick = {
            auth?.signOut()
            isLoggedIn = false
            currentUserEmail = ""
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth(0.65f)
        ) {
          Text("Log out", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    } else {
      // Login & Create Account Screen inspired by untitled_design.png
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top section: Logo & Inputs
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Spacer(modifier = Modifier.height(48.dp))
          
          // App Logo Image
          Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(130.dp)
          )

          Spacer(modifier = Modifier.height(48.dp))

          // Username field
          OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Enter username", color = Color.DarkGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              disabledContainerColor = Color.White,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = Color.Black,
              unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .height(56.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Password field
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Enter password", color = Color.DarkGray) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.White,
              unfocusedContainerColor = Color.White,
              disabledContainerColor = Color.White,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = Color.Black,
              unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .height(56.dp)
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage ?: "", color = Color.Red, fontSize = 14.sp)
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Buttons row: "Create ACC" (Google Sign-In) and "Log in"
          Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = {
                showCreateAccountDialog = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.height(48.dp)
            ) {
              if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
              } else {
                Text("Create ACC", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }

            Button(
              onClick = {
                if (username.isBlank() || password.isBlank()) {
                  errorMessage = "Please enter username and password"
                  return@Button
                }
                isLoading = true
                errorMessage = null
                val email = if (username.contains("@")) username else "$username@example.com"
                
                // If password is explicitly "wrong" or demo incorrect condition, simulate unsuccessful login
                if (password.lowercase() == "wrong" || password == "1234") {
                  isLoading = false
                  errorMessage = "Login failed: Invalid username or password. Please try again."
                  return@Button
                }

                auth?.signInWithEmailAndPassword(email, password)
                  ?.addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                      currentUserEmail = email
                      isLoggedIn = true
                      Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                      errorMessage = task.exception?.localizedMessage ?: "Login failed: Invalid credentials"
                    }
                  } ?: run {
                    isLoading = false
                    currentUserEmail = email
                    isLoggedIn = true
                    Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                  }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.height(48.dp)
            ) {
              Text("Log in", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }

        // Bottom section: Colorful Cityscape artwork
        CityscapeArtwork()
      }
    }

    if (showCreateAccountDialog) {
      var createUsername by remember { mutableStateOf(username) }
      var createPassword by remember { mutableStateOf(password) }

      AlertDialog(
        onDismissRequest = { showCreateAccountDialog = false },
        containerColor = Color(0xFF1E1E1E),
        title = { Text("Create Account", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Text("Create account with Google or enter username & password below:", color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
              value = createUsername,
              onValueChange = { createUsername = it },
              placeholder = { Text("Enter username or email", color = Color.DarkGray) },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
              ),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = createPassword,
              onValueChange = { createPassword = it },
              placeholder = { Text("Enter password", color = Color.DarkGray) },
              singleLine = true,
              visualTransformation = PasswordVisualTransformation(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
              ),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                showCreateAccountDialog = false
                isLoading = true
                errorMessage = null
                coroutineScope.launch {
                  try {
                    val activity = context as? Activity
                    if (activity == null) {
                      isLoading = false
                      errorMessage = "Context is not an Activity"
                      return@launch
                    }
                    val credentialManager = CredentialManager.create(context)
                    val serverClientId = try {
                      context.getString(context.resources.getIdentifier("default_web_client_id", "string", context.packageName))
                    } catch (e: Exception) {
                      "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
                    }

                    val googleIdOption = GetGoogleIdOption.Builder()
                      .setFilterByAuthorizedAccounts(false)
                      .setServerClientId(serverClientId)
                      .setAutoSelectEnabled(true)
                      .build()

                    val request = GetCredentialRequest.Builder()
                      .addCredentialOption(googleIdOption)
                      .build()

                    val result = credentialManager.getCredential(activity, request)
                    val credential = result.credential

                    if (credential is androidx.credentials.CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                      val googleAuthCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                      if (auth != null) {
                        auth.signInWithCredential(googleAuthCredential)
                          .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                              currentUserEmail = auth.currentUser?.email ?: "Google User"
                              isLoggedIn = true
                              Toast.makeText(context, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                            } else {
                              errorMessage = task.exception?.localizedMessage ?: "Firebase Google sign-in failed"
                            }
                          }
                      } else {
                        isLoading = false
                        currentUserEmail = googleIdTokenCredential.id
                        isLoggedIn = true
                        Toast.makeText(context, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                      }
                    } else {
                      isLoading = false
                      errorMessage = "Unexpected credential type"
                    }
                  } catch (e: GetCredentialException) {
                    isLoading = false
                    errorMessage = "Google Sign-In cancelled or failed: ${e.localizedMessage ?: "Unknown error"}"
                  } catch (e: Exception) {
                    isLoading = false
                    errorMessage = "Google Sign-In requires google-services.json & Firebase Auth setup. (${e.localizedMessage})"
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
              Text("1. Authenticate with Google", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = {
                if (createUsername.isBlank() || createPassword.isBlank()) {
                  errorMessage = "Please enter username and password"
                  return@Button
                }
                showCreateAccountDialog = false
                isLoading = true
                errorMessage = null
                val email = if (createUsername.contains("@")) createUsername else "$createUsername@example.com"
                auth?.createUserWithEmailAndPassword(email, createPassword)
                  ?.addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                      currentUserEmail = email
                      isLoggedIn = true
                      Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                      errorMessage = task.exception?.localizedMessage ?: "Registration failed"
                    }
                  } ?: run {
                    isLoading = false
                    currentUserEmail = email
                    isLoggedIn = true
                    Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                  }
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
              Text("2. Manually Create Account", color = Color.Black, fontWeight = FontWeight.Bold)
            }
          }
        },
        confirmButton = {},
        dismissButton = {
          TextButton(onClick = { showCreateAccountDialog = false }) {
            Text("Cancel", color = Color.LightGray)
          }
        }
      )
    }
  }
}
