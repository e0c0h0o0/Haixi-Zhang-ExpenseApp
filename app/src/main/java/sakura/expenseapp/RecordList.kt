package sakura.expenseapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sakura.expenseapp.databinding.LayoutRecordCardBinding
import java.text.SimpleDateFormat
import java.util.Date


open class RecordCardHolder(private val binding: LayoutRecordCardBinding, val context: Context): ViewHolder(binding.root) {

    fun bind(record: Record) {
        binding.uAmount.text = record.amount.toString()
        binding.uCategory.text = Global.CATEGORY[record.category]
        binding.uDate.text = SimpleDateFormat("yy-MM-dd HH:mm:ss").format(Date(record.date))

    }

}

class RecordListAdapter(private var records: List<Record>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return RecordCardHolder(LayoutRecordCardBinding.inflate(inflater, parent, false), context)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val record = records[position]

        (holder as RecordCardHolder).bind(record)
    }

    fun updateData(newRecords: List<Record>) {
        records = newRecords
        notifyDataSetChanged()
    }

}