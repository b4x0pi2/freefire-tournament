package com.example.data.repository

import com.example.data.database.TournamentDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class TournamentRepository(val dao: TournamentDao) {

    // --- Users ---
    suspend fun getUserByEmail(email: String): User? = dao.getUserByEmail(email)
    suspend fun getUserByFirebaseUid(uid: String): User? = dao.getUserByFirebaseUid(uid)
    fun getUserByIdFlow(userId: Int): Flow<User?> = dao.getUserByIdFlow(userId)
    suspend fun getUserById(userId: Int): User? = dao.getUserById(userId)
    fun getAllUsersFlow(): Flow<List<User>> = dao.getAllUsersFlow()
    suspend fun insertUser(user: User): Long = dao.insertUser(user)
    suspend fun updateUser(user: User) = dao.updateUser(user)
    suspend fun deleteUser(user: User) = dao.deleteUser(user)

    // --- Tournaments ---
    val allTournaments: Flow<List<Tournament>> = dao.getAllTournamentsFlow()
    fun getTournamentByIdFlow(id: Int): Flow<Tournament?> = dao.getTournamentByIdFlow(id)
    suspend fun getTournamentById(id: Int): Tournament? = dao.getTournamentById(id)
    suspend fun insertTournament(tournament: Tournament): Long = dao.insertTournament(tournament)
    suspend fun updateTournament(tournament: Tournament) = dao.updateTournament(tournament)
    suspend fun deleteTournament(tournament: Tournament) = dao.deleteTournament(tournament)

    // --- Registrations ---
    fun getRegistrationsForTournamentFlow(tournamentId: Int): Flow<List<Registration>> =
        dao.getRegistrationsForTournamentFlow(tournamentId)

    suspend fun getRegistrationsForTournament(tournamentId: Int): List<Registration> =
        dao.getRegistrationsForTournament(tournamentId)

    fun getRegistrationsForUserFlow(userId: Int): Flow<List<Registration>> =
        dao.getRegistrationsForUserFlow(userId)

    suspend fun getRegistrationForUser(tournamentId: Int, userId: Int): Registration? =
        dao.getRegistrationForUser(tournamentId, userId)

    suspend fun insertRegistration(registration: Registration): Long =
        dao.insertRegistration(registration)

    suspend fun updateRegistration(registration: Registration) = dao.updateRegistration(registration)
    suspend fun deleteRegistrationById(id: Int) = dao.deleteRegistrationById(id)
    suspend fun deleteRegistration(registration: Registration) = dao.deleteRegistration(registration)

    // --- Brackets ---
    fun getBracketsForTournamentFlow(tournamentId: Int): Flow<List<BracketNode>> =
        dao.getBracketsForTournamentFlow(tournamentId)

    suspend fun getBracketsForTournament(tournamentId: Int): List<BracketNode> =
        dao.getBracketsForTournament(tournamentId)

    suspend fun getBracketNodeById(id: Int): BracketNode? = dao.getBracketNodeById(id)
    suspend fun insertBrackets(brackets: List<BracketNode>) = dao.insertBrackets(brackets)
    suspend fun updateBracketNode(bracketNode: BracketNode) = dao.updateBracketNode(bracketNode)
    suspend fun deleteBracketsForTournament(tournamentId: Int) = dao.deleteBracketsForTournament(tournamentId)

    // --- Disputes ---
    val allDisputes: Flow<List<Dispute>> = dao.getAllDisputesFlow()
    fun getDisputesForTournamentFlow(tournamentId: Int): Flow<List<Dispute>> =
        dao.getDisputesForTournamentFlow(tournamentId)

    suspend fun insertDispute(dispute: Dispute): Long = dao.insertDispute(dispute)
    suspend fun updateDispute(dispute: Dispute) = dao.updateDispute(dispute)

    // --- Transactions ---
    fun getTransactionsForUserFlow(userId: Int): Flow<List<Transaction>> =
        dao.getTransactionsForUserFlow(userId)

    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactionsFlow()
    suspend fun insertTransaction(transaction: Transaction): Long = dao.insertTransaction(transaction)
    suspend fun updateTransaction(transaction: Transaction) = dao.updateTransaction(transaction)

    // --- Admin Settings ---
    val adminSetting: Flow<AdminSetting?> = dao.getAdminSettingFlow()
    suspend fun getAdminSetting(): AdminSetting? = dao.getAdminSetting()
    suspend fun saveAdminSetting(setting: AdminSetting) = dao.insertAdminSetting(setting)

    // --- Database Seeding ---
    suspend fun prepDatabaseWithInitialDataIfNeeded() {
        // Clean up old admin user if present to ensure they cannot log in
        val oldAdmin = dao.getUserByEmail("admin@fft.com")
        if (oldAdmin != null) {
            dao.deleteUser(oldAdmin)
        }

        val checkAdmin = dao.getUserByEmail("parraybabar04@gmail.com")
        if (checkAdmin == null) {
            // Seed Admin User
            val adminId = dao.insertUser(
                User(
                    email = "parraybabar04@gmail.com",
                    passwordHash = "babar@123",
                    mobileNumber = "9999988888",
                    isVerified = true,
                    welcomeBonusClaimed = true,
                    walletBalance = 99999.0,
                    freeFireId = "ADMIN_ID",
                    username = "Tournament Director",
                    isAdmin = true
                )
            )

            // Seed Admin Settings
            dao.insertAdminSetting(
                AdminSetting(
                    id = 1,
                    qrCodeUpiId = "officialpay@ybl",
                    systemAnnouncement = "🏆 Sunday Clash Royale Qualifier Starts at 3 PM! Enter custom room ID in tournament details! 🏆"
                )
            )

            // Seed standard Players
            val players = listOf(
                User(email = "p1@fft.com", passwordHash = "p123", mobileNumber = "8888877777", isVerified = true, welcomeBonusClaimed = true, walletBalance = 550.0, freeFireId = "68219403", username = "Raistar_YT"),
                User(email = "p2@fft.com", passwordHash = "p123", mobileNumber = "7777766666", isVerified = true, welcomeBonusClaimed = true, walletBalance = 420.0, freeFireId = "48102941", username = "Badge99"),
                User(email = "p3@fft.com", passwordHash = "p123", mobileNumber = "6666655555", isVerified = true, welcomeBonusClaimed = true, walletBalance = 100.0, freeFireId = "71038491", username = "Vasiy_Boss"),
                User(email = "p4@fft.com", passwordHash = "p123", mobileNumber = "9999911111", isVerified = true, welcomeBonusClaimed = true, walletBalance = 50.0,  freeFireId = "81940128", username = "TSG_Ritika"),
                User(email = "p5@fft.com", passwordHash = "p123", mobileNumber = "9876543210", isVerified = true, welcomeBonusClaimed = true, walletBalance = 15.0,  freeFireId = "50912480", username = "Ajjubhai94"),
                User(email = "p6@fft.com", passwordHash = "p123", mobileNumber = "8765432109", isVerified = true, welcomeBonusClaimed = true, walletBalance = 80.0,  freeFireId = "55124901", username = "Killer_Clown"),
                User(email = "p7@fft.com", passwordHash = "p123", mobileNumber = "7654321098", isVerified = true, welcomeBonusClaimed = true, walletBalance = 24.0,  freeFireId = "30284910", username = "Ritik_FF"),
                User(email = "p8@fft.com", passwordHash = "p123", mobileNumber = "6543210987", isVerified = true, welcomeBonusClaimed = true, walletBalance = 12.0,  freeFireId = "91028410", username = "Skylord_OP")
            )
            val playerIds = mutableListOf<Long>()
            for (p in players) {
                playerIds.add(dao.insertUser(p))
            }

            // No default tournaments seeded. Only tournaments created/uploaded by the Organizer/Admin will be visible.
        }
    }

    // --- Player Stats & Leaderboard ---
    val allPlayerStats: Flow<List<PlayerStats>> = dao.getAllPlayerStatsFlow()
    
    fun getPlayerStatsFlow(userId: Int): Flow<PlayerStats?> = dao.getPlayerStatsFlow(userId)
    
    suspend fun getPlayerStats(userId: Int): PlayerStats? = dao.getPlayerStats(userId)

    suspend fun recalculateAllPlayerStats() {
        // 1. Get all users
        val users = dao.getAllUsersFlow().first()
        
        // 2. Get all tournaments
        val tournaments = dao.getAllTournamentsFlow().first()
        
        // 3. Collect all bracket nodes across all tournaments
        val allBrackets = mutableListOf<BracketNode>()
        for (t in tournaments) {
            allBrackets.addAll(dao.getBracketsForTournament(t.id))
        }
        
        // 4. Collect all registrations across all tournaments
        val allRegistrations = mutableListOf<Registration>()
        for (t in tournaments) {
            allRegistrations.addAll(dao.getRegistrationsForTournament(t.id))
        }
        
        // 5. For each user, compute stats
        val statsList = users.filter { !it.isAdmin }.map { user ->
            // filter registrations for this user
            val userRegs = allRegistrations.filter { it.userId == user.id && it.status == "REGISTERED" }
            val tournamentsPlayed = userRegs.size
            
            // find verified matches played by this user
            val userMatches = allBrackets.filter { 
                it.matchStatus == "VERIFIED" && (it.player1Id == user.id || it.player2Id == user.id)
            }
            val matchesPlayed = userMatches.size
            
            // find matches won by this user
            val userMatchesWon = userMatches.filter { it.winnerId == user.id }
            val matchesWon = userMatchesWon.size
            
            // total kills (kills = score in the matches)
            var totalKills = 0
            userMatches.forEach { match ->
                if (match.player1Id == user.id) {
                    totalKills += match.player1Score
                } else if (match.player2Id == user.id) {
                    totalKills += match.player2Score
                }
            }
            
            // total deaths (if they played a verified match and lost, they died)
            val totalDeaths = userMatches.filter { it.winnerId != user.id }.size
            
            // tournaments won (if they won the Round 3 match (Finals) of any tournament)
            val tournamentsWon = allBrackets.filter {
                it.roundNumber == 3 && it.matchStatus == "VERIFIED" && it.winnerId == user.id
            }.size
            
            val winRate = if (matchesPlayed > 0) {
                (matchesWon.toFloat() / matchesPlayed.toFloat()) * 100f
            } else 0f
            
            val kdRatio = if (totalDeaths > 0) {
                totalKills.toFloat() / totalDeaths.toFloat()
            } else totalKills.toFloat() // If no deaths, KD is total kills
            
            // Calculate Leaderboard Score Points
            // 1 Tournament Win = 500 points
            // 1 Match Win = 100 points
            // 1 Kill = 10 points
            val scorePoints = (tournamentsWon * 500) + (matchesWon * 100) + (totalKills * 10)
            
            PlayerStats(
                userId = user.id,
                username = user.username.ifBlank { user.email.substringBefore("@") },
                freeFireId = user.freeFireId,
                matchesPlayed = matchesPlayed,
                matchesWon = matchesWon,
                totalKills = totalKills,
                totalDeaths = totalDeaths,
                tournamentsPlayed = tournamentsPlayed,
                tournamentsWon = tournamentsWon,
                winRate = winRate,
                kdRatio = kdRatio,
                scorePoints = scorePoints
            )
        }
        
        // 6. Save stats to database in batch
        dao.insertPlayerStatsList(statsList)
    }
}
