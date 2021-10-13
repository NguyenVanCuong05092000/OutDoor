package brite.outdoor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import brite.outdoor.R
import brite.outdoor.constants.AppConst.Companion.NOTIFICATION_APP
import brite.outdoor.constants.AppConst.Companion.NOTIFICATION_USER
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.ui.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var broadcaster : LocalBroadcastManager?=null

    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(this)
    }
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            val data = Bundle()
            for ((key, value) in remoteMessage.data) {
                data.putString(key, value)
            }

            val mNotificationManager =
                    applicationContext.getSystemService(
                            NOTIFICATION_SERVICE
                    ) as NotificationManager
            val channelId = getString(R.string.project_id)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handleNotificationAndroidO(mNotificationManager, channelId)
            }

            val title = data.getString("title") ?: ""
            val body = data.getString("body") ?: ""
            val userId = data.getString("user_id") ?: ""
            val type = data.getString("type") ?: ""
            val p_id = data.getString("p_id") ?: ""
            val userFollowId = data.getString("user_follow_id")
            val postId = data.getString("post_id")
            val comment_id=data.getString("comment_id")
            val thread_id = data.getString("thread_id")
            val post_id = data.getString("post_id")
            Log.e("TAG", "onMessageReceived: $userFollowId", )


            if (userId.isNotEmpty()){

                if (title.isNotEmpty() || body.isNotEmpty()) {
                    val context = applicationContext
                    val defaultAction = Intent(context, MainActivity::class.java)
                            .setAction(Intent.ACTION_DEFAULT)
                            .putExtra("push", data)
                            .putExtra("notificationFragment","notificationFragment")
                    val mBuilder = NotificationCompat.Builder(context,channelId)
                            .setSmallIcon(R.drawable.notification_template_icon_bg)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentIntent(
                                    PendingIntent.getActivity(
                                            context,
                                            0,
                                            defaultAction,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    )
                            )
                    mNotificationManager.notify(NOTIFICATION_USER, mBuilder.build())
                    val intent = Intent("notify-receive-listener");
                    broadcaster?.sendBroadcast(intent)
                }
            }else {
                if (title.isNotEmpty() || body.isNotEmpty()) {
                    val context = applicationContext
                    val defaultAction = Intent(context, MainActivity::class.java)
                            .setAction(Intent.ACTION_DEFAULT)
                            .putExtra("push", data)
                            .putExtra("notificationFragment","notificationFragment")
                    val mBuilder = NotificationCompat.Builder(context,channelId)
                            .setSmallIcon(R.drawable.notification_template_icon_bg)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setContentIntent(
                                    PendingIntent.getActivity(
                                            context,
                                            0,
                                            defaultAction,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    )
                            )

                    mNotificationManager.notify(NOTIFICATION_APP, mBuilder.build())
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNotificationAndroidO(
            notificationManager: NotificationManager,
            channelId: String
    ) {
        createNotificationChannel(notificationManager, channelId)
        notificationManager
                .getNotificationChannel(channelId)
                ?.canBypassDnd()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
            notificationManager: NotificationManager,
            channelId: String
    ) {
        val notificationChannel =
                NotificationChannel(channelId, getString(R.string.app_name), IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
//class MyFirebaseMessagingService : FirebaseMessagingService(){
//    private val TAG = "FirebaseMessaging"
//
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. This call is initiated by the
//     * InstanceID provider.
//     */
//    override fun onNewToken(s: String) {
//        super.onNewToken(s)
//
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        try {
////            if (TextUtils.isEmpty(ActMain.currentToken)) { // not logged in
////                return;
////            }
////            Log.d("FirebaseMessaging", "Data : " + remoteMessage.getData());
//
//            val data = Bundle()
//            for (entry in remoteMessage.data.entries) {
//
//                data.putString(entry.key, entry.value)
//            }
//            val title = data.getString("title") ?: ""
//            val body = data.getString("body") ?: ""
//            val userId = data.getString("user_id") ?: ""
//            val type = data.getString("type") ?: ""
//            val p_id = data.getString("p_id") ?: ""
//            if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(body)) {
//                val context = applicationContext
//                val defaultAction = Intent(context, MainActivity::class.java)
//                        .setAction(Intent.ACTION_DEFAULT)
//                        .putExtra("push", data)
//                //                defaultAction.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                val mBuilder: NotificationCompat.Builder
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val NOTIFICATION_CHANNEL_ID = getString(R.string.project_id)
//                    //
//                    val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
//                    // Configure the notification channel.
//                    notificationChannel.description = "A channel for notification"
//                    notificationChannel.enableLights(true)
//                    //                    notificationChannel.setLightColor(Color.RED);
//                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
//                    notificationChannel.enableVibration(true)
//                    notificationManager.createNotificationChannel(notificationChannel)
//                    //
//                    mBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
//                } else {
//                    mBuilder = NotificationCompat.Builder(context)
//                }
//
//                mBuilder.setSmallIcon(R.drawable.notification_template_icon_bg)
//                        .setContentTitle(title ?: "")
//                        .setContentText(body ?: "")
//                        .setAutoCancel(true)
//                        .setContentIntent(PendingIntent.getActivity(
//                                context,
//                                0,
//                                defaultAction,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        ))
//
//                val notification = mBuilder.build()
//                val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//                mNotificationManager.notify( //                        isProduct? NotificationConstants.ID_VIEW_PRODUCT_INFO
//                        //                                : NotificationConstants.ID_VIEW_NEWS
//                        NOTIFICATION_USER, notification)
//
////                Log.e(TAG, "onMessageReceived: $type")
////                if (type == TYPE_QA) sendBroadcast(Intent().setAction(IntentActionConstant.ACTION_PUSH_QA)) else if (type == TYPE_NEWS || type == TYPE_MAINTENANCE) {
////                    sendBroadcast(Intent().setAction(IntentActionConstant.ACTION_PUSH_NEWS))
////                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}
