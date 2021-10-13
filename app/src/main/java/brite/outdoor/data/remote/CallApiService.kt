package brite.outdoor.data.remote

import brite.outdoor.constants.ApiConst.Companion.API_BOOKMARK
import brite.outdoor.constants.ApiConst.Companion.API_CANCEL_MERGE_ACCOUNT
import brite.outdoor.constants.ApiConst.Companion.API_CANCEL_MERGE_TO_CREATE_NEW_ACCOUNT
import brite.outdoor.constants.ApiConst.Companion.API_CHECK_CODE_HASH
import brite.outdoor.constants.ApiConst.Companion.API_CREATE_HASH
import brite.outdoor.constants.ApiConst.Companion.API_DELETE_POST
import brite.outdoor.constants.ApiConst.Companion.API_DETAIL
import brite.outdoor.constants.ApiConst.Companion.API_DETAIL_POST
import brite.outdoor.constants.ApiConst.Companion.API_EDIT_POST

import brite.outdoor.constants.ApiConst.Companion.API_FOLLOW_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_HIDE_POST
import brite.outdoor.constants.ApiConst.Companion.API_LIST_BOOK_MARK

import brite.outdoor.constants.ApiConst.Companion.API_LIST_COMMENT
import brite.outdoor.constants.ApiConst.Companion.API_LIST_COMMENT_LEVEL_2
import brite.outdoor.constants.ApiConst.Companion.API_LIST_DELETE_COMMENT
import brite.outdoor.constants.ApiConst.Companion.API_LIST_FOLLOW
import brite.outdoor.constants.ApiConst.Companion.API_LIST_FOLLOWER

import brite.outdoor.constants.ApiConst.Companion.API_LIST_LOCATION_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_LIST_NOTIFICATION
import brite.outdoor.constants.ApiConst.Companion.API_LIST_POST_HOT
import brite.outdoor.constants.ApiConst.Companion.API_LIST_POST_LOCATION
import brite.outdoor.constants.ApiConst.Companion.API_LIST_POST_NEW
import brite.outdoor.constants.ApiConst.Companion.API_LIST_POST_USER_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_LIST_POST_USER_FOLLOW
import brite.outdoor.constants.ApiConst.Companion.API_LIST_POST_UTENSILS
import brite.outdoor.constants.ApiConst.Companion.API_LIST_SEARCH_USER
import brite.outdoor.constants.ApiConst.Companion.API_LIST_UTENSILS_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_LOGIN_FB_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_LOGIN_GG_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_LOGOUT_END_POINT
import brite.outdoor.constants.ApiConst.Companion.API_MERGE_ACCOUNT
import brite.outdoor.constants.ApiConst.Companion.API_NOTIFICATION_UNREAD
import brite.outdoor.constants.ApiConst.Companion.API_POST_COMMENT
import brite.outdoor.constants.ApiConst.Companion.API_POST_LIKES
import brite.outdoor.constants.ApiConst.Companion.API_POST_SHARES
import brite.outdoor.constants.ApiConst.Companion.API_PUSH_POST
import brite.outdoor.constants.ApiConst.Companion.API_REGISTER_TOKEN_PUSH
import brite.outdoor.constants.ApiConst.Companion.API_REPORT
import brite.outdoor.constants.ApiConst.Companion.API_SEARCH_LOCATIONS
import brite.outdoor.constants.ApiConst.Companion.API_SEARCH_UTENSILS
import brite.outdoor.constants.ApiConst.Companion.API_UNBOOKMARK
import brite.outdoor.data.api_entities.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface CallApiService {
    /**
        @FormUrlEncoded : Use with @Field - @FieldMap
        form-urlencoded: POST
        query parameter: GET
        @Body -> Json

        You use application/x-www-form-urlencoded by using annotation @FormUrlEncoded in order to send image you have to transform the whole file into text (e.g. base64).
        A better approach would be use multipart/form-data use @Multipart -- @Part - @PartMap
     * */

    @POST(API_LOGIN_GG_END_POINT)
    @Multipart
    suspend fun loginWithGoogle(
        @PartMap requestLogin: Map<String, String>
    ): Response<ResponseLogin.LoginResult>


    @POST(API_LOGIN_FB_END_POINT)
    @Multipart
    suspend fun loginWithFacebook(
        @PartMap requestLogin: Map<String, String>
    ): Response<ResponseLogin.LoginResult>


    @POST(API_LOGOUT_END_POINT)
    @FormUrlEncoded
    suspend fun logout(
        @FieldMap requestLogout: Map<String, String>
    ): Response<ResponseLogout.LogoutResult>

    @POST(API_LIST_LOCATION_END_POINT)
    @FormUrlEncoded
    suspend fun fetchListLocation(
            @FieldMap requestLocation: Map<String,String>
    ):Response<ResponseListLocation.LocationResult>

    @POST(API_LIST_UTENSILS_END_POINT)
    @FormUrlEncoded
    suspend fun fetchListUtensils(
            @FieldMap requestUtensils : Map<String,String>
    ):Response<ResponseListUtensils.UtensilsResult>

    @Multipart
    @POST(API_PUSH_POST)
    suspend fun fetchPushPost(
            @PartMap requestPushPost:Map<String,String>, @Part listImage: ArrayList<MultipartBody.Part> ):Response<ResponsePushPosts.PushPostResult>

    @POST(API_SEARCH_LOCATIONS)
    @Multipart
    suspend fun searchLocations(
            @PartMap requestSearchLocations : Map<String,String>
    ):Response<ResponseSearchLocations.SearchLocationsResult>

    @POST(API_SEARCH_UTENSILS)
    @Multipart
    suspend fun searchUtensil(
            @PartMap requestSearchUtensils : Map<String,String>
    ):Response<ResponseSearchUtensils.SearchUtensilsResult>

    @POST(API_LIST_SEARCH_USER)
    @Multipart
    suspend fun searchUser(
        @PartMap requestSearchUser : Map<String,String>
    ):Response<ResponseSearchUser.SearchUserResult>

    @POST(API_POST_LIKES)
    @Multipart
    suspend fun postLikes(
        @PartMap requestLike : Map<String,String>
    ):Response<ResponseLike.LikesResult>

    @POST(API_POST_SHARES)
    @Multipart
    suspend fun postShares(
        @PartMap requestShares : Map<String,String>
    ):Response<ResponseShares.SharesResult>

    @POST(API_POST_COMMENT)
    @Multipart
    suspend fun postComment(
        @PartMap requestShares : Map<String,String>,
        @Part listImage: ArrayList<MultipartBody.Part>?
    ):Response<ResponsePostComment.PostCommentResult>

    @POST("$API_REGISTER_TOKEN_PUSH{user_id}")
    @Multipart
    suspend fun registerTokenPush(
        @PartMap requestRegisterToken : Map<String,String>, @Path("user_id") user_id: String
    ):Response<ResponseRegisterToken.RegisterTokenResult>

    @POST("$API_DETAIL_POST{id_posts}")
    @Multipart
    suspend fun detailPost(
        @PartMap requestDetailPost : Map<String,String>, @Path("id_posts") id_posts: String
    ):Response<ResponseDetailPost.DetailPostResult>

    @POST(API_FOLLOW_END_POINT)
    @Multipart
    suspend fun followUnFollowUser(
            @PartMap requestFollow : Map<String,String>
    ):Response<ResponseFollow.FollowResult>

    @POST(API_LIST_POST_USER_END_POINT)
    @FormUrlEncoded
    suspend fun listPostUser(
            @FieldMap requestListPostUser : Map<String,String>
    ):Response<ResponseListPostUser.ListPostUserResult>


    @POST("$API_LIST_COMMENT{post_id}")
    @Multipart
    suspend fun getListComment(
        @PartMap requestRegisterToken : Map<String,String>, @Path("post_id") post_id: String
    ):Response<ResponseListComment.ListCommentResult>

    @POST("$API_LIST_COMMENT_LEVEL_2{thread_id}")
    @Multipart
    suspend fun getListCommentLevel2(
        @PartMap listCommentLevel2 : Map<String,String>, @Path("thread_id") thread_id: String
    ):Response<ResponseListCommentLevel2.ListCommentLevel2Result>

    @POST(API_BOOKMARK)
    @FormUrlEncoded
    suspend fun registeredBookMart(
            @FieldMap requestBookMart : Map<String,String>
    ):Response<ResponseBookMart.BookMartResult>

    @POST(API_DETAIL)
    @Multipart
    suspend fun detailUser(
            @PartMap requestDetailUser : Map<String,String>
    ):Response<ResponseDetail.DetailResult>

    @POST(API_LIST_POST_USER_FOLLOW)
    @Multipart
    suspend fun listPostUserFollow(
            @PartMap requestListPostUserFollow : Map<String,String>
    ) : Response<ResponseListPostUser.ListPostUserResult>

    @POST(API_LIST_BOOK_MARK)
    @Multipart
    suspend fun listBookMark(
            @PartMap requestListBookMark : Map<String,String>
    ): Response<ResponseListBookMark.ListBookMarkResult>

    @POST(API_UNBOOKMARK)
    @FormUrlEncoded
    suspend fun unBookMark(
            @FieldMap requestUnBookMark : Map<String,String>
    ):Response<ResponseUnBookMark.UnBookMarkResult>

    @POST(API_LIST_POST_HOT)
    @FormUrlEncoded
    suspend fun listPostFocus(
            @FieldMap requestListPostFocus : Map<String,String>
    ):Response<ResponseListPostUser.ListPostUserResult>

    @POST(API_LIST_POST_NEW)
    @FormUrlEncoded
    suspend fun listPostNew(
            @FieldMap requestListPostNew : Map<String,String>
    ):Response<ResponseListPostUser.ListPostUserResult>

    @POST(API_REPORT)
    @FormUrlEncoded
    suspend fun report(
            @FieldMap requestReport:Map<String,String>
    ):Response<ResponseReport.ReportResult>

    @POST(API_HIDE_POST)
    @FormUrlEncoded
    suspend fun hidePost(
            @FieldMap requestHidePost : Map<String,String>
    ):Response<ResponseHidePost.HidePostResult>

    @POST("$API_DELETE_POST{id_posts}")
    @Multipart
    suspend fun deletePost(
            @PartMap requestDeletePost : Map<String,String>,@Path("id_posts") post_id:String
    ):Response<ResponseDetailPost.DetailPostResult>

    @POST(API_CREATE_HASH)
    @FormUrlEncoded
    suspend fun createHashAndSendEmail(
            @FieldMap requestCreateHash : Map<String,String>
    ):Response<ResponseCreateHash.CreateHashResult>

    @POST(API_CANCEL_MERGE_TO_CREATE_NEW_ACCOUNT)
    @FormUrlEncoded
    suspend fun createNewAccount(
            @FieldMap requestCreateNewAccount : Map<String,String>
    ):Response<ResponseCreateNewAccount.CreateNewAccountResult>

    @POST(API_CHECK_CODE_HASH)
    @FormUrlEncoded
    suspend fun checkCodeHash(
            @FieldMap requestCheckCodeHash : Map<String,String>
    ):Response<ResponseCheckCodeHash.CheckCodeHashResult>

    @POST(API_MERGE_ACCOUNT)
    @FormUrlEncoded
    suspend fun mergeAccount(
            @FieldMap requestMergeAccount : Map<String,String>
    ):Response<ResponseMergeAccount.MergerAccountResult>

    @POST(API_CANCEL_MERGE_ACCOUNT)
    @FormUrlEncoded
    suspend fun cancelMergeAccount(
            @FieldMap requestCancelMergeAccount : Map<String,String>
    ): Response<ResponseCancelMergeAccount.CancelMergeAccountResult>

    @POST("$API_LIST_POST_LOCATION{location_id}")
    @Multipart
    suspend fun listPostLocation(
            @PartMap requestListPostLocation : Map<String,String>,@Path("location_id") id:String
    ):Response<ResponseListPostUser.ListPostUserResult>

    @POST("$API_LIST_POST_UTENSILS{utensil_id}")
    @Multipart
    suspend fun listPostUtensils(
            @PartMap requestListPostUtensils : Map<String,String>, @Path("utensil_id") id: String
    ):Response<ResponseListPostUser.ListPostUserResult>

    @POST("$API_LIST_DELETE_COMMENT{id_comment}")
    @Multipart
    suspend fun deleteComment(
        @PartMap requestDeleteComment : Map<String,String>,@Path("id_comment") id_comment:String
    ):Response<ResponseDeleteComment.DeleteCommentResult>

    @POST(API_LIST_FOLLOW)
    @Multipart
    suspend fun listFollow(
        @PartMap requestListFollow : Map<String,String>
    ):Response<ResponseFollowUser.FollowUserResult>

    @POST(API_LIST_NOTIFICATION)
    @FormUrlEncoded
    suspend fun listNotification(
            @FieldMap requestListNotification: Map<String,String>
    ):Response<ResponseNotification.NotificationResult>

    @POST(API_LIST_FOLLOWER)
    @Multipart
    suspend fun listFollower(
        @PartMap requestListFollower : Map<String,String>
    ):Response<ResponseFollowUser.FollowUserResult>

    @POST("$API_EDIT_POST{id}")
    @Multipart
    suspend fun editPost(
        @PartMap requestEditPost : Map<String,String>, @Part listImage: ArrayList<MultipartBody.Part>, @Path("id") id:String
    ):Response<ResponseEditPost.EditPostResult>

    @POST(API_NOTIFICATION_UNREAD)
    @Multipart
    suspend fun notifyUnread(
        @PartMap requestNotifyUnread:Map<String,String>
    ):Response<ResponseNotifyUnread.NotifyUnreadResult>
}