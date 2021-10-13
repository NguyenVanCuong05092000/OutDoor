package brite.outdoor.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T> : AppCompatActivity() {
    protected val TAG = this.javaClass.simpleName
    private var binding: T? = null
    protected abstract fun setBinding(): T
    protected fun getBinding() = binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setBinding()
        setContentView((getBinding() as ViewBinding).root)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    //============================
    //=== Fragment Controller  ===
    //============================
    protected fun addOrReplaceFragment(fragment: Fragment, idContent: Int, tag: String) {
        val currentFrm = supportFragmentManager.findFragmentById(idContent)
        if (currentFrm == null) {
            addFragment(fragment, idContent, tag)
        } else {
            replaceFragment(fragment, idContent, tag)
        }
    }

    protected fun addFragmentAddBackStack(fragment: Fragment, idContent: Int, tag: String) {
        try {
            supportFragmentManager.beginTransaction().add(idContent, fragment, tag).addToBackStack(tag).commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun addFragment(fragment: Fragment, idContent: Int, tag: String) {
        try {
            supportFragmentManager.beginTransaction().add(idContent, fragment, tag).commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun replaceFragmentAddBackStack(fragment: Fragment, idContent: Int, tag: String) {
        try {
            supportFragmentManager.beginTransaction().replace(idContent, fragment, tag).addToBackStack(tag).commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun replaceFragment(fragment: Fragment, idContent: Int, tag: String) {
        try {
            supportFragmentManager.beginTransaction().replace(idContent, fragment, tag).commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findFragmentByTag(tag: String): Fragment? {
        return supportFragmentManager.findFragmentByTag(tag)
    }

    protected fun showFragmentByTag(tag: String) {
        try {
            val frm = findFragmentByTag(tag)
            if (frm != null) {
                supportFragmentManager.beginTransaction().show(frm).commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun hideFragmentByTag(tag: String) {
        try {
            val frm = findFragmentByTag(tag)
            if (frm != null) {
                supportFragmentManager.beginTransaction().hide(frm).commitAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}