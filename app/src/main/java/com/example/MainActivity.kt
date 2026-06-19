package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TournamentViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val viewModel: TournamentViewModel = viewModel()
    val sysSetting by viewModel.adminSetting.collectAsState(initial = null)
    val currentUserState by viewModel.currentUser.collectAsState(initial = null)

    var showTicker by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                // Real-time Global Ticker Announcement Bar
                if (showTicker && sysSetting != null && sysSetting?.systemAnnouncement?.isNotBlank() == true) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Campaign,
                            contentDescription = "Alert Symbol",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = sysSetting?.systemAnnouncement ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showTicker = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss Announcement",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                }
            }
        },
        bottomBar = {
            // Adaptive spacer to prevent bottom gesture bar collision
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = viewModel.currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenSwitch animate"
            ) { screen ->
                when (screen) {
                    is Screen.Welcome -> WelcomeLayout(viewModel)
                    is Screen.Login -> LoginLayout(viewModel)
                    is Screen.Signup -> SignupLayout(viewModel)
                    is Screen.UserDashboard -> UserDashboardLayout(viewModel)
                    is Screen.UserWallet -> UserDashboardLayout(viewModel)
                    is Screen.UserTournamentDetails -> UserTournamentDetailsLayout(viewModel, screen.tournamentId)
                    is Screen.AdminDashboard -> AdminDashboardLayout(viewModel)
                    is Screen.AdminTournamentDetails -> AdminDashboardLayout(viewModel)
                }
            }
        }
    }
}

// ==================== WELCOME SCREEN ====================
@Composable
fun WelcomeLayout(viewModel: TournamentViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Neon Gaming Gradient Backing Elements
        Box(
            modifier = Modifier
                .size(280.dp)
                .drawBehind {
                    drawCircle(
                        color = OrangeFiery.copy(alpha = 0.12f),
                        radius = size.width / 1.1f,
                        center = center
                    )
                    drawCircle(
                        color = AmberGold.copy(alpha = 0.08f),
                        radius = size.width / 1.5f,
                        center = center
                    )
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Glowing Orange Shield Esports Decal
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SlateSurfaceCard)
                    .border(2.dp, OrangeFiery, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = "Esports Shield logo",
                    tint = OrangeFiery,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FREE FIRE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = OrangeFiery,
                textAlign = TextAlign.Center
            )

            Text(
                text = "TOURNAMENT ARENA",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    fontSize = 15.sp
                ),
                color = TextLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Compete with pro players, track brackets in real-time, win grand prizes & enjoy instant payouts safely.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.navigateTo(Screen.Login) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(OrangeFiery, AmberGold)
                        )
                    )
                    .testTag("welcome_login_button")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GET STARTED",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { viewModel.navigateTo(Screen.Signup) },
                colors = ButtonDefaults.buttonColors(containerColor = SlateSurface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, AmberGold.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("welcome_signup_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "Create account secure",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CREATE PLAYER WALLET",
                        color = TextLight,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    )
                }
            }
        }
    }
}


// ==================== LOGIN SCREEN ====================
@Composable
fun LoginLayout(viewModel: TournamentViewModel) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        // --- High-Fidelity Cyber Glowing Background Orbs ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Soft fiery gradient orb top-left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(OrangeFiery.copy(alpha = 0.12f), Color.Transparent),
                            radius = size.width * 0.9f
                        ),
                        radius = size.width * 0.8f,
                        center = androidx.compose.ui.geometry.Offset(0f, 0f)
                    )
                    // Soft cyan gradient orb bottom-right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(CyanBright.copy(alpha = 0.1f), Color.Transparent),
                            radius = size.width * 0.8f
                        ),
                        radius = size.width * 0.7f,
                        center = androidx.compose.ui.geometry.Offset(size.width, size.height)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Navigation Bar Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Welcome) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateSurface.copy(alpha = 0.6f))
                        .border(1.dp, SlateSurfaceCard, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back welcome",
                        tint = TextLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Glowing Brand/Trophy Header Badge ---
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(colors = listOf(OrangeFiery, AmberGold)))
                    .padding(1.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(19.dp))
                        .background(SlateDarkBg)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.SportsEsports,
                        contentDescription = "Esports Logo",
                        tint = OrangeFiery,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Stylish Title & Accent Bar ---
            Text(
                text = "SOLDIER LOGIN",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = TextLight,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier
                    .width(80.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(OrangeFiery, AmberGold)
                        )
                    )
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {}

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Authenticate securely to enter elite tournament arenas",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Cyber Form Credentials Container ---
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(1.dp, Brush.linearGradient(listOf(SlateSurfaceCard, OrangeFiery.copy(alpha = 0.25f)))),
                        shape = RoundedCornerShape(24.dp)
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "SECURE PROTOCOL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = OrangeFiery
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.loginEmail,
                        onValueChange = { viewModel.loginEmail = it },
                        label = { Text("Email Address", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = OrangeFiery,
                            unfocusedBorderColor = SlateSurfaceCard,
                            focusedLabelColor = OrangeFiery,
                            unfocusedLabelColor = TextMuted,
                            cursorColor = OrangeFiery
                        ),
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Person, contentDescription = "Email icon", tint = OrangeFiery.copy(alpha = 0.7f))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.loginPassword,
                        onValueChange = { viewModel.loginPassword = it },
                        label = { Text("App Password", color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = TextMuted
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Filled.Lock, contentDescription = "Lock icon", tint = OrangeFiery.copy(alpha = 0.7f))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextLight,
                            unfocusedTextColor = TextLight,
                            focusedBorderColor = OrangeFiery,
                            unfocusedBorderColor = SlateSurfaceCard,
                            focusedLabelColor = OrangeFiery,
                            unfocusedLabelColor = TextMuted,
                            cursorColor = OrangeFiery
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    if (viewModel.loginError != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CrimsonRed.copy(alpha = 0.1f))
                                .border(1.dp, CrimsonRed.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = viewModel.loginError ?: "",
                                color = CrimsonRed,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.login() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(OrangeFiery, AmberGold)
                                )
                            )
                            .testTag("login_button")
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ENTER TOURNAMENT HUB",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Google & Social Multi-Auth Panel ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = SlateSurfaceCard)
                Text(
                    text = " OPTIONAL SECURE SIGN-IN ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = SlateSurfaceCard)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glassmorphic Google sign in
            Button(
                onClick = { viewModel.loginWithGoogle(context) },
                colors = ButtonDefaults.buttonColors(containerColor = SlateSurface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SlateSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.GppGood,
                        contentDescription = "Google Secure Icon",
                        tint = CyanBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Google Play Services",
                        color = TextLight,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Glassmorphic Apple sign in
            Button(
                onClick = { viewModel.loginWithApple() },
                colors = ButtonDefaults.buttonColors(containerColor = SlateSurface.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SlateSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Apple Secure Icon",
                        tint = TextLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Continue with Apple ID Secure",
                        color = TextLight,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Registration Bonus Reward Tip ---
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = OrangeFiery.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Bonus Fire",
                        tint = OrangeFiery,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "New around here?",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextLight)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Register a new profile to claim your ₹10 signup wallet bonus rewards instantly!",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }

    if (viewModel.showGoogleSetupDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showGoogleSetupDialog = false },
            confirmButton = {
                Button(
                    onClick = { viewModel.executeGoogleDemoLogin() },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeFiery),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Bypass / Sandbox Simulation", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showGoogleSetupDialog = false }) {
                    Text("Close", color = TextMuted)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Config Icon",
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Google Sign-In Config Guide",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    if (viewModel.googleSignInError != null) {
                        Text(
                            text = "Status: ${viewModel.googleSignInError}",
                            color = CrimsonRed,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier
                                .background(CrimsonRed.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                .border(1.dp, CrimsonRed.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    
                    Text(
                        text = "To fully enable authentic Google Sign-In with real production credentials:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLight,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val steps = listOf(
                        "1. Open your Google Firebase Console & add your Android App's Package Name (com.aistudio.fftour.xgmz).",
                        "2. Generate and add your App's SHA-1 fingerprint to the Firebase App settings.",
                        "3. Copy the 'Web Client ID' from Firebase Console -> Authentication -> Google Provider.",
                        "4. Go to AI Studio UI -> Secrets panel, add the key 'GOOGLE_CLIENT_ID' with your copied ID.",
                        "5. In the sandbox/emulator, you can bypass this security check by clicking 'Bypass / Sandbox Simulation' below to sign in with a simulated Google profile!"
                    )
                    
                    steps.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            },
            containerColor = SlateSurface,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.border(1.dp, SlateSurfaceCard, RoundedCornerShape(20.dp))
        )
    }
}


// ==================== SIGNUP SCREEN (OTP MOBILE VERIFY) ====================
@Composable
fun SignupLayout(viewModel: TournamentViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        // --- High-Fidelity Cyber Glowing Background Orbs ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Soft fiery gradient orb top-left
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(OrangeFiery.copy(alpha = 0.12f), Color.Transparent),
                            radius = size.width * 0.9f
                        ),
                        radius = size.width * 0.8f,
                        center = androidx.compose.ui.geometry.Offset(0f, 0f)
                    )
                    // Soft cyan gradient orb bottom-right
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(CyanBright.copy(alpha = 0.1f), Color.Transparent),
                            radius = size.width * 0.8f
                        ),
                        radius = size.width * 0.7f,
                        center = androidx.compose.ui.geometry.Offset(size.width, size.height)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Navigation Bar Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Welcome) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateSurface.copy(alpha = 0.6f))
                        .border(1.dp, SlateSurfaceCard, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back welcome",
                        tint = TextLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!viewModel.isOtpVerificationPending) {
                // Glow badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(colors = listOf(OrangeFiery, AmberGold)))
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(19.dp))
                            .background(SlateDarkBg)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "Esports Logo",
                            tint = OrangeFiery,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "PLAYER SIGN UP",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = TextLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Compete & win real cash with instant ₹10 bonus rewards!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
                )

                // --- Input Card Form Container ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(1.dp, Brush.linearGradient(listOf(SlateSurfaceCard, OrangeFiery.copy(alpha = 0.25f)))),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "REGISTRATION PROTOCOL",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = OrangeFiery
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.signupUsername,
                            onValueChange = { viewModel.signupUsername = it },
                            label = { Text("Esports IGN Username", color = TextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = OrangeFiery,
                                unfocusedBorderColor = SlateSurfaceCard,
                                focusedLabelColor = OrangeFiery,
                                unfocusedLabelColor = TextMuted,
                                cursorColor = OrangeFiery
                            ),
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Person, contentDescription = "Username Icon", tint = OrangeFiery.copy(alpha = 0.7f))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.signupEmail,
                            onValueChange = { viewModel.signupEmail = it },
                            label = { Text("Email Address", color = TextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = OrangeFiery,
                                unfocusedBorderColor = SlateSurfaceCard,
                                focusedLabelColor = OrangeFiery,
                                unfocusedLabelColor = TextMuted,
                                cursorColor = OrangeFiery
                            ),
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Email, contentDescription = "Email Icon", tint = OrangeFiery.copy(alpha = 0.7f))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.signupMobile,
                            onValueChange = { viewModel.signupMobile = it },
                            label = { Text("WhatsApp/Mobile Number", color = TextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = OrangeFiery,
                                unfocusedBorderColor = SlateSurfaceCard,
                                focusedLabelColor = OrangeFiery,
                                unfocusedLabelColor = TextMuted,
                                cursorColor = OrangeFiery
                            ),
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone Icon", tint = OrangeFiery.copy(alpha = 0.7f))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.signupFreeFireId,
                            onValueChange = { viewModel.signupFreeFireId = it },
                            label = { Text("Free Fire game numerical UID", color = TextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = OrangeFiery,
                                unfocusedBorderColor = SlateSurfaceCard,
                                focusedLabelColor = OrangeFiery,
                                unfocusedLabelColor = TextMuted,
                                cursorColor = OrangeFiery
                            ),
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "UID Icon", tint = OrangeFiery.copy(alpha = 0.7f))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.signupPassword,
                            onValueChange = { viewModel.signupPassword = it },
                            label = { Text("Profile Password", color = TextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = OrangeFiery,
                                unfocusedBorderColor = SlateSurfaceCard,
                                focusedLabelColor = OrangeFiery,
                                unfocusedLabelColor = TextMuted,
                                cursorColor = OrangeFiery
                            ),
                            leadingIcon = {
                                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Password Icon", tint = OrangeFiery.copy(alpha = 0.7f))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (viewModel.signupError != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CrimsonRed.copy(alpha = 0.1f))
                                    .border(1.dp, CrimsonRed.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = viewModel.signupError ?: "",
                                    color = CrimsonRed,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.signup() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(OrangeFiery, AmberGold)
                                    )
                                )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "START VERIFICATION",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Step 2: Verification Pending (SMS verification screen)
                Text(
                    text = "MOBILE VERIFICATION",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = OrangeFiery
                )

                Icon(
                    imageVector = Icons.Filled.Sms,
                    contentDescription = "SMS verification code icon",
                    tint = AmberGold,
                    modifier = Modifier
                        .padding(vertical = 18.dp)
                        .size(64.dp)
                )

                Text(
                    text = "OTP SMS sent to simulated mobile: ${viewModel.signupMobile}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Enter OTP verification pin below to claim ₹10 sign up welcome bonus securely.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = viewModel.simulatedEnteredOtp,
                    onValueChange = { viewModel.simulatedEnteredOtp = it },
                    label = { Text("Simulation Verification PIN") },
                    placeholder = { Text("Enter 1234") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGold,
                        focusedLabelColor = AmberGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                if (viewModel.signupError != null) {
                    Text(
                        text = viewModel.signupError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.verifyOtp() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "VOUCH WITH OTP & CLAIM 10₹",
                        fontWeight = FontWeight.ExtraBold,
                        color = SlateDarkBg,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { viewModel.isOtpVerificationPending = false }) {
                    Text("Go back & correct layout info", color = TextMuted)
                }
            }
        }
    }
}


// ==================== USER DASHBOARD ====================
@Composable
fun UserDashboardLayout(viewModel: TournamentViewModel) {
    val activeUserFlow = viewModel.currentUser.collectAsState(initial = null)
    val user = activeUserFlow.value ?: return

    val tournaments by viewModel.allTournaments.collectAsState()
    val transactions by viewModel.userTransactions.collectAsState()
    val registrations by viewModel.userRegistrations.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Tournaments, 1 = Wallet Balance, 2 = My Matches

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        // Arena Top Banner (Player Info Badge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateSurface)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.SportsEsports,
                        contentDescription = "Game badge pointer",
                        tint = OrangeFiery,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FF ESPORTS PRO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = OrangeFiery
                    )
                }
                Text(
                    text = user.username.ifBlank { "Unregistered Soldier" },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "FF UID: " + (if (user.freeFireId.isNotBlank()) user.freeFireId else "Pending Link"),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Wallet Quick Card
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AmberGold, RoundedCornerShape(12.dp))
                    .background(SlateSurfaceCard)
                    .clickable { activeTab = 1 }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "WALLET CASH", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "₹ ${String.format("%.2f", user.walletBalance)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = AmberGold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { viewModel.logout() }) {
                Icon(
                    imageVector = Icons.Filled.ExitToApp,
                    contentDescription = "Log out of terminal",
                    tint = CrimsonRed
                )
            }
        }

        // Navigation Tabs M3 Header
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = SlateSurfaceCard,
            contentColor = OrangeFiery,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = OrangeFiery,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("COMPETE MATCHES", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("MY WALLET", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("SCHEDULED", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = { Text("STATS & RANKS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        // Dynamic Tab Content Scroll list
        Crossfade(
            targetState = activeTab,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "UserDashboardTabCrossfade"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> ExploreTournamentsView(tournaments, viewModel)
                1 -> UserWalletSectionView(user, transactions, viewModel)
                2 -> UserMyRegistrationsView(registrations, tournaments, viewModel)
                3 -> PlayerStatsLeaderboardView(viewModel)
            }
        }
    }
}


// Explore Tournaments Tab list
@Composable
fun ExploreTournamentsView(tournaments: List<Tournament>, viewModel: TournamentViewModel) {
    val visibleTournaments = remember(tournaments) { tournaments.filter { it.isVisible } }
    if (visibleTournaments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.DynamicFeed, contentDescription = "No events", tint = TextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No active tournament matches created yet.", color = TextMuted)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = OrangeFiery.copy(alpha = 0.05f)),
                    border = BorderStroke(1.dp, OrangeFiery.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(OrangeFiery.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📢", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SYSTEM ANNOUNCEMENT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = OrangeFiery
                            )
                            Text(
                                text = "New Season VII rules updated. Review in Rules section.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.MilitaryTech, contentDescription = "Active Cups", tint = AmberGold, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Active Esports Contest Cups",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                }
            }

            items(visibleTournaments) { tourney ->
                TournamentItemCard(tourney = tourney) {
                    viewModel.selectTournament(tourney.id)
                    viewModel.navigateTo(Screen.UserTournamentDetails(tourney.id))
                }
            }
        }
    }
}

@Composable
fun TournamentItemCard(tourney: Tournament, onClick: () -> Unit) {
    val dateString = remember(tourney.scheduleTime) {
        val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        formatter.format(Date(tourney.scheduleTime))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
        border = BorderStroke(
            width = 1.dp,
            color = if (tourney.status == "ONGOING") OrangeFiery else Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Mode + Map badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(OrangeFiery.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tourney.gameMode.uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = OrangeFiery
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SlateSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tourney.mapName,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextLight
                        )
                    }
                }

                // Match Status chip
                val infoColor = when (tourney.status) {
                    "REGISTRATION_OPEN" -> CyanBright
                    "ONGOING" -> OrangeFiery
                    else -> TextMuted
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, infoColor, RoundedCornerShape(12.dp))
                        .background(infoColor.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (tourney.status == "REGISTRATION_OPEN") "JOIN OPEN" else tourney.status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = infoColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Title
            Text(
                text = tourney.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = TextLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = SlateSurface, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Details info block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "TOTAL PRIZEPOOL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "₹ ${tourney.prizePool.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = AmberGold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "ENTRY FEE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = if (tourney.entryFee > 0.0) "₹ ${tourney.entryFee.toInt()}" else "FREE ENTRY",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "SPOTS FILLED", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "${tourney.registeredCount} / ${tourney.maxTeams}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CyanBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateSurface)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = "Event Schedule Time",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Battle Schedule: $dateString",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextLight
                )
            }
        }
    }
}


// User Wallet Section View (Wallet Ledger + Deposit Simulated + Withdrawal simulated)
@Composable
fun UserWalletSectionView(user: User, transactions: List<Transaction>, viewModel: TournamentViewModel) {
    var activeSubWalletTab by remember { mutableStateOf(0) } // 0 = Deposit, 1 = Withdrawal, 2 = Statement History

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Main Wallet summary Display card with HTML gradient spec
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFEA580C), Color(0xFFB91C1C))
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AVAILABLE BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "₹ ${String.format("%.2f", user.walletBalance)}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { activeSubWalletTab = 0 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeSubWalletTab == 0) Color.White else Color.White.copy(alpha = 0.15f),
                                contentColor = if (activeSubWalletTab == 0) Color(0xFFC2410C) else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "deposit icon", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Cash", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { activeSubWalletTab = 1 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeSubWalletTab == 1) Color.White else Color.White.copy(alpha = 0.15f),
                                contentColor = if (activeSubWalletTab == 1) Color(0xFFC2410C) else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "withdrawal icon", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Withdraw", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { activeSubWalletTab = 2 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (activeSubWalletTab == 2) Color.White else Color.White.copy(alpha = 0.15f),
                                contentColor = if (activeSubWalletTab == 2) Color(0xFFC2410C) else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.History, contentDescription = "records icon", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ledger", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        when (activeSubWalletTab) {
            0 -> {
                // Deposit Simulation Frame with Admin UPI QR loading
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, CyanBright.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "DEPOSIT GATEWAY (UPI)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TextLight,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Scan QR below to transfer secure funds, then enter simulated verification code.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Custom Painted Neo-QR Asset
                            Box(
                                modifier = Modifier
                                    .size(180.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Painted QR canvas grid simulation
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val sizeX = size.width
                                    val sizeY = size.height

                                    // Outer borders corners
                                    drawRoundRect(Color.Black, Offset(0f, 0f), Size(50f, 50f), CornerRadius(4f))
                                    drawRoundRect(Color.White, Offset(10f, 10f), Size(30f, 30f), CornerRadius(4f))
                                    drawRoundRect(Color.Black, Offset(15f, 15f), Size(20f, 20f), CornerRadius(4f))

                                    drawRoundRect(Color.Black, Offset(sizeX - 50f, 0f), Size(50f, 50f), CornerRadius(4f))
                                    drawRoundRect(Color.White, Offset(sizeX - 40f, 10f), Size(30f, 30f), CornerRadius(4f))
                                    drawRoundRect(Color.Black, Offset(sizeX - 35f, 15f), Size(20f, 20f), CornerRadius(4f))

                                    drawRoundRect(Color.Black, Offset(0f, sizeY - 50f), Size(50f, 50f), CornerRadius(4f))
                                    drawRoundRect(Color.White, Offset(10f, sizeY - 40f), Size(30f, 30f), CornerRadius(4f))
                                    drawRoundRect(Color.Black, Offset(15f, sizeY - 35f), Size(20f, 20f), CornerRadius(4f))

                                    // Scattered tactical dots simulating QR Code
                                    drawRect(Color.Black, Offset(80f, 20f), Size(20f, 15f))
                                    drawRect(Color.Black, Offset(120f, 10f), Size(15f, 35f))
                                    drawRect(Color.Black, Offset(60f, 80f), Size(40f, 10f))
                                    drawRect(Color.Black, Offset(110f, 70f), Size(25f, 25f))

                                    drawRect(Color.Black, Offset(10f, 90f), Size(35f, 15f))
                                    drawRect(Color.Black, Offset(120f, 110f), Size(40f, 20f))
                                    drawRect(Color.Black, Offset(50f, 120f), Size(25f, 30f))
                                    drawRect(Color.Black, Offset(90f, 140f), Size(30f, 10f))

                                    // Central gaming flame icon
                                    drawCircle(OrangeFiery, 18f, center)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Official Organizer UPI ID:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Text(
                                text = viewModel.adminUpiId,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = AmberGold
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = viewModel.depositAmount,
                                onValueChange = { viewModel.depositAmount = it },
                                label = { Text("Transfer Cash Amount (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                trailingIcon = { Icon(Icons.Filled.Payment, contentDescription = null, tint = OrangeFiery) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = viewModel.depositReceiptRef,
                                onValueChange = { viewModel.depositReceiptRef = it },
                                label = { Text("UPI Transaction ID / Ref No.") },
                                placeholder = { Text("Txn 184029410") },
                                trailingIcon = { Icon(Icons.Filled.Check, contentDescription = null, tint = CyanBright) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.depositSimulate() },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanBright),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(
                                    "VERIFY PAYMENT & DEPOSIT",
                                    color = SlateDarkBg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Withdrawal simulation block
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, OrangeFiery.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "SECURE WITHDRAWAL REQUEST",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TextLight
                            )
                            Text(
                                text = "Submit cash out request. Disbursed immediately by the Tournament Director.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = viewModel.withdrawalAmount,
                                onValueChange = { viewModel.withdrawalAmount = it },
                                label = { Text("Withdrawal Amount (₹)") },
                                placeholder = { Text("Enter withdrawal amount, e.g. 100") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = viewModel.withdrawalUpiId,
                                onValueChange = { viewModel.withdrawalUpiId = it },
                                label = { Text("Destination UPI ID") },
                                placeholder = { Text("yourusername@upi") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.withdrawSimulate() },
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeFiery),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("REQUEST CASH OUT", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            2 -> {
                // Ledger Statement Tab listing
                if (transactions.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "No transactions recorded yet in this register.",
                                modifier = Modifier.padding(24.dp),
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Account Statements",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextLight,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(transactions) { txn ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateSurface),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, SlateSurfaceCard)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val txColor = when (txn.type) {
                                    "WELCOME_BONUS", "PRIZE_WIN", "DEPOSIT" -> NeonGreen
                                    else -> CrimsonRed
                                }

                                val sign = when (txn.type) {
                                    "WELCOME_BONUS", "PRIZE_WIN", "DEPOSIT" -> "+"
                                    else -> "-"
                                }

                                val leadingIcon = when (txn.type) {
                                    "WELCOME_BONUS" -> Icons.Filled.AddCard
                                    "PRIZE_WIN" -> Icons.Filled.EmojiEvents
                                    "DEPOSIT" -> Icons.Filled.ArrowCircleDown
                                    "WITHDRAWAL" -> Icons.Filled.ArrowCircleUp
                                    else -> Icons.Filled.LocalAtm
                                }

                                Icon(
                                    imageVector = leadingIcon,
                                    contentDescription = null,
                                    tint = txColor,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = txn.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = txn.type,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (txn.status == "COMPLETED") NeonGreen.copy(alpha = 0.15f)
                                                    else AmberGold.copy(alpha = 0.15f)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = txn.status,
                                                fontSize = 8.sp,
                                                color = if (txn.status == "COMPLETED") NeonGreen else AmberGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "$sign ₹ ${txn.amount.toInt()}",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                                    color = txColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// Registered Scheduled Matches view
@Composable
fun UserMyRegistrationsView(
    registrations: List<Registration>,
    tournaments: List<Tournament>,
    viewModel: TournamentViewModel
) {
    if (registrations.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Event, contentDescription = "schedule", tint = TextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("You are not registered in any tournament.", color = TextMuted)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(registrations) { reg ->
                val tournament = tournaments.find { it.id == reg.tournamentId }
                if (tournament != null) {
                    TournamentItemCard(tourney = tournament) {
                        viewModel.selectTournament(tournament.id)
                        viewModel.navigateTo(Screen.UserTournamentDetails(tournament.id))
                    }
                }
            }
        }
    }
}


// ==================== USER TOURNAMENT DETAILS (WITH LIVE BRACKET TRACKER) ====================
@Composable
fun UserTournamentDetailsLayout(viewModel: TournamentViewModel, tournamentId: Int) {
    val selectedTourney by viewModel.selectedTournamentFlow.collectAsState()
    val registrations by viewModel.selectedTourneyRegistrations.collectAsState()
    val brackets by viewModel.selectedTourneyBrackets.collectAsState()
    val userFlow = viewModel.currentUser.collectAsState()
    val user = userFlow.value ?: return

    var userFreeFireUidInForm by remember { mutableStateOf(user.freeFireId) }
    var userContactPhoneInForm by remember { mutableStateOf(user.mobileNumber) }

    var isAddingDispute by remember { mutableStateOf(false) }
    var disputeMatchIdInput by remember { mutableStateOf("") }
    var disputeText by remember { mutableStateOf("") }

    val isRegistered = remember(registrations, user) {
        registrations.any { it.userId == user.id }
    }

    val tourney = selectedTourney
    if (tourney == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangeFiery)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back toolbar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo(Screen.UserDashboard) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextLight)
                }
                Text(
                    text = "Tournament Details",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Header Title Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = tourney.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = OrangeFiery
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Badge(containerColor = SlateSurface, contentColor = TextLight) {
                            Text(
                                "MODE: " + tourney.gameMode.uppercase(Locale.getDefault()),
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Badge(containerColor = SlateSurface, contentColor = TextLight) {
                            Text("MAP: " + tourney.mapName, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }

        // --- SECTION: LIVE BRACKET VISUALIZER (TRIVIAL FOR PLAYER FEEDBACK) ---
        if (tourney.status == "ONGOING" || tourney.status == "COMPLETED") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountTree,
                        contentDescription = "bracket tracker icon",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Battle Brackets Tracker",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                }
            }

            item {
                // Brackets Tree Layout container
                BracketHorizontalTreeView(brackets)
            }
        }

        // Checkout Register Slot UI (Only if REGISTRATION_OPEN and not already registered)
        if (tourney.status == "REGISTRATION_OPEN" && !isRegistered) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, AmberGold)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SECURE SLOT CHECKOUT",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = AmberGold
                        )
                        Text(
                            text = "Register Free Fire profile details below. Entry fee of ₹ ${tourney.entryFee.toInt()} will be deducted.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = userFreeFireUidInForm,
                            onValueChange = { userFreeFireUidInForm = it },
                            label = { Text("Your Free Fire UID") },
                            placeholder = { Text("E.g. 524108429") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = userContactPhoneInForm,
                            onValueChange = { userContactPhoneInForm = it },
                            label = { Text("WhatsApp Contact Mobile") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (user.walletBalance < tourney.entryFee) {
                            Text(
                                "Insufficient balance. Deposit ₹ ${tourney.entryFee.toInt()} cash inside Wallet tab to join.",
                                color = CrimsonRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        val buttonGradient = if (user.walletBalance >= tourney.entryFee) {
                            Brush.horizontalGradient(colors = listOf(OrangeFiery, AmberGold))
                        } else {
                            Brush.horizontalGradient(colors = listOf(SlateSurface, SlateSurface))
                        }
                        Button(
                            onClick = {
                                viewModel.registerForTournament(tourney, userFreeFireUidInForm, userContactPhoneInForm)
                            },
                            enabled = user.walletBalance >= tourney.entryFee,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(buttonGradient)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CONFIRM REGISTER (₹ ${tourney.entryFee.toInt()})",
                                    color = if (user.walletBalance >= tourney.entryFee) Color.White else TextMuted,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                                )
                            }
                        }
                    }
                }
            }
        } else if (isRegistered) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Registered Verified",
                            tint = NeonGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "YOU ARE IN!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = NeonGreen
                            )
                            Text(
                                text = "Slot reserved. Room code SMS is auto-routed to: $userContactPhoneInForm",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Custom Rules Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rules & Regulations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tourney.rules.ifBlank { "Standard Esports tournament battle rules apply." },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        // --- DISPUTES LODGE FORM ---
        if (isRegistered && tourney.status == "ONGOING") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Dispute Redressal Desk",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonRed
                        )
                        Text(
                            text = "Experienced hacking, teaming, or score mismatch? Open a formal case instantly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (!isAddingDispute) {
                            Button(
                                onClick = { isAddingDispute = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SlateSurface),
                                border = BorderStroke(1.dp, CrimsonRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("LODGE DISPUTE CASE", color = CrimsonRed)
                            }
                        } else {
                            OutlinedTextField(
                                value = disputeMatchIdInput,
                                onValueChange = { disputeMatchIdInput = it },
                                label = { Text("Bracket Match ID (E.g. Match #1)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = disputeText,
                                onValueChange = { disputeText = it },
                                label = { Text("What happened?") },
                                placeholder = { Text("Teaming up, emulator bypass, etc. Please elaborate.") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        val matchIntVal = disputeMatchIdInput.toIntOrNull() ?: 1
                                        viewModel.submitDispute(tourney.id, matchIntVal, disputeText)
                                        disputeText = ""
                                        disputeMatchIdInput = ""
                                        isAddingDispute = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("SUBMIT EVIDENCE", color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = { isAddingDispute = false },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel", color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// --- BEAUTIFUL HORIZONTAL BRACKET TREE IMPLEMENTATION ---
@Composable
fun BracketHorizontalTreeView(brackets: List<BracketNode>) {
    val qfMatches = brackets.filter { it.roundNumber == 1 }
    val sfMatches = brackets.filter { it.roundNumber == 2 }
    val finalMatch = brackets.find { it.roundNumber == 3 }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateSurfaceCard)
            .horizontalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // QF column
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.width(180.dp)) {
                Text(
                    text = "Quarterfinals (R1)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = OrangeFiery
                )
                if (qfMatches.isEmpty()) {
                    Text("No brackets preloaded", color = TextMuted)
                } else {
                    for (node in qfMatches) {
                        BracketNodeUICompact(node = node)
                    }
                }
            }

            // SF column
            Column(verticalArrangement = Arrangement.spacedBy(36.dp), modifier = Modifier.width(180.dp)) {
                Text(
                    text = "Semifinals (R2)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = AmberGold
                )
                if (sfMatches.isEmpty()) {
                    Text("No brackets", color = TextMuted)
                } else {
                    for (node in sfMatches) {
                        BracketNodeUICompact(node = node)
                    }
                }
            }

            // Final column
            Column(verticalArrangement = Arrangement.spacedBy(54.dp), modifier = Modifier.width(180.dp)) {
                Text(
                    text = "Grand Finals (R3)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = CyanBright
                )
                if (finalMatch != null) {
                    BracketNodeUICompact(node = finalMatch)
                } else {
                    Text("No finals bracket available", color = TextMuted)
                }
            }
        }
    }
}

@Composable
fun BracketNodeUICompact(node: BracketNode) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = BorderStroke(
            1.dp,
            if (node.matchStatus == "LIVE") OrangeFiery else SlateSurfaceCard
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Status tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MATCH #${node.matchIndex}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextMuted
                )
                // live tag
                val sColor = when (node.matchStatus) {
                    "LIVE" -> OrangeFiery
                    "VERIFIED" -> NeonGreen
                    else -> TextMuted
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(sColor.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = node.matchStatus, fontSize = 7.sp, color = sColor, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Player 1 line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isP1Winner = node.winnerName == node.player1Name && node.winnerName.isNotBlank()
                Text(
                    text = node.player1Name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isP1Winner) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isP1Winner) AmberGold else TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = node.player1Score.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                    color = if (isP1Winner) AmberGold else TextLight
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player 2 line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isP2Winner = node.winnerName == node.player2Name && node.winnerName.isNotBlank()
                Text(
                    text = node.player2Name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isP2Winner) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isP2Winner) AmberGold else TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = node.player2Score.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
                    color = if (isP2Winner) AmberGold else TextLight
                )
            }
        }
    }
}


// ==================== ADMINISTRATIVE WORKPLACE PANEL ====================
@Composable
fun AdminDashboardLayout(viewModel: TournamentViewModel) {
    val tournaments by viewModel.allTournaments.collectAsState()
    val totalDisputes by viewModel.allDisputes.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    var activeAdminTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
    ) {
        // Organizer Top Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateSurface)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ORGANIZATION BACKOFFICE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                    color = AmberGold
                )
                Text(
                    text = "Organizer Panel",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextLight
                )
            }

            IconButton(onClick = { viewModel.logout() }) {
                Icon(Icons.Filled.Logout, contentDescription = "Exit to Player View", tint = CrimsonRed)
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = activeAdminTab,
            containerColor = SlateSurfaceCard,
            contentColor = AmberGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeAdminTab]),
                    color = AmberGold,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = activeAdminTab == 0,
                onClick = { activeAdminTab = 0 },
                text = { Text("TOURNAMENTS", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = activeAdminTab == 1,
                onClick = { activeAdminTab = 1 },
                text = { Text("LIVE BRACKETS", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = activeAdminTab == 2,
                onClick = { activeAdminTab = 2 },
                text = { Text("DISPUTES", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = activeAdminTab == 3,
                onClick = { activeAdminTab = 3 },
                text = { Text("PAYOUTS & PAYMENTS", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
            Tab(
                selected = activeAdminTab == 4,
                onClick = { activeAdminTab = 4 },
                text = { Text("TICKER ALERTS", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            )
        }

        // Active layout swap
        Crossfade(
            targetState = activeAdminTab,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "AdminDashboardTabCrossfade"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> AdminTournamentsManagerView(tournaments, viewModel)
                1 -> AdminScoreMonitorBracketsView(tournaments, viewModel)
                2 -> AdminDisputesDeskView(totalDisputes, viewModel)
                3 -> AdminPayoutsPaymentsSettingsView(allTransactions, viewModel)
                4 -> AdminAnnouncementTickerSettingsView(viewModel)
            }
        }
    }
}


// Admin Tab 0: Create & Delete Tournaments (With custom entry fees, first/second rewards, map selections)
@Composable
fun AdminTournamentsManagerView(tournaments: List<Tournament>, viewModel: TournamentViewModel) {
    var expandedTournamentId by remember { mutableStateOf<Int?>(null) }
    val registrations by viewModel.selectedTourneyRegistrations.collectAsState()

    LaunchedEffect(expandedTournamentId) {
        if (expandedTournamentId != null) {
            viewModel.selectTournament(expandedTournamentId!!)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Create New Tournament form card
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LAUNCH NEW CUSTOM TOURNAMENT",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = viewModel.newTourneyTitle,
                        onValueChange = { viewModel.newTourneyTitle = it },
                        label = { Text("Tournament Public Title") },
                        placeholder = { Text("E.g. Sunday Night Clash Squad Pro") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = viewModel.newTourneyMode,
                            onValueChange = { viewModel.newTourneyMode = it },
                            label = { Text("Game Mode") },
                            placeholder = { Text("Solo, Squad") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = viewModel.newTourneyMap,
                            onValueChange = { viewModel.newTourneyMap = it },
                            label = { Text("Custom Map") },
                            placeholder = { Text("Bermuda") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = viewModel.newTourneyEntryFee,
                            onValueChange = { viewModel.newTourneyEntryFee = it },
                            label = { Text("Entry Fee (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = viewModel.newTourneyPrizePool,
                            onValueChange = { viewModel.newTourneyPrizePool = it },
                            label = { Text("Total Prize Pool (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = viewModel.newTourneyFirstPrize,
                            onValueChange = { viewModel.newTourneyFirstPrize = it },
                            label = { Text("1st Place Prize (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = viewModel.newTourneySecondPrize,
                            onValueChange = { viewModel.newTourneySecondPrize = it },
                            label = { Text("2nd Place Prize (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.newTourneyRules,
                        onValueChange = { viewModel.newTourneyRules = it },
                        label = { Text("Custom Rules") },
                        placeholder = { Text("No Emulators. Send final match snapshot to lobby...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.createTournament() },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "PUBLISH ARENA CONTEST",
                            color = SlateDarkBg,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Currently Scheduled Contests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextLight
            )
        }

        items(tournaments) { tourney ->
            val isExpanded = expandedTournamentId == tourney.id
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isExpanded) AmberGold.copy(alpha = 0.5f) else Color.Transparent
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tourney.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${tourney.gameMode} • ${tourney.mapName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        IconButton(onClick = { viewModel.deleteTournament(tourney) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "delete", tint = CrimsonRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = SlateSurface, contentColor = TextLight) {
                                Text(tourney.status, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Badge(containerColor = SlateSurface, contentColor = TextLight) {
                                Text("Spots: ${tourney.registeredCount}/${tourney.maxTeams}", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Visibility Indicator tag
                        val visColor = if (tourney.isVisible) NeonGreen else CrimsonRed
                        val visText = if (tourney.isVisible) "PUBLIC" else "HIDDEN"
                        Badge(containerColor = visColor.copy(alpha = 0.15f), contentColor = visColor) {
                            Text(visText, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SlateSurface, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Toggle Visibility switch row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                onClick = { viewModel.toggleTournamentVisibility(tourney) }
                            ).padding(4.dp)
                        ) {
                            Icon(
                                imageVector = if (tourney.isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle Visibility Icon",
                                tint = if (tourney.isVisible) NeonGreen else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (tourney.isVisible) "Visible to Players" else "Hidden from Players",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = if (tourney.isVisible) TextLight else TextMuted
                            )
                        }

                        // View Participants button
                        TextButton(
                            onClick = {
                                expandedTournamentId = if (isExpanded) null else tourney.id
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (isExpanded) AmberGold else OrangeFiery
                            )
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Expand Registrants",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isExpanded) "Hide Registered" else "View Registered (${tourney.registeredCount})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Expanded section for registered participants list
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateSurface)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "REGISTERED PARTICIPANTS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = AmberGold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (registrations.isEmpty()) {
                                    Text(
                                        text = "No participants registered for this arena contest yet.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    registrations.forEachIndexed { index, reg ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(AmberGold.copy(alpha = 0.15f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = AmberGold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = reg.username,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                        color = TextLight
                                                    )
                                                    Text(
                                                        text = "UID: ${reg.freeFireUid} • Phone: ${reg.contactPhone}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = TextMuted
                                                    )
                                                }
                                            }

                                            // Kick participant action to make it fully functional skeleton
                                            IconButton(
                                                onClick = { viewModel.deleteParticipant(reg, tourney) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Remove participant",
                                                    tint = CrimsonRed.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        if (index < registrations.size - 1) {
                                            HorizontalDivider(color = SlateSurfaceCard, thickness = 1.dp, modifier = Modifier.padding(vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// Admin Tab 1: Live Score & Brackets monitor
@Composable
fun AdminScoreMonitorBracketsView(tournaments: List<Tournament>, viewModel: TournamentViewModel) {
    var selectedToMonitorTournament by remember { mutableStateOf<Tournament?>(null) }
    val brackets by viewModel.selectedTourneyBrackets.collectAsState()
    val registrations by viewModel.selectedTourneyRegistrations.collectAsState()

    // Scopes block
    LaunchedEffect(selectedToMonitorTournament) {
        selectedToMonitorTournament?.let {
            viewModel.selectTournament(it.id)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (selectedToMonitorTournament == null) {
            item {
                Text(
                    text = "Select Tournament to Monitor & Edit Scores:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextLight
                )
            }

            items(tournaments) { tourney ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedToMonitorTournament = tourney },
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = tourney.title,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextLight
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Status: ${tourney.status}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Text(text = "Spots filled: ${tourney.registeredCount}/${tourney.maxTeams}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            }
        } else {
            val liveTourney = selectedToMonitorTournament!!

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedToMonitorTournament = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back", tint = TextLight)
                    }
                    Text(
                        text = "Score & Bracket Control Panel",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                }
            }

            item {
                // Info display
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = liveTourney.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = OrangeFiery)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (liveTourney.status == "REGISTRATION_OPEN") {
                            Text(
                                text = "Tournament hasn't started yet. Start match series to generate custom 8-player Quarterfinals brackets.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.startTournamentAndGenerateBrackets(liveTourney)
                                    // reload
                                    selectedToMonitorTournament = liveTourney.copy(status = "ONGOING")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("LAUNCH ESPORTS & BUILD BRACKETS", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                            }
                        } else if (liveTourney.status == "ONGOING") {
                            Text(
                                text = "Automatic prize payout active. Once final Round 3 winner is declared, tap Finalize below.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.finalizeTournamentAndDistributePrizes(liveTourney)
                                    selectedToMonitorTournament = liveTourney.copy(status = "COMPLETED")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("FINALIZE & AUTO-DISTRIBUTE PRIZES", color = SlateDarkBg, fontWeight = FontWeight.ExtraBold)
                            }
                        } else {
                            Text(
                                text = "🏆 COMPLETED. PRIZES DISTRIBUTED TO WALLETS AUTOMATICALLY.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonGreen
                            )
                        }
                    }
                }
            }

            if (liveTourney.status != "REGISTRATION_OPEN") {
                item {
                    Text(text = "Live Match Bracket Nodes:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextLight)
                }

                items(brackets) { node ->
                    AdminMatchModifierRow(node, viewModel)
                }
            } else {
                // Participant Registrations control list
                item {
                    Text(
                        text = "Verify & Edit Registered Participants (${registrations.size}):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextLight
                    )
                }

                if (registrations.isEmpty()) {
                    item {
                        Text("No participants registered yet.", color = TextMuted)
                    }
                } else {
                    items(registrations) { reg ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, SlateSurface)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (viewModel.editingRegistrationId == reg.id) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        OutlinedTextField(
                                            value = viewModel.editedRegUsername,
                                            onValueChange = { viewModel.editedRegUsername = it },
                                            label = { Text("Display IGN") }
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = viewModel.editedRegFreeFireUid,
                                            onValueChange = { viewModel.editedRegFreeFireUid = it },
                                            label = { Text("Free Fire UID") }
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row {
                                            Button(
                                                onClick = {
                                                    viewModel.editParticipant(reg.id, viewModel.editedRegUsername, viewModel.editedRegFreeFireUid)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                                            ) {
                                                Text("Save", color = SlateDarkBg)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            OutlinedButton(onClick = { viewModel.editingRegistrationId = null }) {
                                                Text("Cancel")
                                            }
                                        }
                                    }
                                } else {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = reg.username, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextLight)
                                        Text(text = "UID: " + reg.freeFireUid, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                        Text(text = "Phone: " + reg.contactPhone, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                viewModel.editingRegistrationId = reg.id
                                                viewModel.editedRegUsername = reg.username
                                                viewModel.editedRegFreeFireUid = reg.freeFireUid
                                            }
                                        ) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Edit Player Info", tint = AmberGold)
                                        }

                                        IconButton(onClick = { viewModel.deleteParticipant(reg, liveTourney) }) {
                                            Icon(Icons.Filled.GroupRemove, contentDescription = "Kick registration Player", tint = CrimsonRed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMatchModifierRow(bracket: BracketNode, viewModel: TournamentViewModel) {
    var p1ScoreState by remember(bracket.player1Score) { mutableIntStateOf(bracket.player1Score) }
    var p2ScoreState by remember(bracket.player2Score) { mutableIntStateOf(bracket.player2Score) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val rndText = when (bracket.roundNumber) {
                    1 -> "QF (R1)"
                    2 -> "SF (R2)"
                    else -> "Grand Finals"
                }
                Text(
                    text = "$rndText - Match #${bracket.matchIndex}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = AmberGold
                )

                // Match flow states controller buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { viewModel.updateMatchScore(bracket, p1ScoreState, p2ScoreState, "LIVE") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (bracket.matchStatus == "LIVE") OrangeFiery else SlateSurface
                        ),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("LIVE", fontSize = 9.sp)
                    }

                    Button(
                        onClick = { viewModel.updateMatchScore(bracket, p1ScoreState, p2ScoreState, "VERIFIED") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (bracket.matchStatus == "VERIFIED") NeonGreen else SlateSurface
                        ),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("VERIFY", fontSize = 9.sp, color = if (bracket.matchStatus == "VERIFIED") SlateDarkBg else TextLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player 1 controller rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bracket.player1Name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextLight,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { p1ScoreState = maxOf(0, p1ScoreState - 1) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Filled.RemoveCircleOutline, contentDescription = null, tint = TextMuted)
                    }
                    Text(
                        text = p1ScoreState.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AmberGold,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    IconButton(
                        onClick = { p1ScoreState += 1 },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Player 2 controller rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bracket.player2Name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextLight,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { p2ScoreState = maxOf(0, p2ScoreState - 1) },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Filled.RemoveCircleOutline, contentDescription = null, tint = TextMuted)
                    }
                    Text(
                        text = p2ScoreState.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = AmberGold,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    IconButton(
                        onClick = { p2ScoreState += 1 },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = TextMuted)
                    }
                }
            }
        }
    }
}


// Admin Tab 2: Disputes Desk Desk View
@Composable
fun AdminDisputesDeskView(disputes: List<Dispute>, viewModel: TournamentViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Disputes Redressal Center",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextLight
            )
        }

        if (disputes.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard), modifier = Modifier.fillMaxWidth()) {
                    Text("No disputes filed from players currently.", modifier = Modifier.padding(24.dp), color = TextMuted, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(disputes) { dispute ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(
                        1.dp,
                        if (dispute.status == "PENDING") CrimsonRed else SlateSurfaceCard
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Filed by: " + dispute.username,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextLight
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (dispute.status == "PENDING") CrimsonRed.copy(alpha = 0.15f)
                                        else NeonGreen.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = dispute.status,
                                    color = if (dispute.status == "PENDING") CrimsonRed else NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = "COMPLAINT DESCRIPTION:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = dispute.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextLight,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        if (dispute.status == "PENDING") {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "SELECT IMMEDIATE ACTION:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.resolveDisputeAndCloseCase(dispute, "RESOLVED_PLAYER1") },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangeFiery),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("RESOLVE FOR P1", fontSize = 10.sp)
                                }

                                Button(
                                    onClick = { viewModel.resolveDisputeAndCloseCase(dispute, "RESOLVED_PLAYER2") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("RESOLVE FOR P2", fontSize = 10.sp, color = SlateDarkBg)
                                }

                                Button(
                                    onClick = { viewModel.resolveDisputeAndCloseCase(dispute, "DISMISSED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateSurface),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("DISMISS", fontSize = 10.sp)
                                }
                            }
                        } else {
                            if (dispute.adminAnnouncement.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SlateSurface)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = dispute.adminAnnouncement,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = NeonGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


// Admin Tab 3: Withdrawal Cash Payout Approval & QR Setting edit
@Composable
fun AdminPayoutsPaymentsSettingsView(transactions: List<Transaction>, viewModel: TournamentViewModel) {
    val pendingWithdrawals = remember(transactions) {
        transactions.filter { it.type == "WITHDRAWAL" }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Edit payment QR settings
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "UPGRADE ORGANIZER UPI SYSTEMS QR ID",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = AmberGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = viewModel.adminUpiId,
                        onValueChange = { viewModel.adminUpiId = it },
                        label = { Text("Your QR Payout Merchant UPI ID") },
                        placeholder = { Text("pay@yourupiaddress") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.saveAdminSettings() },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SAVE MERCHANT UPI ID", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Player Cash Out Requests",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextLight
            )
        }

        if (pendingWithdrawals.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard), modifier = Modifier.fillMaxWidth()) {
                    Text("No player payout cash out requests.", modifier = Modifier.padding(24.dp), color = TextMuted, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(pendingWithdrawals) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Sum: ₹ " + req.amount.toInt().toString(),
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = OrangeFiery
                                )
                                Text(
                                    text = req.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextLight
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (req.status == "PENDING") AmberGold.copy(alpha = 0.15f)
                                        else NeonGreen.copy(alpha = 0.15f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = req.status,
                                    color = if (req.status == "PENDING") AmberGold else NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (req.status == "PENDING") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.approvePayoutRequest(req) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("APPROVE INSTANT PAY OUT", color = SlateDarkBg, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}


// Admin Tab 4: System Billboard announcements setting
@Composable
fun AdminAnnouncementTickerSettingsView(viewModel: TournamentViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurfaceCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "REAL-TIME BILLBOARD BROADCAST ticker",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = OrangeFiery
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This announcement message runs dynamically at the very top header across all players' screens.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.adminAnnouncementText,
                    onValueChange = { viewModel.adminAnnouncementText = it },
                    label = { Text("Broadcasting Bulletin Text") },
                    placeholder = { Text("E.g. Room ID pw is posted inside sunday cups details...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.saveAdminSettings() },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeFiery),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("BROADCAST ANNOUNCEMENT LIVE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== PLAYER STATISTICS & LEADERBOARD VIEW ====================
@Composable
fun PlayerStatsLeaderboardView(viewModel: TournamentViewModel) {
    val statsList by viewModel.allPlayerStats.collectAsState()
    val currentUserStats by viewModel.currentUserStats.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Personal Statistics Header ---
        item {
            Text(
                text = "YOUR PERFORMANCE",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                color = OrangeFiery,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val stats = currentUserStats
            if (stats != null) {
                // Card for Personal Stats
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SlateSurface, SlateSurfaceCard)
                            )
                        )
                        .border(1.dp, SlateSurfaceCard, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stats.username,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                    color = TextLight
                                )
                                Text(
                                    text = "UID: ${stats.freeFireId.ifBlank { "Not linked" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            // Rank Score Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrangeFiery.copy(alpha = 0.15f))
                                    .border(1.dp, OrangeFiery.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${stats.scorePoints} PTS",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                                    color = OrangeFiery
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stat Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Win Rate
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "WIN RATE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${String.format("%.1f", stats.winRate)}%",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = AmberGold
                                    )
                                }
                            }
                            // K/D Ratio
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "K/D RATIO",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = String.format("%.2f", stats.kdRatio),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = CyanBright
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Total Kills
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "TOTAL KILLS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${stats.totalKills}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = TextLight
                                    )
                                }
                            }
                            // Tournaments Won
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SlateDarkBg),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "TOURNEYS WON",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${stats.tournamentsWon}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = NeonGreen
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SlateSurfaceCard)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Complete matches to generate statistics.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // --- Global Leaderboard Header & Search ---
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GLOBAL LEADERBOARD",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                        color = OrangeFiery
                    )

                    Text(
                        text = "${statsList.size} SOLDERS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Soldier...", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        focusedContainerColor = SlateSurface,
                        unfocusedContainerColor = SlateSurface,
                        focusedBorderColor = OrangeFiery,
                        unfocusedBorderColor = SlateSurfaceCard,
                        focusedPlaceholderColor = TextMuted,
                        unfocusedPlaceholderColor = TextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search icon", tint = TextMuted)
                    }
                )
            }
        }

        // --- Leaderboard rows ---
        val filteredStats = statsList.filter {
            it.username.contains(searchQuery, ignoreCase = true) || it.freeFireId.contains(searchQuery, ignoreCase = true)
        }

        if (filteredStats.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players found matching your query.",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(filteredStats.size) { index ->
                val stats = filteredStats[index]
                val rank = index + 1
                val isSelf = stats.userId == currentUser?.id

                ListItem(
                    modifier = Modifier
                        .padding(vertical = 2.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isSelf) 1.dp else 0.dp,
                            color = if (isSelf) OrangeFiery else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { /* Click row and view stats */ },
                    colors = ListItemDefaults.colors(
                        containerColor = if (isSelf) SlateSurfaceCard else SlateSurface,
                        headlineColor = TextLight,
                        supportingColor = TextMuted
                    ),
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when (rank) {
                                        1 -> Color(0xFFFFD700) // Gold
                                        2 -> Color(0xFFC0C0C0) // Silver
                                        3 -> Color(0xFFCD7F32) // Bronze
                                        else -> SlateDarkBg
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$rank",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                color = if (rank in 1..3) Color.Black else TextLight
                            )
                        }
                    },
                    headlineContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stats.username,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelf) OrangeFiery else TextLight
                            )
                            if (isSelf) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(OrangeFiery.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "YOU",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 8.sp),
                                        color = OrangeFiery
                                    )
                                }
                            }
                        }
                    },
                    supportingContent = {
                        Text(
                            text = "WR: ${String.format("%.1f", stats.winRate)}% • KD: ${String.format("%.2f", stats.kdRatio)} • Kills: ${stats.totalKills}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${stats.scorePoints}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = if (rank == 1) Color(0xFFFFD700) else OrangeFiery
                            )
                            Text(
                                text = "PTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                )
            }
        }
    }
}
