package com.endumedia.herokick.ui.productslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.endumedia.herokick.R
import com.endumedia.herokick.repository.NetworkState
import com.endumedia.herokick.ui.ItemCLickListener
import com.endumedia.herokick.vo.Product
import kotlinx.android.synthetic.main.product_item.view.*


/**
 * Created by Nino on 02.10.19
 */
class ProductsAdapter(private val clickListener: ItemCLickListener,
                      private val retryCallback: () -> Unit)
: PagedListAdapter<Product, RecyclerView.ViewHolder>(Product_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.product_item -> NoteViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.product_item, parent, false))
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(
                parent,
                retryCallback
            )
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.product_item -> (holder as NoteViewHolder).bind(getItem(position))
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder)
                .bindTo(networkState)
        }
    }

    override fun submitList(pagedList: PagedList<Product>?) {
        super.submitList(pagedList)
    }

    private var latestList: PagedList<Product>? = null

    override fun onCurrentListChanged(
        previousList: PagedList<Product>?,
        currentList: PagedList<Product>?) {
        super.onCurrentListChanged(previousList, currentList)
        latestList = currentList
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.product_item
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    @Synchronized fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    inner class NoteViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        fun bind(item: Product?) {
            if (item == null) return

            itemView.setOnClickListener {
                clickListener.onCLicked(item) }

            item.name?.let {
                itemView.tvName.text = it
            }
            item.brandName?.let {
                itemView.tvBrand.text = it
            }
            item.size?.let {
                itemView.tvSize.text = it
            }
        }
    }

    companion object {
        val Product_COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id

            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id

            override fun getChangePayload(oldItem: Product, newItem: Product): Any? {
                return null
            }
        }
    }
}