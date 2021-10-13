package brite.outdoor.ui.fragments.select_place

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import brite.outdoor.R
import brite.outdoor.data.entities.LocationEntity
import brite.outdoor.databinding.FrmMapBinding
import brite.outdoor.ui.fragments.BaseDialogFragment
import brite.outdoor.utils.*
import brite.outdoor.viewmodel.SelectPlaceViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frm_map.*
import java.util.*


@AndroidEntryPoint
class FrmMap(val onShowPushPost:(LocationEntity) ->Unit={_:LocationEntity->}) : BaseDialogFragment<FrmMapBinding>(), View.OnClickListener {
    private var midLatLng: LatLng? = null
    private var mapFragment: SupportMapFragment? = null
    private val selectPlaceViewModel by viewModels<SelectPlaceViewModel>()
    private var initMapPosition: LatLng? = null
    private var mMap: GoogleMap? = null
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap?.let {
            initMapPosition = LatLng(21.028833569997694, 105.85214260208838)

            it.animateCamera(CameraUpdateFactory.newLatLngZoom(initMapPosition, 15f))
            it.setOnCameraMoveListener(OnCameraMoveListener {
                midLatLng = googleMap.cameraPosition.target
            })
            it.setOnCameraIdleListener {
                try {
                    midLatLng = googleMap.cameraPosition.target
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses: List<Address> = geocoder.getFromLocation(midLatLng?.latitude!!, midLatLng?.longitude!!, 1)
                    getBinding()?.edtNameLocation?.text = addresses[0].getAddressLine(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

//    listFragment.add(FrmMap(onShowPushPost={location:LocationEntity->onShowPushPost(location)}))
    override fun loadControlsAndResize(binding: FrmMapBinding?) {
        getBinding()?.apply {
            val sizeButtonSearch = getSizeWithScale(40.0)
            clTopNav.resizeHeight(getSizeWithScale(56.0))
            imgMaker.resizeLayout(getSizeWithScale(24.0), getSizeWithScale(24.0))
            btnTopNavButton.resizeLayout(getSizeWithScale(38.67), getSizeWithScale(38.67))
            tvTopNavTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getSizeWithScaleFloat(18.0))
            edtSearchPlace.resizeHeight(getSizeWithScale(46.0))
            edtNameLocation.resizeHeight(getSizeWithScale(46.0))

            btnSearch.resizeLayout(sizeButtonSearch, sizeButtonSearch)
            btnSearch.setOnClickListener(this@FrmMap)
            btnAddPlace.setOnClickListener(this@FrmMap)
            btnTopNavButton.setOnClickListener(this@FrmMap)
        }
    }
    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmMapBinding {
        return FrmMapBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        searchClick()

    }

    private fun searchPlace(){
        try {
            AppUtils.hideKeyboard(view)
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocationName(getBinding()?.edtSearchPlace?.text.toString(), 5)
            if (addresses.isNullOrEmpty()){
                getBinding()?.tvErrNoPlace?.visibility = View.VISIBLE
                getBinding()?.edtNameLocation?.text = ""
            }else {
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(addresses[0].latitude, addresses[0].longitude), 17f))
                getBinding()?.tvErrNoPlace?.visibility = View.GONE
               getBinding()?.edtNameLocation?.text = addresses[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
       if (!isClickAble()) return
        when(v?.id){
            R.id.btnSearch->{
                if (getBinding()?.edtSearchPlace?.text.toString() != "") searchPlace()
            }
            R.id.btnTopNavButton-> dismissAllowingStateLoss()
            R.id.btnAddPlace->{
                selectPlaceViewModel.locationSelected.value = LocationEntity(midLatLng?.latitude?:0.0, midLatLng?.longitude?:0.0, "", edtNameLocation.text.toString())
                val location = LocationEntity(midLatLng?.latitude?:0.0, midLatLng?.longitude?:0.0, "",edtNameLocation.text.toString() )
                onShowPushPost(location)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun searchClick() {
        getBinding()?.apply {
            edtSearchPlace.setOnKeyListener { v, keyCode, event ->

                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    KeyboardUtil.hideKeyboard(mActivity)
                    val key = edtSearchPlace.text.toString()
                    if (key.isNotEmpty()) {
                        if (getBinding()?.edtSearchPlace?.text.toString() != "") searchPlace()
                    }
                    edtSearchPlace.isCursorVisible=false
                }

                false
            }
            edtSearchPlace.setOnTouchListener { _, _ ->
                edtSearchPlace.isCursorVisible = true
                false
            }
        }
    }
}