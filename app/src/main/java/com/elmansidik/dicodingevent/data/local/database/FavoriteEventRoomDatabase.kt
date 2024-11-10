package com.elmansidik.dicodingevent.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEvent::class], version = 1, exportSchema = false)
abstract class FavoriteEventDatabase : RoomDatabase() {
    abstract fun getFavoriteEventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var instance: FavoriteEventDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): FavoriteEventDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteEventDatabase::class.java, "favorite_event_database"
                ).build().also { instance = it }
            }
        }
    }
}
