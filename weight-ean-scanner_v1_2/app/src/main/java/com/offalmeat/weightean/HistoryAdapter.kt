package com.offalmeat.weightean

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.offalmeat.weightean.databinding.ItemHistoryBinding

data class HistoryRow(
    val ts: String,
    val code: String,
    val prefix: String,
    val plu: String,
    val weight: String?,
    val price: String?
)

class HistoryAdapter(private var items: List<HistoryRow>) :
    RecyclerView.Adapter<HistoryAdapter.VH>() {

    class VH(val b: ItemHistoryBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.b.tvTs.text = it.ts
        holder.b.tvCode.text = it.code
        holder.b.tvInfo.text = buildString {
            append("Префикс: ${it.prefix} | PLU: ${it.plu}")
            if (!it.weight.isNullOrBlank()) append(" | Вес: ${it.weight} кг")
            if (!it.price.isNullOrBlank()) append(" | Цена: ${it.price}")
        }
    }

    fun update(newItems: List<HistoryRow>) {
        items = newItems
        notifyDataSetChanged()
    }
}
