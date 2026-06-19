package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.User
import com.example.data.model.Tournament
import com.example.data.model.Registration
import com.example.data.model.BracketNode
import com.example.data.model.Dispute
import com.example.data.model.Transaction
import com.example.data.model.AdminSetting
import com.example.data.model.PlayerStats

@Database(
    entities = [
        User::class,
        Tournament::class,
        Registration::class,
        BracketNode::class,
        Dispute::class,
        Transaction::class,
        AdminSetting::class,
        PlayerStats::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tournamentDao(): TournamentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "freefire_tournaments_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
