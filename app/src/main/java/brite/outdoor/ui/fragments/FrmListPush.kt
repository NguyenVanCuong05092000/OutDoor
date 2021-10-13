package brite.outdoor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import brite.outdoor.adapter.utils.AdapterNotification
import brite.outdoor.constants.AppConst.Companion.FRM_LIST_PUSH
import brite.outdoor.data.api_entities.response.ResponseListPush
import brite.outdoor.databinding.FrmListPushBinding


class FrmListPush : BaseFragment<FrmListPushBinding>(), View.OnClickListener {
    private var adapterNotification: AdapterNotification? = null
    private var listNotification: ArrayList<ResponseListPush.ListPushData> = ArrayList()
    override fun isBackPreviousEnable(): Boolean {
        return true
    }
    override fun backToPrevious() {
        finish()
    }
    override fun loadControlsAndResize(binding: FrmListPushBinding?) {

    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): FrmListPushBinding {
        return FrmListPushBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        setDataList()
    }

    override fun getCurrentFragment(): Int {
        return FRM_LIST_PUSH
    }

    override fun finish() {
        mActivity?.closeMainFuncScreen(this)
    }

    private fun initAdapter(){
        getBinding()?.apply {
            adapterNotification = AdapterNotification(requireContext(), object : AdapterNotification.OnClickItemListener{
                override fun onClickItem(entityNew: ResponseListPush.ListPushData?) {

                }

            })
            rcListPush.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterNotification
            }
        }
    }

    private fun setDataList(){
        try {
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))
            listNotification.add(ResponseListPush.ListPushData(1, "THÔNG BÁO KHẨN SỐ 07: Về các trường hợp dương tính với SARS-CoV-2 liên quan ổ dịch chợ Tân An"," 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh. 2 CA (BN34606-BN34607) ghi nhận tại tỉnh Bắc Ninh: 1 ca liên quan đến ổ dịch Khu công nghiệp Quế Võ; 1 ca liên quan đến ổ dịch Khu 3 - Tiền An. Kết quả xét nghiệm ngày 13/7/2021 dương tính với SARS-CoV-2. Hiện đang cách ly, điều trị tại Bệnh viện Đa khoa tỉnh Bắc Ninh.", "2021-06-02T14:24:42+0700"))

            adapterNotification?.apply {
                updateList(listNotification)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onClick(v: View?) {
    }

}