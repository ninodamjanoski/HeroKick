package com.endumedia.herokick.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.endumedia.herokick.R
import com.endumedia.herokick.di.Injectable
import com.endumedia.herokick.util.CustomNavOptions
import com.endumedia.herokick.vo.Product
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.view_details_row.view.*
import javax.inject.Inject


/**
 * Created by Nino on 20.11.19
 */
class DetailsFragment : Fragment(), Injectable {

    private val toolBar by lazy { view?.findViewById<Toolbar>(R.id.toolbar) }
    private val tvName by lazy { view?.findViewById<TextView>(R.id.tvName) }
    private val vContainer by lazy { view?.findViewById<LinearLayout>(R.id.container) }

    private val inflater by lazy { LayoutInflater.from(context) }

    companion object {
        const val ITEM = "item"
    }


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val model by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(ItemDetailsViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        setHasOptionsMenu(true)
        toolBar?.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val item = arguments?.get(ITEM) as Product

        // Load cached data
        model.setCachedItem(item)
        initBackPressedListener()

        // Fill details with latest data silently from backend
        model.getItemById(item.id)

        model.itemLiveData
            .observe(this, androidx.lifecycle.Observer {
                fillDetails(it)
            })
    }


    /**
     * Using a scroll view, to support smaller screen sizes
     */
    private fun fillDetails(item: Product) {

        tvName?.apply {
            text = item.name
        }
        item.size?.let {
            addRow(R.string.sizeLabel, it)
        }
        item.brandName?.let {
            addRow(R.string.brandNameLabel, it)
        }
        item.servingSize?.let {
            addRow(R.string.servingSizeLabel, it)
        }
        item.servingsPerContainer?.let {
            addRow(R.string.servingsPerContainerLabel, it)
        }
        item.calories?.let {
            addRow(R.string.caloriesLabel, it)
        }
        item.fatCalories?.let {
            addRow(R.string.fatCaloriesLabel, it)
        }
        item.protein?.let {
            addRow(R.string.proteinLabel, it)
        }
        item.fat?.let {
            addRow(R.string.fatLabel, it)
        }
        item.sugars?.let {
            addRow(R.string.sugarsLabel, it)
        }
    }

    private fun addRow(@StringRes label: Int, value: String) {
        val row = inflater.inflate(R.layout.view_details_row, vContainer, false)
        row.tvLabel.text = getString(label)
        row.tvValue.text = value
        container.addView(row)
    }

    /**
     * Back pressed callback
     */
    private fun initBackPressedListener() {
        requireActivity().onBackPressedDispatcher
            .addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    remove()
                    val navController = findNavController()
                    navController.popBackStack()
                    navController.navigate(R.id.productsFragment,
                        null, CustomNavOptions.down().build())
                }
            })
    }


}