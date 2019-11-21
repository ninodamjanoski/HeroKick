package com.endumedia.herokick.ui.productslist

import android.os.Bundle
import android.view.*
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.endumedia.herokick.R
import com.endumedia.herokick.di.Injectable
import com.endumedia.herokick.repository.NetworkState
import com.endumedia.herokick.ui.ItemCLickListener
import com.endumedia.herokick.ui.details.DetailsFragment
import com.endumedia.herokick.ui.widgets.HintAdapter
import com.endumedia.herokick.ui.widgets.HintSpinner
import com.endumedia.herokick.util.CustomNavOptions
import com.endumedia.herokick.vo.Product
import javax.inject.Inject
import kotlin.jvm.internal.Intrinsics


/**
 * Created by Nino on 01.10.19
 */
class ProductsFragment : Fragment(), Injectable {

    private lateinit var hintAdapter: HintAdapter<String>
    private val list by lazy { view?.findViewById<RecyclerView>(R.id.list) }
    private val swipeRefresh by lazy { view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh) }
    private val spSort by lazy { view?.findViewById<Spinner>(R.id.spinner_sort) }
    private var searchView: SearchView? = null
    private val toolBar by lazy { view?.findViewById<Toolbar>(R.id.toolbar) }

    private lateinit var productsAdapter: ProductsAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(ProductsViewModel::class.java) }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
        setupSearchView(menu)
    }

    private fun setupSearchView(menu: Menu) {
        val searchMenuItem = menu.findItem(R.id.action_seach)
        searchView = searchMenuItem.actionView as SearchView
        searchView?.queryHint = getString(R.string.name)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                submitDataRequest()
                return false
            }
        })
    }

    private fun submitDataRequest() {
        model.submitDataRequest(searchView?.query.toString(),
            hintAdapter.selectedTopPosition)
    }

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_products_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        setHasOptionsMenu(true)

        model.products.observe(this, Observer { items ->
            productsAdapter.submitList(items)
        })

        model.networkState.observe(this, NetworkStateObserver())
        model.refreshState.observe(this, NetworkStateObserver())

        spSort?.run {
            initFilterSpinner(this,
                R.string.product_sort, R.array.sort_spinner_values)
        }

        productsAdapter = ProductsAdapter(object : ItemCLickListener {
            override fun onCLicked(item: Any) {
                val bundle = Bundle().apply {
                    putParcelable(DetailsFragment.ITEM, item as Product)
                }
                findNavController()
                    .navigate(R.id.detailsFragment,
                        bundle, CustomNavOptions.up().build())
            }
        }) { model.retry() }
        list?.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        list?.adapter = productsAdapter

        swipeRefresh?.setOnRefreshListener {
            model.refresh()
        }
    }

    inner class NetworkStateObserver : Observer<NetworkState> {
        override fun onChanged(state: NetworkState?) {
            productsAdapter.setNetworkState(state)
            if (state != NetworkState.LOADING && swipeRefresh?.isRefreshing ?: false) {
                swipeRefresh?.isRefreshing = false
            }
        }
    }

    private fun initFilterSpinner(spinner: Spinner, res: Int, arrayRes: Int) {
        val stringArray = resources.getStringArray(arrayRes)
        hintAdapter = HintAdapter(context, R.layout.product_list_view_row, res, stringArray.asList(), true)
        HintSpinner(spinner, hintAdapter,
            HintSpinner.Callback<String> { i, t ->
                hintAdapter.selectedTopPosition = i
                submitDataRequest()
            }).init()
    }
}