package brite.outdoor.constants

class ApiConst {
    companion object {

        const val API_KEY = "12ca361d-3ac8-9f90d-5d60-a625addabd05e"
        const val PARAM_API = "api_key"
        const val PARAM_API_VERSION = "1"
        const val PARAM_DEVICE_TYPE = "device_type"
        const val PARAM_DEVICE_UUID = "device_uuid"
        const val API_VERSION = "1"
        const val ANDROID = "2"
        const val PARAM_TOKEN = "token"

        const val HTTP_OK = 200
        const val HTTP_ERROR_TOKEN = 402
        const val HTTP_ERROR_DELETED_POST= 407
        const val HTTP_ERROR_DELETED_COMMENT=408

        const val DOMAIN_NAME = "https://outdoor.brite.vn/api/"
        const val API_LOGIN_GG_END_POINT = "v$API_VERSION/userLogin/loginWithGoogle"
        const val API_LOGIN_FB_END_POINT = "v$API_VERSION/userLogin/loginWithFacebook"
        const val API_LOGOUT_END_POINT = "v$API_VERSION/userLogin/logout"
        const val API_LIST_LOCATION_END_POINT = "v$API_VERSION/user/listLocation"
        const val API_LIST_UTENSILS_END_POINT = "v$API_VERSION/user/listUtensils"
        const val API_POST_LIKES = "v$API_VERSION/post/postLikes"
        const val API_POST_SHARES = "v$API_VERSION/post/postShare"
        const val API_POST_COMMENT = "v$API_VERSION/post/postComment"
        const val API_PUSH_POST = "v$API_VERSION/post/create"
        const val API_SEARCH_LOCATIONS = "v$API_VERSION/post/searchLocation"
        const val API_SEARCH_UTENSILS = "v$API_VERSION/post/searchUtensil"
        const val API_REGISTER_TOKEN_PUSH = "v$API_VERSION/user/registerTokenPush/"

        const val API_LIST_COMMENT = "v$API_VERSION/post/listComment/"
        const val API_LIST_COMMENT_LEVEL_2 = "v$API_VERSION/post/listCommentLvl2/"
        const val API_FOLLOW_END_POINT = "v$API_VERSION/user/follow"
        const val API_DETAIL_POST = "v$API_VERSION/post/detailPost/"
        const val API_LIST_POST_USER_END_POINT = "v$API_VERSION/post/listPostsOfUser"
        const val API_BOOKMARK = "v$API_VERSION/user/bookmark"
        const val API_DETAIL = "v$API_VERSION/user/detail"
        const val API_LIST_POST_USER_FOLLOW =  "v$API_VERSION/post/listPostUserFollow"
        const val API_LIST_BOOK_MARK = "v$API_VERSION/user/listBookMark"
        const val API_UNBOOKMARK = "v$API_VERSION/user/unBookmark/"
        const val API_LIST_POST_HOT = "v$API_VERSION/post/listPostHot"
        const val API_LIST_POST_NEW = "v$API_VERSION/post/listPostNew"
        const val API_LIST_SEARCH_USER = "v$API_VERSION/user/searchUser"
        const val API_REPORT = "v$API_VERSION/post/reportPost"
        const val API_HIDE_POST = "v$API_VERSION/post/hidePosts"
        const val API_DELETE_POST= "v$API_VERSION/post/deletePost/"
        const val API_CREATE_HASH = "v$API_VERSION/UserLogin/createHash"
        const val API_CANCEL_MERGE_TO_CREATE_NEW_ACCOUNT = "v$API_VERSION/UserLogin/createNewAccount"
        const val API_CHECK_CODE_HASH = "v$API_VERSION/UserLogin/checkCode"
        const val API_MERGE_ACCOUNT = "v$API_VERSION/UserLogin/mergeAcount"
        const val API_CANCEL_MERGE_ACCOUNT = "v$API_VERSION/UserLogin/cancelMerge"
        const val API_LIST_POST_LOCATION = "v$API_VERSION/post/listPostByLocationsId/"
        const val API_LIST_POST_UTENSILS = "v$API_VERSION/post/listPostByUtensilId/"
        const val API_LIST_DELETE_COMMENT = "v$API_VERSION/post/deleteComment/"
        const val API_LIST_FOLLOW = "v$API_VERSION/user/listFollow"
        const val API_LIST_NOTIFICATION = "v$API_VERSION/notification/index"
        const val API_LIST_FOLLOWER = "v$API_VERSION/user/listFollower"
        const val API_EDIT_POST = "v$API_VERSION/post/edit/"
        const val API_NOTIFICATION_UNREAD="v$API_VERSION/notification/countNotiUnread/"

        //API_LOGIN
        const val PARAM_API_SOCIAL_ID = "social_id"
        const val PARAM_API_ACCESS_TOKEN = "access_token"
        const val PARAM_API_TYPE_SOCIAL = "type_social"
        const val PARAM_API_CODE_HASH ="code_hash"

        //API_LOGOUT
        const val PARAM_API_TOKEN = "token"

        //API_PUSH_POST
        const val PARAM_API_TITLE = "title"
        const val PARAM_API_CONTENT = "content"
        const val PARAM_API_LOCATION_ID = "location_id"
        const val PARAM_API_HASHTAG_ID = "hashtag_id"
        const val PARAM_API_UTENSIL_ID = "utensil_id"
        const val PARAM_API_LOCATION_NAME = "location_name"
        const val PARAM_API_LAT = "lat"
        const val PARAM_API_LNG = "lng"
        const val PARAM_API_UTENSIL_NAME = "utensil_name"
        const val PARAM_API_HASHTAG_NAME = "hashtag_name"
        const val PARAM_API_IMG_DELETE = "img_delete"
        const val PARAM_API_TYPE_PUSH_POST="type"


        const val PARAM_API_SEARCH = "key_word"
        const val PARAM_API_LAST_SYNC="last_sync"
        const val PARAM_API_FIREBASE_TOKEN="firebase_token"
        const val PARAM_IMAGE="image"
        const val PARAM_TYPE_VIDEO="type_video"

        //follow
        const val PARAM_API_USER_ID = "user_id"
        const val PARAM_API_FOLLOW_ID = "follower_id"
        const val PARAM_API_POST_ID = "post_id"
        const val PARAM_API_PAGE="page"
        const val PARAM_API_LASTEST_ID="lastest_id"
        const val PARAM_API_LASTEST_CINDEX = "lastest_cIndex"
        //bookmark
        const val PARAM_API_BOOKMARK_ID = "id"
        const val PARAM_API_LIMIT = "limit"

        //comment
        const val PARAM_API_COMMENT = "comment"
        const val PARAM_API_PARENT_ID= "parent_id"
        const val PARAM_API_THREAD_ID= "thread_id"
        const val PARAM_API_REPLY_ID= "reply_id"
        const val PARAM_API_COMMENT_ID="comment_id"

        //report
        const val PARAM_REPORT_REASON="reason"

        // list Notification
        const val PARAM_LANGUAGE="language"

    }
}