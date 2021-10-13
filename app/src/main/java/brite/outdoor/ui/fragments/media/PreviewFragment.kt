package brite.outdoor.ui.fragments.media
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import brite.outdoor.databinding.FrmPreviewBinding
import brite.outdoor.ui.fragments.BaseFragmentMedia
import brite.outdoor.R
import brite.outdoor.constants.AppConst.Companion.RESULT_CODE
import brite.outdoor.constants.ExtraConst.Companion.EXTRA_MEDIA
import brite.outdoor.entity.Media
import brite.outdoor.ui.activities.CameraActivity
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.CameraViewModel

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus



@AndroidEntryPoint
class PreviewFragment : BaseFragmentMedia<FrmPreviewBinding>(R.layout.frm_preview) {


    private val viewMode by activityViewModels<CameraViewModel>()
    override val binding: FrmPreviewBinding by lazy { FrmPreviewBinding.inflate(layoutInflater) }
    private var media:Media?=null
    private val args:PreviewFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        args.media.let {
            this.media=it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustInsets()
        viewMode.mediaData.value = getMedia()
        playVideo()

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.btnSend.setOnClickListener { shareImage() }



    }

    private fun playVideo() {
        try {
            viewMode.mediaData.observe(viewLifecycleOwner,{
//                 media = it.get(0)
                binding.apply {
                    if (media?.isVideo==true) {

                        playerView.setVideoURI(media?.uri)
                        playerView.setMediaController(null)
                        playerView.requestFocus()
                        playerView.start()
                        playerView.setOnPreparedListener { mp ->
                            //Start Playback
                            playerView.start()
                            //Loop Video
                            mp!!.isLooping = true;
                        }
                    } else {
                        imagePreview.visibility = View.VISIBLE
                        playerView.visibility = View.GONE
                        Glide.with(requireContext()).load(media?.uri)
                            .placeholder(R.drawable.progress_animation)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .transform(CenterCrop(), RoundedCorners(1))
                            .into(imagePreview)
                    }
                }
            })




        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * This methods adds all necessary margins to some views based on window insets and screen orientation
     * */
    private fun adjustInsets() {
        activity?.window?.fitSystemWindows()
        binding.btnBack.onWindowInsets { view, windowInsets ->
            view.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        }
        binding.bgBottom.onWindowInsets { view, windowInsets ->
            view.bottomMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
        }
    }

    private fun shareImage() {
        val  activity = requireActivity() as CameraActivity
        val intent =Intent()
        intent.putExtra(EXTRA_MEDIA,media)
        activity.setResult(RESULT_CODE,intent )
        activity.finish()
    }


    override fun onBackPressed() {
        try {

            view?.let { Navigation.findNavController(it).popBackStack() }
            viewMode.actionState.postValue(CameraViewModel.STATE.NOTHING)

//            val resolver = requireContext().applicationContext.contentResolver
//            resolver.delete(getMedia().get(0).uri, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }





}