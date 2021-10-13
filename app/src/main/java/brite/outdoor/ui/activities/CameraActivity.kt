package brite.outdoor.ui.activities

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import brite.outdoor.R
import brite.outdoor.constants.ExtraConst
import brite.outdoor.databinding.ActCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraActivity : BaseActivity<ActCameraBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        val type = bundle?.getInt(ExtraConst.EXTRA_MEDIA)
        val navController = findNavController(R.id.fragmentNavHost)
        val bundleNav = Bundle()
        type?.let {
            bundleNav.putInt("type",type)
            navController.setGraph(navController.graph,bundleNav)
        }
        intiView()
    }

    private fun intiView() {

    }

    override fun setBinding(): ActCameraBinding {
       return ActCameraBinding.inflate(layoutInflater)
    }
}