package brite.outdoor.data.repository

import androidx.annotation.WorkerThread
import brite.outdoor.data.api_entities.response.ResponsePushPosts
import brite.outdoor.data.entities.SelectPlace
import brite.outdoor.data.entities.SelectUtensils
import brite.outdoor.data.local.room.AppDao
import com.google.gson.Gson
import javax.inject.Inject

class AppRepository @Inject constructor(private val localDataSource: AppDao
) {
    @WorkerThread
    suspend fun insertPost(info: ResponsePushPosts.PushPostsResponse) = localDataSource.insertPushPosts(info)

    @WorkerThread
    suspend fun deletePost(id: String) = localDataSource.deletePushPost(id)

    @WorkerThread
    suspend fun getPushPosts(id: String) = localDataSource.getPushPosts(id)


    @WorkerThread
    suspend fun insertSelectPlace(info: SelectPlace) = localDataSource.insertSelectPlace(info)

    @WorkerThread
    suspend fun deleteSelectPlace(id: String) = localDataSource.deleteSelectPlace(id)

    @WorkerThread
    suspend fun getSelectPlace(id: String) = localDataSource.getSelectPlace(id)

    @WorkerThread
    suspend fun insertSelectUtensils(info: SelectUtensils) = localDataSource.insertSelectUtensils(info)

    @WorkerThread
    suspend fun deleteSelectUtensils(id: String) = localDataSource.deleteSelectUtensils(id)

    @WorkerThread
    suspend fun getSelectUtensils(id: String) = localDataSource.getSelectUtensils(id)

}