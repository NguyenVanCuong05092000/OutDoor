package brite.outdoor.constants

class AppConst {
    companion object {
        const val TEST_MODE = true
        //
        const val SCREEN_WIDTH_DESIGN = 375
        const val DATE_FORMAT = "yyyy/MM/dd HH:mm:ss"
        const val TIME_FORMAT = "HH:mm"
        const val THRESHOLD_CLICK_TIME = 500
        const val THRESHOLD_CLICK_TIME_SWITCH_SCREEN = 3000
        const val DEFAULT_FIRST_PAGE = 1
        const val DEFAULT_ITEM_PER_PAGE = 20
        const val DEFAULT_ITEM_PER_PAGE_COMMENT = 20
        const val STATUS_PEOPLE_FOLLOW = 0
        const val STATUS_PEOPLE_FOLLOWERS = 1
        const val STATUS_PEOPLE_LIKE = 2
        const val DEFAULT_TOTAL_RESULT = 0
        const val LANGUAGE_VIETNAM = "1"
        const val LANGUAGE_ENGLISH = "2"
        const val TYPE_NOTIFICATION_FOLLOW=1
        const val TYPE_NOTIFICATION_COMMENT=2
        const val TYPE_NOTIFICATION_NEW_POST=3
        const val RESULT_CODE=101
        const val STATUS_LANGUAGE_VI = 1
        const val STATUS_LANGUAGE_EN = 2
        const val TYPE_PUSH_POST_LOCATION="1"
        const val TYPE_PUSH_POST_UTENSIL="2"
        const val DEFAULT_COUNT_NOTIFY_UNREAD=0
        const val DEFAULT_NUMBER_VIDEO_POST=1
        const val DEFAULT_NUMBER_IMAGE_POST=9


        const val URL_OUTDOOR="https://brite.vn/outdoor_html/source/index.html"
        const val URL_TERM = "http://outdoor.brite.vn/term"
        const val URL_POLICY = "http://outdoor.brite.vn/ios-policy"
        const val URL_REPORT_VIOLATIONS = "http://outdoor.brite.vn/ReportViolation?language="

        const val LOGIN_WITH_UNKNOWN = "UNKNOWN"
        const val LOGIN_WITH_GOOGLE = "GOOGLE"
        const val LOGIN_WITH_FACEBOOK = "FACEBOOK"

        //notification
        const val NOTIFICATION_APP = 123
        const val NOTIFICATION_USER = 124

        //Fragment
        const val FRM_INTRO = 1
        const val FRM_LOGIN = 2
        const val FRM_HOME = 3
        const val FRM_MY_PAGE = 4
        const val FRM_PUSH_POST = 5
        const val FRM_SEARCH = 6
        const val FRM_POST_DETAIL = 7
        const val FRM_SEARCH_DETAIL = 8
        const val FRM_SETTING = 9
        const val FRM_LIST_POST_LOCATION = 10


        const val FRM_LIST_PEOPLE_INTERACTIVE= 11
        const val FRM_PERSONAL_PAGE= 12
        const val FRM_LIST_PUSH= 13
        const val FRM_MAP= 14
        const val FRM_SELECT_PLACE= 15
        const val FRM_NOTIFICATION=16

        const val FRM_LANGUAGE = 17
        const val FRM_SELECT_UTENSILS= 19
        //
        const val FRM_POST_LOCATION=1
        const val FRM_POST_UTENSILS=2
        const val FRM_EDIT_POST = 3

        //
        const val DIALOG_AVATAR = 1
        const val FRM_FOCUS=20
        const val FRM_FOLLOW=21
        const val FRM_NEWS=22
        const val FRM_LIST_BOOKMARK=23
        const val FRM_MY_POST=24

    }
}