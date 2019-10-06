package com.endumedia.herokick.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.endumedia.herokick.R
import com.endumedia.herokick.di.Injectable
import com.endumedia.herokick.repository.NetworkState
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsFragment : Fragment(), Injectable {

    private val list by lazy { view?.findViewById<RecyclerView>(R.id.list) }
    private val swipeRefresh by lazy { view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) }

    private lateinit var adapter: ProductsAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(ProductsViewModel::class.java) }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_products_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.products.observe(this, Observer { items ->
            adapter.submitList(items)
        })

        model.networkState.observe(this, Observer { state ->
            adapter.setNetworkState(state)
            if (state != NetworkState.LOADING && swipeRefresh?.isRefreshing ?: false) {
                swipeRefresh?.isRefreshing = false
            }
        })

        adapter = ProductsAdapter (context!!) { model.retry() }
        list?.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        list?.adapter = adapter

        swipeRefresh?.setOnRefreshListener {
            model.refresh()
        }
    }
}