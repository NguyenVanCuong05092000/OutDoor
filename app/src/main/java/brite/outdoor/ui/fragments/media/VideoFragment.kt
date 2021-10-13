package brite.outdoor.ui.fragments.media

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.animation.doOnCancel
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import brite.outdoor.databinding.FrmVideoBinding
import brite.outdoor.ui.fragments.BaseFragmentMedia
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.entity.Media
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.CameraViewModel
import kotlinx.android.synthetic.main.frm_setting.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates


@SuppressLint("RestrictedApi")
class VideoFragment : BaseFragmentMedia<FrmVideoBinding>(R.layout.frm_video) {
    // An instance for display manager to get display change callbacks
    private val displayManager by lazy { requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var videoCapture: VideoCapture? = null

    private var displayId = -1

    // Selector showing which camera is selected (front or back)
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    // Selector showing which flash mode is selected (on, off or auto)
    private var flashMode by Delegates.observable(ImageCapture.FLASH_MODE_OFF) { _, _, new ->
        binding.btnFlash.setImageResource(
            when (new) {
                ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
                ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_auto
                else -> R.drawable.ic_flash_off
            }
        )
    }

    // Selector showing is grid enabled or not
    private var hasGrid = false

    // Selector showing is flash enabled or not
    private var isTorchOn = false

    // Selector showing is recording currently active
    private var isRecording = false
    private val animateRecord by lazy {
        ObjectAnimator.ofFloat(binding.btnRecordVideo, View.ALPHA, 1f, 0.5f).apply {
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            doOnCancel { binding.btnRecordVideo.alpha = 1f }
        }
    }

    private var mTimeRunning = false

    var job: Job? = null

    private val viewMode by activityViewModels<CameraViewModel>()

    // A lazy instance of the current fragment's view binding
    override val binding: FrmVideoBinding by lazy { FrmVideoBinding.inflate(layoutInflater) }


    /**
     * A display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@VideoFragment.displayId) {
                preview?.targetRotation = view.display.rotation
                videoCapture?.setTargetRotation(view.display.rotation)
            }
        } ?: Unit
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hasGrid = PrefManager.getInstance(requireContext()).getBoolean(KEY_GRID, false)
        initViews()

        binding.btnRecordVideo.visibility=View.VISIBLE

        displayManager.registerDisplayListener(displayListener, null)

        binding.run {
            viewFinder.addOnAttachStateChangeListener(object :
                View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) =
                    displayManager.registerDisplayListener(displayListener, null)

                override fun onViewAttachedToWindow(v: View) =
                    displayManager.unregisterDisplayListener(displayListener)
            })
            btnSwitchCamera.setOnClickListener { toggleCamera() }
            btnGrid.setOnClickListener { toggleGrid() }
            btnFlash.setOnClickListener { toggleFlash() }
            btnCamera.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_video_to_camera) }

            // This swipe gesture adds a fun gesture to switch between video and photo
            val swipeGestures = SwipeGestureDetector().apply {
                setSwipeCallback(left = {
                    Navigation.findNavController(view).navigate(R.id.action_video_to_camera)
                })
            }

            val gestureDetectorCompat = GestureDetector(requireContext(), swipeGestures)
            viewFinder.setOnTouchListener { _, motionEvent ->
                if (gestureDetectorCompat.onTouchEvent(motionEvent)) return@setOnTouchListener false
                return@setOnTouchListener true
            }
        }

        val whProgressLoading = MyApplication.getInstance().getSizeWithScale(40.0)
        binding.progressLoadVideo.resizeLayout(whProgressLoading,whProgressLoading)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRecordVideo.setOnClickListener {
            recordVideo()
            viewMode.actionState.apply {
                if (this.value != CameraViewModel.STATE.CONSUMED) {
                    if (!mTimeRunning) {
                        onStartVideo()
                    }
                }
                this.postValue(CameraViewModel.STATE.CONSUMED)

            }

        }

    }

    /**
     * Create some initial states
     * */
    private fun initViews() {
        binding.btnGrid.setImageResource(if (hasGrid) R.drawable.ic_grid_on else R.drawable.ic_grid_off)
        binding.groupGridLines.visibility = if (hasGrid) View.VISIBLE else View.GONE

        adjustInsets()
    }

    /**
     * This methods adds all necessary margins to some views based on window insets and screen orientation
     * */
    private fun adjustInsets() {
        activity?.window?.fitSystemWindows()
        binding.btnRecordVideo.onWindowInsets { view, windowInsets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                view.bottomMargin =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            } else {
                view.endMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).right
            }
        }
        binding.btnFlash.onWindowInsets { view, windowInsets ->
            view.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        }
    }

    /**
     * Change the facing of camera
     *  toggleButton() function is an Extension function made to animate button rotation
     * */
    private fun toggleCamera() = binding.btnSwitchCamera.toggleButton(

        flag = lensFacing == CameraSelector.DEFAULT_BACK_CAMERA,
        rotationAngle = 180f,
        firstIcon = R.drawable.ic_outline_camera_rear,
        secondIcon = R.drawable.ic_outline_camera_front,
    ) {
        lensFacing = if (it) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        startCamera()
        viewMode.actionState.postValue(CameraViewModel.STATE.NOTHING)
        job?.cancel()
        animateRecord.cancel()
        isRecording=false
        binding.progressCircularId.progress=0
        mTimeRunning = false

    }

    /**
     * Unbinds all the lifecycles from CameraX, then creates new with new parameters
     * */
    private fun startCamera() {
        // This is the Texture View where the camera will be rendered
        val viewFinder = binding.viewFinder

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // The display information
            val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
            // The ratio for the output image and preview
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            // The display rotation
            val rotation = viewFinder.display.rotation

            val localCameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

            // The Configuration of camera preview
            preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio) // set the camera aspect ratio
                .setTargetRotation(rotation) // set the camera rotation
                .build()

            val videoCaptureConfig =
                VideoCapture.DEFAULT_CONFIG.config // default config for video capture
            // The Configuration of video capture
            videoCapture = VideoCapture.Builder
                .fromConfig(videoCaptureConfig)
                .build()

            localCameraProvider.unbindAll() // unbind the use-cases before rebinding them

            try {
                // Bind all use cases to the camera with lifecycle
                camera = localCameraProvider.bindToLifecycle(
                    viewLifecycleOwner, // current lifecycle owner
                    lensFacing, // either front or back facing
                    preview, // camera preview use case
                    videoCapture, // video capture use case
                )

                // Attach the viewfinder's surface provider to preview use case
                preview?.setSurfaceProvider(viewFinder.surfaceProvider)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind use cases", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     *  Detecting the most suitable aspect ratio for current dimensions
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /**
     * Navigate to PreviewFragment
     * */
    private fun openPreview(media:Media) {
        view?.let {
        findNavController().navigate(VideoFragmentDirections.actionVideoToPreview(media))
        }
    }

    @SuppressLint("MissingPermission")
    private fun recordVideo() {
        val localVideoCapture =
            videoCapture ?: throw IllegalStateException("Camera initialization failed.")

        // Options fot the output video file
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            requireContext().contentResolver.run {
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                VideoCapture.OutputFileOptions.Builder(this, contentUri, contentValues)
            }
        } else {
            File(outputDirectory).mkdirs()
            val file = File("$outputDirectory/${System.currentTimeMillis()}.mp4")

            VideoCapture.OutputFileOptions.Builder(file)
           
        }.build()

        if (!isRecording) {
            animateRecord.start()
            localVideoCapture.startRecording(
                outputOptions, // the options needed for the final video
                requireContext().mainExecutor(), // the executor, on which the task will run
                object : VideoCapture.OnVideoSavedCallback { // the callback after recording a video
                    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                        // Create small preview

                        try {
                            val file = File(outputFileResults.savedUri.toString())
                            var media:Media?=null

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                                media =
                                    outputFileResults.savedUri?.let { Media(it,isVideo = true) }
                            }else{
                                media =
                                    outputFileResults.savedUri?.let { Media(it,isVideo = true,file.path.substring(6)) }
                            }
                            media?.let {
                                if (binding.progressLoadVideo.isVisible) openPreview(it)
                                binding.progressLoadVideo.visibility = View.GONE
                                binding.progressLoadVideo.animation = null
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                    }

                    override fun onError(
                        videoCaptureError: Int,
                        message: String,
                        cause: Throwable?
                    ) {
                        // This function is called if there is an error during recording process
                        animateRecord.cancel()
                        val msg = "Video capture failed: $message"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        cause?.printStackTrace()
                    }
                })
        } else {
            //show progress load save video
            binding.progressLoadVideo.visibility=View.VISIBLE
            binding.progressLoadVideo.setAnimation(12,800)

            // disable click button RecordVideo
            binding.btnRecordVideo.visibility=View.GONE
            // stop timer and set progress = 0
            job?.cancel()
            binding.progressCircularId.progress = 0
            mTimeRunning = false
            // cancel anim btn Record and go to PreView
            animateRecord.cancel()
            localVideoCapture.stopRecording()

        }
        isRecording = !isRecording
    }

    /**
     * Turns on or off the grid on the screen
     * */
    private fun toggleGrid() = binding.btnGrid.toggleButton(
        flag = hasGrid,
        rotationAngle = 180f,
        firstIcon = R.drawable.ic_grid_off,
        secondIcon = R.drawable.ic_grid_on
    ) { flag ->
        hasGrid = flag
        PrefManager.getInstance(requireContext()).writeBoolean(KEY_GRID, flag)
        binding.groupGridLines.visibility = if (flag) View.VISIBLE else View.GONE
    }

    /**
     * Turns on or off the flashlight
     * */
    private fun toggleFlash() = binding.btnFlash.toggleButton(
        flag = flashMode == ImageCapture.FLASH_MODE_ON,
        rotationAngle = 360f,
        firstIcon = R.drawable.ic_flash_off,
        secondIcon = R.drawable.ic_flash_on
    ) { flag ->
        isTorchOn = flag
        flashMode = if (flag) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        camera?.cameraControl?.enableTorch(flag)
    }

    override fun onPermissionGranted() {
        // Each time apps is coming to foreground the need permission check is being processed
        binding.viewFinder.let { vf ->
            vf.post {
                // Setting current display ID
                displayId = vf.display.displayId
                startCamera()
                camera?.cameraControl?.enableTorch(isTorchOn)
            }
        }
    }

    private fun onStartVideo() {
        job = GlobalScope.launch(Dispatchers.Main) {
            //code
            for (x in 0..300 step 1) {
                binding.progressCircularId.progress = x
                delay(100)
                if (x==300) recordVideo()
                Log.e(TAG, "onStartVideo: $x", )
            }

        }
        mTimeRunning = true
    }

    override fun onBackPressed() = requireActivity().finish()

    override fun onStop() {
        super.onStop()
        camera?.cameraControl?.enableTorch(false)
    }

    companion object {
        private const val TAG = "CameraXDemo"

        const val KEY_GRID = "sPrefGridVideo"

        private const val RATIO_4_3_VALUE = 4.0 / 3.0 // aspect ratio 4x3
        private const val RATIO_16_9_VALUE = 16.0 / 9.0 // aspect ratio 16x9
    }


}