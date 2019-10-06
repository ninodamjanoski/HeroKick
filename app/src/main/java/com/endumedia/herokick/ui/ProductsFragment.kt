package com.endumedia.herokick.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
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
import com.endumedia.herokick.ui.widgets.HintAdapter
import com.endumedia.herokick.ui.widgets.HintSpinner
import javax.inject.Inject


/**
 * Created by Nino on 01.10.19
 */
class ProductsFragment : Fragment(), Injectable {

    private val list by lazy { view?.findViewById<RecyclerView>(R.id.list) }
    private val swipeRefresh by lazy { view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) }
    private val spSort by lazy { view?.findViewById<Spinner>(R.id.spinner_sort) }

    private lateinit var productsAdapter: ProductsAdapter

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
            productsAdapter.submitList(items)
        })

        model.networkState.observe(this, networkStateObserver)
        model.refreshState.observe(this, networkStateObserver)

        spSort?.run {
            val stringArray = resources.getStringArray(R.array.sort_spinner_values)
            val hintAdapter = HintAdapter(context, R.layout.product_list_view_row,
                R.string.product_sort, stringArray.asList(), false)
            HintSpinner(this, hintAdapter,
                HintSpinner.Callback<String> { i, t ->
                    hintAdapter.setSelectedTopPosition(i)
                    model.setSorting(i)
                }).init()
        }

        productsAdapter = ProductsAdapter { model.retry() }
        list?.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        list?.adapter = productsAdapter

        swipeRefresh?.setOnRefreshListener {
            model.refresh()
        }
    }

    private val networkStateObserver = Observer<NetworkState> { state ->
            productsAdapter.setNetworkState(state)
            if (state != NetworkState.LOADING && swipeRefresh?.isRefreshing ?: false) {
                swipeRefresh?.isRefreshing = false
            }
        }

}