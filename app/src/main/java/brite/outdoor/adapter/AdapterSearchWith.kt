package brite.outdoor.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.app.MyApplication
import brite.outdoor.constants.PrefConst
import brite.outdoor.constants.PrefConst.Companion.PREF_USER_ID
import brite.outdoor.data.api_entities.response.*
import brite.outdoor.data.entities.ObjectSearch
import brite.outdoor.data.local.pref.PrefManager
import brite.outdoor.databinding.ItemSearchWithLocateOrToolBinding
import brite.outdoor.databinding.ItemSearchWithUserBinding
import brite.outdoor.utils.*
import brite.outdoor.utils.loadImageWithCustomCorners
import com.bumptech.glide.Glide
import java.text.Normalizer
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class AdapterSearchWith(private val sizeAvatar: Int, private val sizeIcon: Int,private val context:Context,private val isShowBottomNavi:Boolean=true) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {
    interface OnClickFollowListener {
        fun onClickFollow(userData: ResponseSearchUser.SearchUserData)
    }
    interface OnClickItemListener{
      fun onClicked(item:Any)
    }
    companion object{
        const val FOLLOWED_FLAG=1
        const val UNFOLLOWED_FLAG=0
    }
    private var onClickFollowListener: OnClickFollowListener? = null
    private var onClickItemListener : OnClickItemListener?=null
    fun setOnClickFollow(mOnClickFollowListener: OnClickFollowListener?){
        this.onClickFollowListener = mOnClickFollowListener
    }
    fun setOnClickItem(mOnClick:OnClickItemListener?){
        this.onClickItemListener=mOnClick
    }
    private var listResult = ArrayList<ObjectSearch.ResultSearchEntity>()

    private var listData= ArrayList<ObjectSearch.ResultSearchEntity>()

    var filterList = ArrayList<ObjectSearch.ResultSearchEntity>()

    fun initData(list: List<ObjectSearch.ResultSearchEntity>?) {
        list?.let {
            listData.clear()
            listData.addAll(list)

        }
    }

    fun updateList(list: List<ObjectSearch.ResultSearchEntity>) {
        list.let {
            listResult.clear()
            listResult.addAll(list)
            notifyDataSetChanged()
        }
    }
    fun refreshData(){
        listResult.clear()
        listResult.addAll(listData)
        notifyDataSetChanged()
    }

    fun clearData(){
        listResult.clear()
        listData.clear()
        notifyDataSetChanged()
    }
    inner class SearchWithUserHolder(private val binding: ItemSearchWithUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivAvatar.resizeLayout(sizeAvatar, sizeAvatar)
            binding.viewFooter.layoutParams.height = MyApplication.getInstance().getSizeWithScale(65.0)
        }

        fun bind(result: ObjectSearch.ResultSearchEntity,position: Int) {
            binding.ivAvatar.setImageResource(R.drawable.ic_item_search_locate)
            if (result.content is ResponseSearchUser.SearchUserData){
                (result.content as ResponseSearchUser.SearchUserData).let {
                   val idUser = PrefManager.getInstance(context).getString(PREF_USER_ID)
                    if (idUser?.toInt() == it.id){
                        binding.btnFollow.visibility= View.GONE
                    }else{
                        if (it.followed_flag.toInt() == FOLLOWED_FLAG){
                            binding.btnFollow.setText(R.string.lb_unfollow_search)
                            binding.btnFollow.isSelected = true
                            binding.btnFollow.setTextColor(context.resources.getColor(R.color.colorMain))
                        }else{
                            binding.btnFollow.setText(R.string.lb_follow_search)
                            binding.btnFollow.isSelected = false
                            binding.btnFollow.setTextColor(context.resources.getColor(R.color.white))
                        }
                    }

                    binding.btnFollow.setSingleClick {
                        onClickFollowListener?.apply {
                            onClickFollow(it)
                        }
                    }
                    binding.tvName.text=it.name
                    try {
                        binding.ivAvatar.loadImageWithCustomCorners(urlImage(it.avatar,it.url_prefix_avatar),160)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                binding.tvName.setSingleClick {
                    onClickItemListener?.apply {
                        onClicked(result.content)
                    }
                }
            }
                if (position == listResult.size-1) binding.viewFooter.visibility= View.VISIBLE
                else binding.viewFooter.visibility= View.GONE

        }
    }

    inner class SearchWithLocateOrToolHolder(private val binding: ItemSearchWithLocateOrToolBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var pathImageLocation : String?=null
        private var pathImageUtensils : String?=null
        init {
            binding.ivLogo.resizeHeight(sizeIcon)
            pathImageLocation=PrefManager.getInstance(context).getString(PrefConst.PREF_PATH_IMAGE_LIST_LOCATION)
            pathImageUtensils=PrefManager.getInstance(context).getString(PrefConst.PREF_PATH_IMAGE_LIST_UTENSILS)
            binding.viewFooter.layoutParams.height = MyApplication.getInstance().getSizeWithScale(65.0)

        }


        fun bind(result: ObjectSearch.ResultSearchEntity,position: Int,isShowBottomNavi: Boolean) {
            if (result.content is ResponseSearchLocations.SearchLocationData){
                (result.content as ResponseSearchLocations.SearchLocationData).let {
                    binding.tvTitle.text = it.name
                    binding.tvContent.text = it.description
                    try {
                        if (it.getListImage()?.get(0) == null){
                            binding.ivLogo.setImageResource(R.drawable.ic_no_image)
                        }else binding.ivLogo.loadImageUrl(urlImage(it.getListImage()?.get(0),pathImageLocation))
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            if(result.content is ResponseListLocation.LocationData){
                (result.content as ResponseListLocation.LocationData).let {
                    binding.tvTitle.text = it.name
                    binding.tvContent.text = it.description
                    try {
                        if (it.getListImage()?.get(0) == null){
                            binding.ivLogo.setImageResource(R.drawable.ic_item_search_locate)
                        }else{
                            binding.ivLogo.loadImageUrl(urlImage(it.getListImage()?.get(0),pathImageLocation))
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            if (result.content is ResponseSearchUtensils.SearchUtensilsData){
                (result.content as ResponseSearchUtensils.SearchUtensilsData).let {
                    binding.tvTitle.text = it.name
                    try {
                        if (it.getListImage()?.get(0) == null){
                            binding.ivLogo.setImageResource(R.drawable.ic_item_search_tool)
                        }else
                            binding.ivLogo.loadImageUrl(urlImage(it.getListImage()?.get(0),pathImageUtensils))
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }


            if (result.content is ResponseListUtensils.UtensilsData){
                (result.content as ResponseListUtensils.UtensilsData).let {
                    binding.tvTitle.text=it.name
                    try {
                        if (it.getListImage()?.get(0) == null){
                            binding.ivLogo.setImageResource(R.drawable.ic_item_search_tool)
                        }else
                            binding.ivLogo.loadImageUrl(urlImage(it.getListImage()?.get(0),pathImageUtensils))
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            }
            binding.container.setSingleClick {
                onClickItemListener?.apply {
                    onClicked(result.content)
                }
            }

            if (isShowBottomNavi){
                if (position == listData.size-1 && listData.size>1) binding.viewFooter.visibility= View.VISIBLE
                else binding.viewFooter.visibility= View.GONE

                if (position == listResult.size-1 && listResult.size>1) binding.viewFooter.visibility= View.VISIBLE
                else binding.viewFooter.visibility= View.GONE
            }

        }
    }

    private fun fromWithUser(parent: ViewGroup): ItemSearchWithUserBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemSearchWithUserBinding.inflate(layoutInflater, parent, false)
    }

    private fun fromWithLocateOrTool(parent: ViewGroup): ItemSearchWithLocateOrToolBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemSearchWithLocateOrToolBinding.inflate(layoutInflater, parent, false)
    }


    override fun getItemViewType(position: Int): Int {
        return listResult[position].type
    }

    override fun getItemCount(): Int {
        return listResult.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchWithLocateOrToolHolder -> {
                holder.bind(listResult[position],position,isShowBottomNavi)
            }
            is SearchWithUserHolder -> {
                holder.bind(listResult[position],position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ObjectSearch.ResultSearchEntity.LOCATE -> {
                SearchWithLocateOrToolHolder(fromWithLocateOrTool(parent))
            }
            ObjectSearch.ResultSearchEntity.TOOL -> {
                SearchWithLocateOrToolHolder(fromWithLocateOrTool(parent))
            }
            ObjectSearch.ResultSearchEntity.USER -> {
                SearchWithUserHolder(fromWithUser(parent))
            }
            else -> {
                SearchWithLocateOrToolHolder(fromWithLocateOrTool(parent))
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList=listData
                } else {
                    val resultList = ArrayList<ObjectSearch.ResultSearchEntity>()
                    for (row in listData) {
                        when(row.type){
                            ObjectSearch.ResultSearchEntity.LOCATE -> {
                                (row.content as ResponseListLocation.LocationData).let {
                                    val covertString= convertToString(it.name)
                                    covertString?.let { name->
                                        if (name.contains(constraint.toString().toLowerCase(Locale.ROOT))) resultList.add(ObjectSearch.ResultSearchEntity(ObjectSearch.ResultSearchEntity.LOCATE,row.content))
                                    }
                                }
                            }
                            ObjectSearch.ResultSearchEntity.TOOL -> {
                                (row.content as ResponseListUtensils.UtensilsData).let {
                                    val covertString= convertToString(it.name)
                                    covertString?.let { name->
                                        if (name.contains(constraint.toString().toLowerCase(Locale.ROOT))) resultList.add(ObjectSearch.ResultSearchEntity(ObjectSearch.ResultSearchEntity.TOOL,row.content))
                                    }

                                }
                            }
                        }
                    }
                    filterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    results?.values.let {
                        listResult=it as ArrayList<ObjectSearch.ResultSearchEntity>
                    }
                    notifyDataSetChanged()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }
    fun convertToString(str: String?): String? {
        try {
            val temp: String = Normalizer.normalize(str, Normalizer.Form.NFD)
            val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
            return pattern.matcher(temp).replaceAll("").toLowerCase(Locale.ROOT).replace("đ", "d")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}