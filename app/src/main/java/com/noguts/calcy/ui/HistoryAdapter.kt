package com.noguts.calcy.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noguts.calcy.R
import com.noguts.calcy.data.CalculationHistoryEntity
import java.text.DateFormat
import java.util.Date

class HistoryAdapter(
    private val onItemClicked: (CalculationHistoryEntity) -> Unit
) : ListAdapter<CalculationHistoryEntity, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view, onItemClicked)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(
        itemView: View,
        private val onItemClicked: (CalculationHistoryEntity) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvExpression: TextView = itemView.findViewById(R.id.tvHistoryExpression)
        private val tvResult: TextView = itemView.findViewById(R.id.tvHistoryResult)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tvHistoryTimestamp)

        fun bind(item: CalculationHistoryEntity) {
            tvExpression.text = item.expression
            tvResult.text = item.result
            tvTimestamp.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(item.createdAt))

            itemView.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<CalculationHistoryEntity>() {
            override fun areItemsTheSame(
                oldItem: CalculationHistoryEntity,
                newItem: CalculationHistoryEntity
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: CalculationHistoryEntity,
                newItem: CalculationHistoryEntity
            ): Boolean = oldItem == newItem
        }
    }
}
