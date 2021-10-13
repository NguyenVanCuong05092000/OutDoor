package brite.outdoor.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import brite.outdoor.data.api_entities.response.ResponseListLocation
import brite.outdoor.data.api_entities.response.ResponseListUtensils
import brite.outdoor.data.api_entities.response.ResponsePushPosts
import brite.outdoor.data.entities.PostContentEntity
import brite.outdoor.data.entities.SelectPlace
import brite.outdoor.data.entities.SelectUtensils

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPushPosts(info: ResponsePushPosts.PushPostsResponse)

    @Query("SELECT  * FROM pushPostsResponse WHERE id= :id")
    suspend fun getPushPosts(id: String): ResponsePushPosts.PushPostsResponse?

//    @Query("UPDATE pushPostsResponse SET listConTent = :list WHERE id = :id")
//    suspend fun updatePushPosts(list: ArrayList<PostContentEntity>, id: String): Int

    @Query("DELETE FROM pushPostsResponse WHERE id = :id")
    suspend fun deletePushPost(id: String)

    @Query(" SELECT EXISTS(SELECT * from pushPostsResponse WHERE id= :id)")
    suspend fun isCheckId( id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListLocation(info: ArrayList<ResponseListLocation.LocationData>)

    @Query("SELECT  * FROM location")
    fun getListLocation(): LiveData<List<ResponseListLocation.LocationData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListUtensils(info: ArrayList<ResponseListUtensils.UtensilsData>)

    @Query("SELECT  * FROM utensils")
    fun getListUtensils(): LiveData<List<ResponseListUtensils.UtensilsData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectPlace(info: SelectPlace)

    @Query("SELECT  * FROM selectPlace WHERE id= :id")
    suspend fun getSelectPlace(id: String): SelectPlace?

    @Query("DELETE FROM selectPlace WHERE id = :id")
    suspend fun deleteSelectPlace(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSelectUtensils(info: SelectUtensils)

    @Query("SELECT  * FROM selectUtensils WHERE id= :id")
    suspend fun getSelectUtensils(id: String): SelectUtensils?

    @Query("DELETE FROM selectUtensils WHERE id = :id")
    suspend fun deleteSelectUtensils(id: String)
}