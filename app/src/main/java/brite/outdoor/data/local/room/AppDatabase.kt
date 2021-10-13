package brite.outdoor.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseListUtensils
import brite.outdoor.data.api_entities.response.ResponseLogin
import brite.outdoor.data.api_entities.response.ResponsePushPosts
import brite.outdoor.data.entities.PostContentEntity
import brite.outdoor.data.entities.SelectPlace
import brite.outdoor.data.entities.SelectUtensils

@Database(entities = [ResponsePushPosts.PushPostsResponse::class,ResponseListLocation.LocationData::class,ResponseListUtensils.UtensilsData::class,SelectPlace::class,SelectUtensils::class], version = 3, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "DataBs")
                .fallbackToDestructiveMigration()
                .build()
    }
}