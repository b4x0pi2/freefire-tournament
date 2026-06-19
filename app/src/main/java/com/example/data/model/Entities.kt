package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val mobileNumber: String = "",
    val isVerified: Boolean = false,
    val welcomeBonusClaimed: Boolean = false,
    val walletBalance: Double = 0.0,
    val freeFireId: String = "",
    val username: String = "",
    val joinedDate: Long = System.currentTimeMillis(),
    val isAdmin: Boolean = false,
    val firebaseUid: String? = null
)

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val gameMode: String, // "Squad", "Solo", "Duo"
    val mapName: String,  // "Bermuda", "Purgatory", "Kalahari", "Alpine"
    val entryFee: Double,
    val prizePool: Double,
    val firstPrize: Double,
    val secondPrize: Double,
    val scheduleTime: Long,
    val rules: String,
    val status: String,    // "REGISTRATION_OPEN", "ONGOING", "COMPLETED"
    val maxTeams: Int = 8,
    val registeredCount: Int = 0,
    val currentRound: Int = 1,
    val isVisible: Boolean = true
)

@Entity(tableName = "registrations")
data class Registration(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tournamentId: Int,
    val userId: Int,
    val username: String,
    val freeFireUid: String,
    val contactPhone: String,
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "REGISTERED" // "REGISTERED", "DISQUALIFIED", "PENDING_VERIFICATION"
)

@Entity(tableName = "brackets")
data class BracketNode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tournamentId: Int,
    val roundNumber: Int,  // 1 = QF (Round of 8), 2 = SF (Round of 4), 3 = Final (Round of 2)
    val matchIndex: Int,
    val player1Id: Int?,
    val player1Name: String,
    val player2Id: Int?,
    val player2Name: String,
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val winnerId: Int? = null,
    val winnerName: String = "",
    val matchStatus: String = "PENDING" // "PENDING", "LIVE", "VERIFIED"
)

@Entity(tableName = "disputes")
data class Dispute(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tournamentId: Int,
    val matchId: Int,
    val userId: Int,
    val username: String,
    val description: String,
    val status: String = "PENDING", // "PENDING", "RESOLVED_PLAYER1", "RESOLVED_PLAYER2", "DISMISSED"
    val adminAnnouncement: String = "",
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val type: String, // "WELCOME_BONUS", "ENTRY_FEE", "PRIZE_WIN", "DEPOSIT", "WITHDRAWAL"
    val amount: Double,
    val description: String,
    val status: String = "COMPLETED", // "PENDING", "COMPLETED"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_settings")
data class AdminSetting(
    @PrimaryKey val id: Int = 1,
    val qrCodeUpiId: String = "esportspay@upi",
    val systemAnnouncement: String = "🔥 Welcome to Free Fire Esports Tournaments! Instant withdrawals active. 🔥"
)

@Entity(tableName = "player_stats")
data class PlayerStats(
    @PrimaryKey val userId: Int,
    val username: String,
    val freeFireId: String,
    val matchesPlayed: Int = 0,
    val matchesWon: Int = 0,
    val totalKills: Int = 0,
    val totalDeaths: Int = 0,
    val tournamentsPlayed: Int = 0,
    val tournamentsWon: Int = 0,
    val winRate: Float = 0f,
    val kdRatio: Float = 0f,
    val scorePoints: Int = 0
)
