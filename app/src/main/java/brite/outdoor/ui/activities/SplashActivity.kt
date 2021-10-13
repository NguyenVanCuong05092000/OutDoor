package brite.outdoor.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import brite.outdoor.constants.PrefConst
import brite.outdoor.data.local.pref.PrefManager
import java.util.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 750L)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun loadLocale(){
        val language = PrefManager.getInstance(this).getString(PrefConst.PREF_MULTI_LANGUAGE)
        language?.let {
            if (it=="") PrefManager.getInstance(this).writeString(PrefConst.PREF_MULTI_LANGUAGE,"vi")
            setLocale(language)
        }
    }
    private fun setLocale(lang:String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config= Configuration()
        config.locale=locale

        this.resources.updateConfiguration(config,this.resources.displayMetrics)
       

    }
}
