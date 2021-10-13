package brite.outdoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R
import brite.outdoor.data.api_entities.response.ResponseReport
import brite.outdoor.databinding.ItemReportBinding
import brite.outdoor.BR
import brite.outdoor.utils.setSingleClick

class AdapterReport(
        private val listItem:ArrayList<ResponseReport.Report>,
        private val context:Context,
        val itemClickListener:(ResponseReport.Report) -> Unit={_:ResponseReport.Report->}):RecyclerView.Adapter<AdapterReport.ReportViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = DataBindingUtil.inflate<ItemReportBinding>(LayoutInflater.from(context), R.layout.item_report,parent,false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val item = listItem[position]
        bindView(binding = holder.binding,item)
        holder.binding.setVariable(BR.item,item)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = listItem.size

    private fun bindView(binding: ItemReportBinding,item: ResponseReport.Report){
        binding.apply {
            root.setSingleClick {
                itemClickListener(item)
            }
        }
    }
    class ReportViewHolder(val binding:ItemReportBinding)  : RecyclerView.ViewHolder(binding.root)

}