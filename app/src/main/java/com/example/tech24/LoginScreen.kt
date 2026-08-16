package com.example.tech24

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.tech24.model.LoginRequest
import com.example.tech24.network.Tech24Api
import androidx.compose.foundation.Image
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(
        "tech24",
        Context.MODE_PRIVATE
    )
    var password by remember { mutableStateOf("") }
    var email by remember {
        mutableStateOf(
            prefs.getString("saved_email", "") ?: ""
        )
    }
    var rememberMe by remember {
        mutableStateOf(email.isNotEmpty())
    }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showWelcome by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFDFDFD),
                        Color(0xFFF8F8F8),
                        Color(0xFFFFF2E5)
                    )
                )
            )
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.tech24_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ATM Case Management",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Secure access to your dashboard",
                    color = Color.Gray,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text("Enter your email.....", fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text("Enter your password.....", fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {passwordVisible = !passwordVisible}
                        ) {
                            Icon(
                                imageVector = if(passwordVisible)
                                Icons.Outlined.Visibility
                                else
                                Icons.Outlined.VisibilityOff,
                                contentDescription =
                                    if (passwordVisible)
                                "Hide password"
                                else
                                "Show password"
                            )
                        }
                    },
                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = {
                            rememberMe = it
                        }
                    )

                    Text("Remember me", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isLoading = true

                        scope.launch {

                            try {

                                val response = Tech24Api.service.login(
                                    LoginRequest(
                                        email = email,
                                        password = password
                                    )
                                )

                                if (response.isSuccessful && response.body() != null) {

                                    val login = response.body()!!
                                    firstName = login.user.first_name.trim()
                                    println(login.token)
                                    println(login.user.first_name)

                                    prefs.edit()
                                        .putString("token", login.token).putString("first_name", firstName.trim())
                                        .apply()
                                    if(rememberMe) {
                                        prefs.edit()
                                            .putString("saved_email", email)
                                            .apply()
                                    } else {
                                        prefs.edit()
                                            .remove("saved_email")
                                            .apply()
                                    }
                                    isLoading = false
                                    showWelcome = true
                                    delay(2500)
                                    showWelcome = false
                                    onLoginSuccess()

                                } else {
                                    isLoading = false
                                    println("Login failed")

                                }

                            } catch (e: Exception) {
                                isLoading = false
                                e.printStackTrace()

                            }

                        }

                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6200)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Sign in",
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
    if (showWelcome) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {

            Card(
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = firstName,
                        fontSize = 22.sp,
                        color = Color(0xFFFF6200),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CircularProgressIndicator()

                }

            }

        }
    }
}