package com.example.data.database

import androidx.room.*
import com.example.data.model.User
import com.example.data.model.Tournament
import com.example.data.model.Registration
import com.example.data.model.BracketNode
import com.example.data.model.Dispute
import com.example.data.model.Transaction
import com.example.data.model.AdminSetting
import com.example.data.model.PlayerStats
import kotlinx.coroutines.flow.Flow

@Dao
interface TournamentDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE firebaseUid = :uid LIMIT 1")
    suspend fun getUserByFirebaseUid(uid: String): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: Int): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)


    // --- Tournaments ---
    @Query("SELECT * FROM tournaments ORDER BY scheduleTime DESC")
    fun getAllTournamentsFlow(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    suspend fun getTournamentById(id: Int): Tournament?

    @Query("SELECT * FROM tournaments WHERE id = :id LIMIT 1")
    fun getTournamentByIdFlow(id: Int): Flow<Tournament?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament): Long

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Delete
    suspend fun deleteTournament(tournament: Tournament)


    // --- Registrations ---
    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId")
    fun getRegistrationsForTournamentFlow(tournamentId: Int): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId")
    suspend fun getRegistrationsForTournament(tournamentId: Int): List<Registration>

    @Query("SELECT * FROM registrations WHERE userId = :userId")
    fun getRegistrationsForUserFlow(userId: Int): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId AND userId = :userId LIMIT 1")
    suspend fun getRegistrationForUser(tournamentId: Int, userId: Int): Registration?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: Registration): Long

    @Update
    suspend fun updateRegistration(registration: Registration)

    @Query("DELETE FROM registrations WHERE id = :id")
    suspend fun deleteRegistrationById(id: Int)

    @Delete
    suspend fun deleteRegistration(registration: Registration)


    // --- Brackets ---
    @Query("SELECT * FROM brackets WHERE tournamentId = :tournamentId ORDER BY roundNumber ASC, matchIndex ASC")
    fun getBracketsForTournamentFlow(tournamentId: Int): Flow<List<BracketNode>>

    @Query("SELECT * FROM brackets WHERE tournamentId = :tournamentId ORDER BY roundNumber ASC, matchIndex ASC")
    suspend fun getBracketsForTournament(tournamentId: Int): List<BracketNode>

    @Query("SELECT * FROM brackets WHERE id = :id LIMIT 1")
    suspend fun getBracketNodeById(id: Int): BracketNode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrackets(brackets: List<BracketNode>)

    @Update
    suspend fun updateBracketNode(bracketNode: BracketNode)

    @Query("DELETE FROM brackets WHERE tournamentId = :tournamentId")
    suspend fun deleteBracketsForTournament(tournamentId: Int)


    // --- Disputes ---
    @Query("SELECT * FROM disputes ORDER BY submittedAt DESC")
    fun getAllDisputesFlow(): Flow<List<Dispute>>

    @Query("SELECT * FROM disputes WHERE tournamentId = :tournamentId")
    fun getDisputesForTournamentFlow(tournamentId: Int): Flow<List<Dispute>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: Dispute): Long

    @Update
    suspend fun updateDispute(dispute: Dispute)


    // --- Transactions ---
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsForUserFlow(userId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)


    // --- Admin Settings ---
    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    fun getAdminSettingFlow(): Flow<AdminSetting?>

    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    suspend fun getAdminSetting(): AdminSetting?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdminSetting(setting: AdminSetting)

    // --- Player Stats ---
    @Query("SELECT * FROM player_stats WHERE userId = :userId LIMIT 1")
    fun getPlayerStatsFlow(userId: Int): Flow<PlayerStats?>

    @Query("SELECT * FROM player_stats WHERE userId = :userId LIMIT 1")
    suspend fun getPlayerStats(userId: Int): PlayerStats?

    @Query("SELECT * FROM player_stats ORDER BY scorePoints DESC, totalKills DESC")
    fun getAllPlayerStatsFlow(): Flow<List<PlayerStats>>

    @Query("SELECT * FROM player_stats ORDER BY scorePoints DESC, totalKills DESC")
    suspend fun getAllPlayerStatsSync(): List<PlayerStats>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerStats(stats: PlayerStats)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerStatsList(statsList: List<PlayerStats>)
}
