package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.TournamentRepository
import com.example.util.FirebaseAuthHelper
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Signup : Screen()
    object UserDashboard : Screen()
    object UserWallet : Screen()
    data class UserTournamentDetails(val tournamentId: Int) : Screen()
    object AdminDashboard : Screen()
    data class AdminTournamentDetails(val tournamentId: Int) : Screen()
}

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TournamentRepository

    // Screen State
    var currentScreen by mutableStateOf<Screen>(Screen.Welcome)
        private set

    // Auth States
    var currentUser = MutableStateFlow<User?>(null)
        private set

    // Active Forms State
    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)

    var signupEmail by mutableStateOf("")
    var signupUsername by mutableStateOf("")
    var signupPassword by mutableStateOf("")
    var signupMobile by mutableStateOf("")
    var signupFreeFireId by mutableStateOf("")
    var signupError by mutableStateOf<String?>(null)
    var isOtpVerificationPending by mutableStateOf(false)

    // Google Sign-In Native States
    var isGoogleSignInInProgress by mutableStateOf(false)
    var googleSignInError by mutableStateOf<String?>(null)
    var showGoogleSetupDialog by mutableStateOf(false)
    var simulatedEnteredOtp by mutableStateOf("")
    var signupTempUser: User? = null

    // Wallet transaction values
    var withdrawalAmount by mutableStateOf("")
    var withdrawalUpiId by mutableStateOf("")
    var depositAmount by mutableStateOf("")
    var depositReceiptRef by mutableStateOf("")

    // Admin Forms
    var newTourneyTitle by mutableStateOf("")
    var newTourneyMode by mutableStateOf("Solo") // "Solo", "Duo", "Squad"
    var newTourneyMap by mutableStateOf("Bermuda") // "Bermuda", "Purgatory", "Kalahari", "Alpine"
    var newTourneyEntryFee by mutableStateOf("10")
    var newTourneyPrizePool by mutableStateOf("1000")
    var newTourneyFirstPrize by mutableStateOf("700")
    var newTourneySecondPrize by mutableStateOf("300")
    var newTourneyRules by mutableStateOf("")

    // Admin QR Settings
    var adminUpiId by mutableStateOf("esportspay@upi")
    var adminAnnouncementText by mutableStateOf("")

    // Edit registration state
    var editingRegistrationId by mutableStateOf<Int?>(null)
    var editedRegUsername by mutableStateOf("")
    var editedRegFreeFireUid by mutableStateOf("")

    init {
        FirebaseAuthHelper.initialize(application)
        val database = AppDatabase.getDatabase(application)
        repository = TournamentRepository(database.tournamentDao())

        viewModelScope.launch {
            // Seed database instantly with gorgeous real-time data examples on startup
            repository.prepDatabaseWithInitialDataIfNeeded()
            // Recalculate stats on initial seed data so the leaderboards are populated
            repository.recalculateAllPlayerStats()
            // Fetch configuration settings
            val settings = repository.getAdminSetting()
            if (settings != null) {
                adminUpiId = settings.qrCodeUpiId
                adminAnnouncementText = settings.systemAnnouncement
            }
        }
    }

    // Navigations
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    // Exposure Flow variables
    val allTournaments: StateFlow<List<Tournament>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDisputes: StateFlow<List<Dispute>> = repository.allDisputes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSetting: StateFlow<AdminSetting?> = repository.adminSetting
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlayerStats: StateFlow<List<PlayerStats>> = repository.allPlayerStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUserStats: StateFlow<PlayerStats?> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getPlayerStatsFlow(user.id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val userTransactions: StateFlow<List<Transaction>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getTransactionsForUserFlow(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val userRegistrations: StateFlow<List<Registration>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.getRegistrationsForUserFlow(user.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Tournament Reactive states
    private val _selectedTournamentId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTournamentFlow: StateFlow<Tournament?> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getTournamentByIdFlow(id)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTourneyRegistrations: StateFlow<List<Registration>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getRegistrationsForTournamentFlow(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTourneyBrackets: StateFlow<List<BracketNode>> = _selectedTournamentId
        .flatMapLatest { id ->
            if (id != null) repository.getBracketsForTournamentFlow(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTournament(id: Int) {
        _selectedTournamentId.value = id
    }

    // --- Authentication Actions ---
    fun login() {
        if (loginEmail.isBlank() || loginPassword.isBlank()) {
            loginError = "Please enter both Email and Password"
            return
        }

        val auth = FirebaseAuthHelper.getAuth()
        if (auth != null) {
            auth.signInWithEmailAndPassword(loginEmail.trim(), loginPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        val uid = firebaseUser?.uid ?: ""
                        viewModelScope.launch {
                            var user = repository.getUserByFirebaseUid(uid)
                            if (user == null) {
                                user = repository.getUserByEmail(loginEmail.trim())
                                if (user != null) {
                                    val updatedUser = user.copy(firebaseUid = uid)
                                    repository.updateUser(updatedUser)
                                    user = updatedUser
                                } else {
                                    val isUserAdmin = loginEmail.trim() == "parraybabar04@gmail.com"
                                    val newUser = User(
                                        email = loginEmail.trim(),
                                        passwordHash = loginPassword,
                                        firebaseUid = uid,
                                        isVerified = true,
                                        username = loginEmail.substringBefore("@"),
                                        isAdmin = isUserAdmin,
                                        welcomeBonusClaimed = true,
                                        walletBalance = 10.0
                                    )
                                    val newId = repository.insertUser(newUser)
                                    repository.insertTransaction(
                                        Transaction(
                                            userId = newId.toInt(),
                                            type = "WELCOME_BONUS",
                                            amount = 10.0,
                                            description = "Secure welcome bonus applied on Firebase auto-register",
                                            status = "COMPLETED"
                                        )
                                    )
                                    user = repository.getUserById(newId.toInt())
                                }
                            }

                            currentUser.value = user
                            loginError = null
                            loginEmail = ""
                            loginPassword = ""
                            if (user != null && user.isAdmin) {
                                navigateTo(Screen.AdminDashboard)
                            } else {
                                navigateTo(Screen.UserDashboard)
                            }
                        }
                    } else {
                        android.util.Log.e("Auth", "Firebase login failed: ${task.exception?.message}")
                        loginLocalFallback(task.exception?.message ?: "Firebase authentication failed")
                    }
                }
        } else {
            loginLocalFallback(null)
        }
    }

    private fun loginLocalFallback(firebaseError: String?) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(loginEmail.trim())
            if (user != null && user.passwordHash == loginPassword) {
                currentUser.value = user
                loginError = null
                loginEmail = ""
                loginPassword = ""
                if (user.isAdmin) {
                    navigateTo(Screen.AdminDashboard)
                } else {
                    navigateTo(Screen.UserDashboard)
                }
            } else {
                loginError = firebaseError ?: "Invalid credentials. Please verify your Email and Password."
            }
        }
    }

    fun signup() {
        if (signupEmail.isBlank() || signupUsername.isBlank() || signupPassword.isBlank() || signupMobile.isBlank()) {
            signupError = "All fields are required for player profile safety"
            return
        }
        if (signupMobile.length < 5) {
            signupError = "Please provide a valid active Mobile Number"
            return
        }

        val auth = FirebaseAuthHelper.getAuth()
        if (auth != null) {
            auth.createUserWithEmailAndPassword(signupEmail.trim(), signupPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = task.result?.user
                        val uid = firebaseUser?.uid ?: ""
                        viewModelScope.launch {
                            signupTempUser = User(
                                email = signupEmail.trim(),
                                passwordHash = signupPassword,
                                mobileNumber = signupMobile.trim(),
                                isVerified = false,
                                welcomeBonusClaimed = false,
                                walletBalance = 0.0,
                                freeFireId = signupFreeFireId.trim(),
                                username = signupUsername.trim(),
                                isAdmin = signupEmail.trim() == "parraybabar04@gmail.com",
                                firebaseUid = uid
                            )
                            signupError = null
                            isOtpVerificationPending = true
                        }
                    } else {
                        android.util.Log.e("Auth", "Firebase signup failed: ${task.exception?.message}")
                        signupLocalFallback(task.exception?.message ?: "Firebase registration failed")
                    }
                }
        } else {
            signupLocalFallback(null)
        }
    }

    private fun signupLocalFallback(firebaseError: String?) {
        viewModelScope.launch {
            val existing = repository.getUserByEmail(signupEmail.trim())
            if (existing != null) {
                signupError = firebaseError ?: "User with this email already exists"
                return@launch
            }
            signupTempUser = User(
                email = signupEmail.trim(),
                passwordHash = signupPassword,
                mobileNumber = signupMobile.trim(),
                isVerified = false,
                welcomeBonusClaimed = false,
                walletBalance = 0.0,
                freeFireId = signupFreeFireId.trim(),
                username = signupUsername.trim(),
                isAdmin = signupEmail.trim() == "parraybabar04@gmail.com",
                firebaseUid = "LOCAL_${UUID.randomUUID()}"
            )
            signupError = null
            isOtpVerificationPending = true
        }
    }

    fun verifyOtp() {
        if (simulatedEnteredOtp == "1234" || simulatedEnteredOtp == "123456" || simulatedEnteredOtp.length >= 4) {
            viewModelScope.launch {
                val tempUser = signupTempUser
                if (tempUser != null) {
                    val hasClaimed = tempUser.email.isNotBlank() && (
                        repository.getUserByEmail(tempUser.email)?.welcomeBonusClaimed == true || 
                        (tempUser.firebaseUid != null && repository.getUserByFirebaseUid(tempUser.firebaseUid)?.welcomeBonusClaimed == true)
                    )

                    val bonusAmount = if (hasClaimed) 0.0 else 10.0
                    val finalUser = tempUser.copy(
                        isVerified = true,
                        welcomeBonusClaimed = !hasClaimed,
                        walletBalance = bonusAmount
                    )

                    val newId = repository.insertUser(finalUser)

                    if (!hasClaimed) {
                        repository.insertTransaction(
                            Transaction(
                                userId = newId.toInt(),
                                type = "WELCOME_BONUS",
                                amount = 10.0,
                                description = "Welcome bonus reward on mobile verification",
                                status = "COMPLETED"
                            )
                        )
                    }

                    val loggedIn = repository.getUserById(newId.toInt())
                    currentUser.value = loggedIn
                    signupTempUser = null
                    isOtpVerificationPending = false
                    simulatedEnteredOtp = ""
                    signupEmail = ""
                    signupUsername = ""
                    signupPassword = ""
                    signupMobile = ""
                    signupFreeFireId = ""
                    navigateTo(Screen.UserDashboard)
                }
            }
        } else {
            signupError = "Invalid simulated OTP. Try entering '1234' for verification verification."
        }
    }

    fun logout() {
        currentUser.value = null
        navigateTo(Screen.Welcome)
    }

    // --- Social / Auth Simulated logins ----
    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            isGoogleSignInInProgress = true
            googleSignInError = null

            // Try to obtain Web Client ID from BuildConfig
            val clientId = try {
                com.example.BuildConfig.GOOGLE_CLIENT_ID
            } catch (e: Exception) {
                ""
            }

            if (clientId.isBlank() || clientId == "your_client_id_here") {
                isGoogleSignInInProgress = false
                showGoogleSetupDialog = true
                return@launch
            }

            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(clientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val email = googleIdTokenCredential.id
                    val displayName = googleIdTokenCredential.displayName ?: "Google Player"

                    // Try to update with Firebase Auth if ready
                    val firebaseAuth = FirebaseAuthHelper.getAuth()
                    if (firebaseAuth != null) {
                        try {
                            val credentialObj = GoogleAuthProvider.getCredential(idToken, null)
                            firebaseAuth.signInWithCredential(credentialObj)
                                .addOnCompleteListener { authTask ->
                                    val fireUid = if (authTask.isSuccessful) authTask.result?.user?.uid else null
                                    viewModelScope.launch {
                                        val finalUid = fireUid ?: ("GOOGLE_FALLBACK_" + UUID.randomUUID().toString().take(8))
                                        var user = if (fireUid != null) repository.getUserByFirebaseUid(fireUid) else null
                                        if (user == null) {
                                            user = repository.getUserByEmail(email)
                                        }
                                        if (user == null) {
                                            user = User(
                                                email = email,
                                                passwordHash = "google_linked_" + UUID.randomUUID().toString().take(8),
                                                mobileNumber = "+91 " + (9000000000L + (Math.random() * 999999999L).toLong()).toString(),
                                                isVerified = true,
                                                welcomeBonusClaimed = true,
                                                walletBalance = 100.0,
                                                freeFireId = (10000000L + (Math.random() * 89999999L).toLong()).toString(),
                                                username = displayName.replace(" ", "_").replace(".", "") + "_FF",
                                                isAdmin = email.trim() == "parraybabar04@gmail.com",
                                                firebaseUid = finalUid
                                            )
                                            val newId = repository.insertUser(user)
                                            repository.insertTransaction(
                                                Transaction(
                                                    userId = newId.toInt(),
                                                    type = "WELCOME_BONUS",
                                                    amount = 10.0,
                                                    description = "Linked Google Account Welcome Gift",
                                                    status = "COMPLETED"
                                                )
                                            )
                                            user = repository.getUserById(newId.toInt())
                                        } else {
                                            if (user.firebaseUid != finalUid) {
                                                val updatedUser = user.copy(firebaseUid = finalUid)
                                                repository.updateUser(updatedUser)
                                                user = updatedUser
                                            }
                                        }
                                        currentUser.value = user
                                        navigateTo(if (user != null && user.isAdmin) Screen.AdminDashboard else Screen.UserDashboard)
                                    }
                                }
                        } catch (fe: Exception) {
                            android.util.Log.e("GoogleSignIn", "Firebase Auth credential linking failed: ${fe.message}")
                            // Fallback on catch
                            val finalUid = "GOOGLE_CATCH_" + UUID.randomUUID().toString().take(8)
                            var user = repository.getUserByEmail(email)
                            if (user == null) {
                                user = User(
                                    email = email,
                                    passwordHash = "google_linked_" + UUID.randomUUID().toString().take(8),
                                    mobileNumber = "+91 " + (9000000000L + (Math.random() * 999999999L).toLong()).toString(),
                                    isVerified = true,
                                    welcomeBonusClaimed = true,
                                    walletBalance = 100.0,
                                    freeFireId = (10000000L + (Math.random() * 89999999L).toLong()).toString(),
                                    username = displayName.replace(" ", "_").replace(".", "") + "_FF",
                                    isAdmin = email.trim() == "parraybabar04@gmail.com",
                                    firebaseUid = finalUid
                                )
                                val newId = repository.insertUser(user)
                                repository.insertTransaction(
                                    Transaction(
                                        userId = newId.toInt(),
                                        type = "WELCOME_BONUS",
                                        amount = 10.0,
                                        description = "Linked Google Account Welcome Gift (Local Fallback)",
                                        status = "COMPLETED"
                                    )
                                )
                                user = repository.getUserById(newId.toInt())
                            }
                            currentUser.value = user
                            navigateTo(if (user != null && user.isAdmin) Screen.AdminDashboard else Screen.UserDashboard)
                        }
                    } else {
                        // Firebase not available - Fall back to local
                        var user = repository.getUserByEmail(email)
                        val finalUid = "GOOGLE_LOCAL_" + UUID.randomUUID().toString().take(8)
                        if (user == null) {
                            user = User(
                                email = email,
                                passwordHash = "google_linked_" + UUID.randomUUID().toString().take(8),
                                mobileNumber = "+91 " + (9000000000L + (Math.random() * 999999999L).toLong()).toString(),
                                isVerified = true,
                                welcomeBonusClaimed = true,
                                walletBalance = 100.0,
                                freeFireId = (10000000L + (Math.random() * 89999999L).toLong()).toString(),
                                username = displayName.replace(" ", "_").replace(".", "") + "_FF",
                                isAdmin = email.trim() == "parraybabar04@gmail.com",
                                firebaseUid = finalUid
                            )
                            val newId = repository.insertUser(user)
                            repository.insertTransaction(
                                Transaction(
                                    userId = newId.toInt(),
                                    type = "WELCOME_BONUS",
                                    amount = 10.0,
                                    description = "Linked Google Account Welcome Gift (Local Fallback)",
                                    status = "COMPLETED"
                                )
                            )
                            user = repository.getUserById(newId.toInt())
                        }
                        currentUser.value = user
                        navigateTo(if (user != null && user.isAdmin) Screen.AdminDashboard else Screen.UserDashboard)
                    }
                } else {
                    googleSignInError = "Invalid credential payload received from Google Secure Sign In."
                    showGoogleSetupDialog = true
                }
            } catch (e: GetCredentialException) {
                android.util.Log.e("GoogleSignIn", "Google Login Failed: ${e.message}")
                googleSignInError = "Credential lookup failed: ${e.message}."
                showGoogleSetupDialog = true
            } catch (e: Exception) {
                android.util.Log.e("GoogleSignIn", "Unexpected Error: ${e.message}")
                googleSignInError = "Exception: ${e.message}"
                showGoogleSetupDialog = true
            } finally {
                isGoogleSignInInProgress = false
            }
        }
    }

    fun executeGoogleDemoLogin() {
        viewModelScope.launch {
            var user = repository.getUserByEmail("googleplayer@fft.com")
            if (user == null) {
                user = User(
                    email = "googleplayer@fft.com",
                    passwordHash = "google",
                    mobileNumber = "+91 9988776655",
                    isVerified = true,
                    welcomeBonusClaimed = true,
                    walletBalance = 100.0,
                    freeFireId = "88991100",
                    username = "GoogleWarrior_FF",
                    isAdmin = false
                )
                val newId = repository.insertUser(user)
                repository.insertTransaction(
                    Transaction(
                        userId = newId.toInt(),
                        type = "WELCOME_BONUS",
                        amount = 10.0,
                        description = "Simulated Linked Google Account Gift",
                        status = "COMPLETED"
                    )
                )
                user = repository.getUserById(newId.toInt())
            }
            currentUser.value = user
            navigateTo(Screen.UserDashboard)
            showGoogleSetupDialog = false
        }
    }

    fun loginWithApple() {
        viewModelScope.launch {
            var user = repository.getUserByEmail("appleplayer@fft.com")
            if (user == null) {
                user = User(
                    email = "appleplayer@fft.com",
                    passwordHash = "apple",
                    mobileNumber = "+1 4455660022",
                    isVerified = true,
                    welcomeBonusClaimed = true,
                    walletBalance = 250.0,
                    freeFireId = "66551234",
                    username = "AppleEsports_OP",
                    isAdmin = false
                )
                val newId = repository.insertUser(user)
                repository.insertTransaction(
                    Transaction(
                        userId = newId.toInt(),
                        type = "WELCOME_BONUS",
                        amount = 10.0,
                        description = "Linked Apple ID Welcome Reward",
                        status = "COMPLETED"
                    )
                )
                user = repository.getUserById(newId.toInt())
            }
            currentUser.value = user
            navigateTo(Screen.UserDashboard)
        }
    }

    // --- User Wallet Actions ---
    fun depositSimulate() {
        val amount = depositAmount.toDoubleOrNull() ?: 0.0
        val ref = depositReceiptRef.trim()
        val user = currentUser.value
        if (amount <= 0.0 || ref.isEmpty() || user == null) return

        viewModelScope.launch {
            // Update balance and record completed transaction
            val updatedUser = user.copy(walletBalance = user.walletBalance + amount)
            repository.updateUser(updatedUser)
            currentUser.value = updatedUser

            repository.insertTransaction(
                Transaction(
                    userId = user.id,
                    type = "DEPOSIT",
                    amount = amount,
                    description = "UPI deposit payment ref: $ref",
                    status = "COMPLETED"
                )
            )

            // Clear values
            depositAmount = ""
            depositReceiptRef = ""
        }
    }

    fun withdrawSimulate() {
        val amount = withdrawalAmount.toDoubleOrNull() ?: 0.0
        val upi = withdrawalUpiId.trim()
        val user = currentUser.value
        if (amount <= 0.0 || upi.isEmpty() || user == null) return
        if (user.walletBalance < amount) {
            // Can't withdraw more than balance
            return
        }

        viewModelScope.launch {
            // Deduct instantly and record PENDING transaction for administratve payout
            val updatedUser = user.copy(walletBalance = user.walletBalance - amount)
            repository.updateUser(updatedUser)
            currentUser.value = updatedUser

            repository.insertTransaction(
                Transaction(
                    userId = user.id,
                    type = "WITHDRAWAL",
                    amount = amount,
                    description = "Withdrawal payout to scale upi: $upi",
                    status = "PENDING"
                )
            )

            withdrawalAmount = ""
            withdrawalUpiId = ""
        }
    }

    // --- Tournament Registrations ---
    fun registerForTournament(tournament: Tournament, playerFreeFireUid: String, phone: String) {
        val user = currentUser.value ?: return
        if (playerFreeFireUid.isBlank() || phone.isBlank()) return

        if (user.walletBalance < tournament.entryFee) {
            // Insufficient wallet funds
            return
        }

        viewModelScope.launch {
            // Deduct entry fee
            val updatedUser = user.copy(walletBalance = user.walletBalance - tournament.entryFee)
            repository.updateUser(updatedUser)
            currentUser.value = updatedUser

            // Add Registration
            repository.insertRegistration(
                Registration(
                    tournamentId = tournament.id,
                    userId = user.id,
                    username = user.username.ifBlank { "Player_${user.id}" },
                    freeFireUid = playerFreeFireUid.trim(),
                    contactPhone = phone.trim()
                )
            )

            // Adjust tournament spots count
            val updatedTourney = tournament.copy(registeredCount = tournament.registeredCount + 1)
            repository.updateTournament(updatedTourney)
            selectTournament(tournament.id) // Reload details flow trigger

            // Record entry transaction
            repository.insertTransaction(
                Transaction(
                    userId = user.id,
                    type = "ENTRY_FEE",
                    amount = tournament.entryFee,
                    description = "Entry slot checkout for: ${tournament.title}",
                    status = "COMPLETED"
                )
            )

            // Auto-recalculate player statistics to count new tournament participation immediately!
            repository.recalculateAllPlayerStats()
        }
    }

    fun submitDispute(tournamentId: Int, matchId: Int, text: String) {
        val user = currentUser.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            repository.insertDispute(
                Dispute(
                    tournamentId = tournamentId,
                    matchId = matchId,
                    userId = user.id,
                    username = user.username,
                    description = text
                )
            )
        }
    }


    // --- Administrator Dashboard Functions ---

    // Edit settings / Announcement / QR upload
    fun saveAdminSettings() {
        viewModelScope.launch {
            repository.saveAdminSetting(
                AdminSetting(
                    id = 1,
                    qrCodeUpiId = adminUpiId,
                    systemAnnouncement = adminAnnouncementText
                )
            )
        }
    }

    // Create a new tournament with custom rules and auto-initialize empty bracket slots from 8 participants
    fun createTournament() {
        val title = newTourneyTitle.trim()
        val pool = newTourneyPrizePool.toDoubleOrNull() ?: 1000.0
        val fee = newTourneyEntryFee.toDoubleOrNull() ?: 10.0
        val p1 = newTourneyFirstPrize.toDoubleOrNull() ?: 700.0
        val p2 = newTourneySecondPrize.toDoubleOrNull() ?: 300.0
        val rulesTxt = newTourneyRules.trim().ifBlank { "Standard esports battle regulations apply." }

        if (title.isEmpty()) return

        viewModelScope.launch {
            val tourney = Tournament(
                title = title,
                gameMode = newTourneyMode,
                mapName = newTourneyMap,
                entryFee = fee,
                prizePool = pool,
                firstPrize = p1,
                secondPrize = p2,
                scheduleTime = System.currentTimeMillis() + 172800000,
                rules = rulesTxt,
                status = "REGISTRATION_OPEN"
            )

            val newId = repository.insertTournament(tourney)

            // Clear admin fields
            newTourneyTitle = ""
            newTourneyRules = ""
            newTourneyEntryFee = "10"
            newTourneyPrizePool = "1000"
            newTourneyFirstPrize = "700"
            newTourneySecondPrize = "300"
            newTourneyMode = "Solo"
            newTourneyMap = "Bermuda"
        }
    }

    fun deleteTournament(tournament: Tournament) {
        viewModelScope.launch {
            repository.deleteTournament(tournament)
            repository.deleteBracketsForTournament(tournament.id)
            navigateTo(Screen.AdminDashboard)
        }
    }

    fun toggleTournamentVisibility(tournament: Tournament) {
        viewModelScope.launch {
            val updated = tournament.copy(isVisible = !tournament.isVisible)
            repository.updateTournament(updated)
        }
    }

    // Modify a registered participant from the admin table
    fun editParticipant(registrationId: Int, newName: String, newUid: String) {
        viewModelScope.launch {
            val curRegList = selectedTourneyRegistrations.value
            val regToUpdate = curRegList.find { it.id == registrationId }
            if (regToUpdate != null) {
                repository.updateRegistration(
                    regToUpdate.copy(username = newName, freeFireUid = newUid)
                )
            }
            editingRegistrationId = null
        }
    }

    // Decline / Kick / Delete registered participant
    fun deleteParticipant(registration: Registration, tournament: Tournament) {
        viewModelScope.launch {
            repository.deleteRegistration(registration)
            // Adjust count
            val count = maxOf(0, tournament.registeredCount - 1)
            repository.updateTournament(tournament.copy(registeredCount = count))
        }
    }

    // Launch Custom Bracket Generator automatically when tournament begins
    fun startTournamentAndGenerateBrackets(tournament: Tournament) {
        viewModelScope.launch {
            // Update tournament status
            val updated = tournament.copy(status = "ONGOING", currentRound = 1)
            repository.updateTournament(updated)

            // Fetch registrations to populate rounds
            val participants = repository.getRegistrationsForTournament(tournament.id)
            val pNames = participants.map { it.username }.toMutableList()
            val pIds = participants.map { it.userId }.toMutableList()

            // Pads to 8 participants
            while (pNames.size < 8) {
                pNames.add("Bot_Combatant_${UUID.randomUUID().toString().take(4)}")
                pIds.add(-1)
            }

            // Create initial Quarterfinals bracket nodes (Round 1: 4 matches index 0..3)
            val bracketNodes = mutableListOf<BracketNode>()
            for (i in 0..3) {
                val index1 = i * 2
                val index2 = i * 2 + 1
                bracketNodes.add(
                    BracketNode(
                        tournamentId = tournament.id,
                        roundNumber = 1,
                        matchIndex = i,
                        player1Id = if (pIds[index1] == -1) null else pIds[index1],
                        player1Name = pNames[index1],
                        player2Id = if (pIds[index2] == -1) null else pIds[index2],
                        player2Name = pNames[index2],
                        player1Score = 0,
                        player2Score = 0,
                        winnerId = null,
                        winnerName = "",
                        matchStatus = "PENDING"
                    )
                )
            }

            // Round 2 (Semifinals): Match Index 0..1 (Pending)
            bracketNodes.add(
                BracketNode(tournamentId = tournament.id, roundNumber = 2, matchIndex = 0, player1Id = null, player1Name = "TBD (QF0 Winner)", player2Id = null, player2Name = "TBD (QF1 Winner)"))
            bracketNodes.add(
                BracketNode(tournamentId = tournament.id, roundNumber = 2, matchIndex = 1, player1Id = null, player1Name = "TBD (QF2 Winner)", player2Id = null, player2Name = "TBD (QF3 Winner)"))

            // Round 3 (Finals): Match Index 0 (Pending)
            bracketNodes.add(
                BracketNode(tournamentId = tournament.id, roundNumber = 3, matchIndex = 0, player1Id = null, player1Name = "TBD (SF0 Winner)", player2Id = null, player2Name = "TBD (SF1 Winner)"))

            repository.insertBrackets(bracketNodes)
            selectTournament(tournament.id) // Reset selectors cache
        }
    }

    // Set bracket match status and edit/verify scores of brackets
    fun updateMatchScore(bracket: BracketNode, score1: Int, score2: Int, status: String) {
        viewModelScope.launch {
            var finalWinnerId: Int? = null
            var finalWinnerName = ""

            if (status == "VERIFIED") {
                if (score1 > score2) {
                    finalWinnerId = bracket.player1Id
                    finalWinnerName = bracket.player1Name
                } else {
                    finalWinnerId = bracket.player2Id
                    finalWinnerName = bracket.player2Name
                }
            }

            val updatedNode = bracket.copy(
                player1Score = score1,
                player2Score = score2,
                matchStatus = status,
                winnerId = finalWinnerId,
                winnerName = finalWinnerName
            )
            repository.updateBracketNode(updatedNode)

            // If verified, propagate this winner to the next round automatically!
            if (status == "VERIFIED" && finalWinnerName.isNotEmpty()) {
                propagateWinnerToNextRound(bracket.tournamentId, bracket.roundNumber, bracket.matchIndex, finalWinnerId, finalWinnerName)
            }

            // Automatically recalculate player statistics based on the updated match scores!
            repository.recalculateAllPlayerStats()
        }
    }

    private suspend fun propagateWinnerToNextRound(tournamentId: Int, curRound: Int, matchIdx: Int, winnerId: Int?, winnerName: String) {
        val allBrackets = repository.getBracketsForTournament(tournamentId)

        if (curRound == 1) {
            // Quarterfinals winner goes to Semifinals (Round 2)
            // QF 0 and 1 winners play Semifinal 0 (player1 vs player2)
            // QF 2 and 3 winners play Semifinal 1 (player1 vs player2)
            val nextMatchIdx = matchIdx / 2
            val isPlayer1Slot = (matchIdx % 2 == 0)

            val sfMatch = allBrackets.find { it.roundNumber == 2 && it.matchIndex == nextMatchIdx }
            if (sfMatch != null) {
                val updatedSf = if (isPlayer1Slot) {
                    sfMatch.copy(player1Id = winnerId, player1Name = winnerName)
                } else {
                    sfMatch.copy(player2Id = winnerId, player2Name = winnerName)
                }
                repository.updateBracketNode(updatedSf)
            }
        } else if (curRound == 2) {
            // Semifinals winner goes to Finals (Round 3)
            // SF 0 and 1 winners play in Finals 0 (player1 vs player2)
            val isPlayer1Slot = (matchIdx % 2 == 0)

            val mainMatch = allBrackets.find { it.roundNumber == 3 && it.matchIndex == 0 }
            if (mainMatch != null) {
                val updatedF = if (isPlayer1Slot) {
                    mainMatch.copy(player1Id = winnerId, player1Name = winnerName)
                } else {
                    mainMatch.copy(player2Id = winnerId, player2Name = winnerName)
                }
                repository.updateBracketNode(updatedF)
            }
        }
    }

    // Dispute cases handling dashboard
    fun resolveDisputeAndCloseCase(dispute: Dispute, resolution: String) {
        viewModelScope.launch {
            repository.updateDispute(
                dispute.copy(status = resolution, adminAnnouncement = "Dispute Case #${dispute.id} Resolved: $resolution")
            )
        }
    }

    // Approve user requested withdrawal withdrawals payout
    fun approvePayoutRequest(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(
                transaction.copy(status = "COMPLETED", description = transaction.description + " (Sent via Dashboard Instant Pay Out)")
            )
        }
    }

    // Automated Prize Pool Distribution when Tournament Finishes
    fun finalizeTournamentAndDistributePrizes(tournament: Tournament) {
        viewModelScope.launch {
            // Check if final winner is decided
            val allBrackets = repository.getBracketsForTournament(tournament.id)
            val finalMatch = allBrackets.find { it.roundNumber == 3 && it.matchIndex == 0 }

            if (finalMatch == null || finalMatch.matchStatus != "VERIFIED" || finalMatch.winnerName.isBlank()) {
                // Bracket not complete yet
                return@launch
            }

            val winnerName = finalMatch.winnerName
            val winnerId = finalMatch.winnerId

            // Identify second prize player
            val secondName = if (finalMatch.winnerName == finalMatch.player1Name) finalMatch.player2Name else finalMatch.player1Name
            val secondId = if (finalMatch.winnerName == finalMatch.player1Name) finalMatch.player2Id else finalMatch.player1Id

            // 1. Give First prize wallet balance
            if (winnerId != null && winnerId > 0) {
                val winnerUser = repository.getUserById(winnerId)
                if (winnerUser != null) {
                    repository.updateUser(winnerUser.copy(walletBalance = winnerUser.walletBalance + tournament.firstPrize))
                    repository.insertTransaction(
                        Transaction(
                            userId = winnerId,
                            type = "PRIZE_WIN",
                            amount = tournament.firstPrize,
                            description = "1st Place Winner Prize of: ${tournament.title}",
                            status = "COMPLETED"
                        )
                    )
                }
            }

            // 2. Give Second prize wallet balance
            if (secondId != null && secondId > 0) {
                val runnerUser = repository.getUserById(secondId)
                if (runnerUser != null) {
                    repository.updateUser(runnerUser.copy(walletBalance = runnerUser.walletBalance + tournament.secondPrize))
                    repository.insertTransaction(
                        Transaction(
                            userId = secondId,
                            type = "PRIZE_WIN",
                            amount = tournament.secondPrize,
                            description = "2nd Place Runner-Up Prize of: ${tournament.title}",
                            status = "COMPLETED"
                        )
                    )
                }
            }

            // 3. Complete Tournament Status
            repository.updateTournament(tournament.copy(status = "COMPLETED"))
            selectTournament(tournament.id) // Refresh details
        }
    }
}
