package brite.outdoor.data.remote


import brite.outdoor.data.api_entities.response.ResponseRegisterToken
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class ApiController @Inject constructor(private val apiService: CallApiService) : BaseHandleResult() {
    suspend fun loginWithGoogle(requestLogin: HashMap<String,String>) = getResult { apiService.loginWithGoogle(requestLogin) }
    suspend fun loginWithFacebook(requestLogin: HashMap<String,String>) = getResult { apiService.loginWithFacebook(requestLogin) }

    suspend fun logout(requestLogin: HashMap<String,String>) = getResult { apiService.logout(requestLogin) }

    suspend fun fetchListLocation(requestLocation: HashMap<String, String>) = getResult { apiService.fetchListLocation(requestLocation) }
    suspend fun fetchListUtensils(requestUtensils: HashMap<String, String>) = getResult { apiService.fetchListUtensils(requestUtensils) }

    suspend fun fetchPushPost(requestPushPost : HashMap<String,String>,listImage: ArrayList<MultipartBody.Part>  ) = getResult { apiService.fetchPushPost(requestPushPost, listImage) }

    suspend fun searchLocations(requestSearchLocations : HashMap<String,String>) = getResult { apiService.searchLocations(requestSearchLocations) }
    suspend fun searchUtensils(requestSearchUtensils : HashMap<String,String>) = getResult { apiService.searchUtensil(requestSearchUtensils)}
    suspend fun searchUser(requestSearchUser : HashMap<String,String>) = getResult { apiService.searchUser(requestSearchUser) }

    suspend fun postLike(requestLike : HashMap<String,String>) = getResult { apiService.postLikes(requestLike) }

    suspend fun postShares(requestShares : HashMap<String,String>) = getResult { apiService.postShares(requestShares) }

    suspend fun postComment(requestComment : HashMap<String,String>,listImage:ArrayList<MultipartBody.Part>?) = getResult { apiService.postComment(requestComment,listImage) }

    suspend fun registerTokenPush(requestRegisterTokenResult : HashMap<String,String>, userId: String) = getResult { apiService.registerTokenPush(requestRegisterTokenResult,userId) }

    suspend fun getListComment(requestListComment : HashMap<String,String>, postId: String) = getResult { apiService.getListComment(requestListComment,postId) }

    suspend fun getListCommentLevel2(requestListCommentLevel2 : HashMap<String,String>, threadId: String) = getResult { apiService.getListCommentLevel2(requestListCommentLevel2,threadId) }

    suspend fun followUnFollowUser(requestFollow:HashMap<String,String>) = getResult { apiService.followUnFollowUser(requestFollow) }

    suspend fun listPostUser(requestListPostUser:HashMap<String,String>) = getResult { apiService.listPostUser(requestListPostUser) }

    suspend fun registeredBookMart(requestBookMart : HashMap<String,String>) = getResult { apiService.registeredBookMart(requestBookMart) }

    suspend fun detailPost(requestDetailPost:HashMap<String,String>, postId: String) = getResult { apiService.detailPost(requestDetailPost, postId) }

    suspend fun detailUser(requestDetailUser:HashMap<String,String>) = getResult { apiService.detailUser(requestDetailUser) }

    suspend fun listPostUserFollow(requestListPostUserFollow : HashMap<String,String>) = getResult { apiService.listPostUserFollow(requestListPostUserFollow) }

    suspend fun listBookMark(requestListBookMark : HashMap<String,String>) = getResult { apiService.listBookMark(requestListBookMark) }

    suspend fun unBookMark(requestUnBookMark :  HashMap<String,String>) = getResult { apiService.unBookMark(requestUnBookMark) }

    suspend fun listPostFocus(requestListPostFocus : HashMap<String,String>) = getResult { apiService.listPostFocus(requestListPostFocus) }

    suspend fun listPostNew(requestListPostNew : HashMap<String,String>) = getResult { apiService.listPostNew(requestListPostNew) }

    suspend fun report(requestReport:HashMap<String,String>)=getResult { apiService.report(requestReport) }

    suspend fun hidePost(requestHidePost : HashMap<String,String>) = getResult { apiService.hidePost(requestHidePost) }

    suspend fun deletePost(requestDeletePost : HashMap<String,String>,postId:String) = getResult { apiService.deletePost(requestDeletePost,postId) }

    suspend fun createHash(requestCreateHash : HashMap<String,String>) = getResult { apiService.createHashAndSendEmail(requestCreateHash) }

    suspend fun createNewAccount(requestCreateNewAccount : HashMap<String,String>) = getResult { apiService.createNewAccount(requestCreateNewAccount) }

    suspend fun checkCodeHash(requestCheckCodeHash : HashMap<String,String>) = getResult { apiService.checkCodeHash(requestCheckCodeHash) }

    suspend fun mergeAccount(requestMergeAccount : HashMap<String,String>) = getResult { apiService.mergeAccount(requestMergeAccount) }

    suspend fun cancelMergeAccount(requestCancelMergeAccount : HashMap<String,String>) = getResult { apiService.cancelMergeAccount(requestCancelMergeAccount) }

    suspend fun listPostLocation(requestListPostLocation : HashMap<String,String>,locationId:String) = getResult { apiService.listPostLocation(requestListPostLocation,locationId) }

    suspend fun listPostUtensils(requestListPostUtensils : HashMap<String,String>,utensilId : String) =getResult { apiService.listPostUtensils(requestListPostUtensils,utensilId) }

    suspend fun deleteComment(deleteComment : HashMap<String,String>,id_comment:String) = getResult { apiService.deleteComment(deleteComment,id_comment) }

    suspend fun listFollow(listFollow : HashMap<String,String>) = getResult { apiService.listFollow(listFollow) }

    suspend fun listNotification(requestListNotification:HashMap<String,String>) = getResult { apiService.listNotification(requestListNotification) }

    suspend fun listFollower(listFollower : HashMap<String,String>) = getResult { apiService.listFollower(listFollower) }

    suspend fun editPost(editPost : HashMap<String,String>,listImage: ArrayList<MultipartBody.Part>, id: String) = getResult { apiService.editPost(editPost,listImage, id) }

    suspend fun notifyUnread(requestNotifyUnread:HashMap<String,String>) = getResult { apiService.notifyUnread(requestNotifyUnread) }
}