package com.ugrcaan.qriosity.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SavedLinkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QriosityDatabase : RoomDatabase() {

    abstract fun savedLinkDao(): SavedLinkDao

    companion object {
        @Volatile
        private var INSTANCE: QriosityDatabase? = null

        fun getInstance(context: Context): QriosityDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    QriosityDatabase::class.java,
                    "qriosity-db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
