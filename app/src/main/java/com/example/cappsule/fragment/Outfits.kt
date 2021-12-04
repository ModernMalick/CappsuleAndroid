package com.example.cappsule.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cappsule.R
import com.example.cappsule.adapter.OutfitRecyclerAdapter
import com.example.cappsule.database.DatabaseHelperOutfit
import com.example.cappsule.dialog.DeleteDialog
import com.example.cappsule.dialog.OutfitEditDialog
import com.example.cappsule.getImage
import com.example.cappsule.objects.Outfit
import com.example.cappsule.objects.Outfit.OutfitNameComparator
import com.example.cappsule.toaster
import kotlinx.coroutines.*

class Outfits : Fragment(), OutfitRecyclerAdapter.OnClickListener {
    private lateinit var databaseHelperOutfit: DatabaseHelperOutfit
    private lateinit var recyclerview: RecyclerView
    private lateinit var outfitRecyclerAdapter: OutfitRecyclerAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    val outfits = ArrayList<Outfit>()
    private lateinit var imageViewEmpty: ImageView
    private lateinit var textViewEmpty: TextView
    private lateinit var textViewEmpty2: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var frameOutfit: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelperOutfit = DatabaseHelperOutfit(context)
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outfits, container, false)
        frameOutfit = view.findViewById(R.id.frame_outfit)
        progressBar = requireActivity().requireViewById(R.id.progressBar2)
        imageViewEmpty = view.findViewById(R.id.imageViewEmpty2)
        textViewEmpty = view.findViewById(R.id.textViewEmpty2)
        textViewEmpty2 = view.findViewById(R.id.textViewEmpty4)
        recyclerview = view.findViewById(R.id.outfit_recycler)
        layoutManager = GridLayoutManager(context, 2)
        recyclerview.layoutManager = layoutManager
        outfitRecyclerAdapter = OutfitRecyclerAdapter(activity!!, outfits, databaseHelperOutfit, this)
        recyclerview.adapter = outfitRecyclerAdapter

        return view
    }

    @Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            frameOutfit.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            populateDataSet()
            delay(500)
            progressBar.visibility = View.GONE
            frameOutfit.visibility = View.VISIBLE
        }
    }

    fun populateDataSet() {
        outfits.clear()
        val cursor = databaseHelperOutfit.data
        while (cursor.moveToNext()) {
            val outfit = Outfit(
                cursor.getString(0),
                getImage(cursor.getBlob(1)),
                getImage(cursor.getBlob(2)),
                getImage(cursor.getBlob(3)),
                getImage(cursor.getBlob(4)),
                cursor.getInt(5)
            )
            outfits.add(outfit)
            outfits.sortWith(OutfitNameComparator())
            outfitRecyclerAdapter.notifyDataSetChanged()
        }
        if (outfitRecyclerAdapter.itemCount == 0) {
            imageViewEmpty.visibility = View.VISIBLE
            textViewEmpty.visibility = View.VISIBLE
            textViewEmpty2.visibility = View.VISIBLE
        } else {
            imageViewEmpty.visibility = View.GONE
            textViewEmpty.visibility = View.GONE
            textViewEmpty2.visibility = View.GONE
        }
    }

    override fun onClickDeleteListener(position: Int) {
        val deleteDialog = DeleteDialog()
        deleteDialog.position = position
        deleteDialog.outfits = this
        deleteDialog.show(childFragmentManager, deleteDialog.tag)
    }

    fun actualDeletion(position: Int) {
        val rightpos = outfits[position].id
        databaseHelperOutfit.deleteOutfit(rightpos)
        outfits.removeAt(position)
        outfitRecyclerAdapter.notifyDataSetChanged()
        populateDataSet()
        toaster(context, resources.getString(R.string.OutfitDeleted))
    }

    override fun onClickOutfitListener(position: Int) {
        val rightpos = outfits[position].id
        val cursor = databaseHelperOutfit.data
        var name: String? = ""
        while (cursor.moveToNext()) {
            if (cursor.getInt(5) == rightpos) {
                name = cursor.getString(0)
            }
        }
        val outfitEditDialog = OutfitEditDialog()
        outfitEditDialog.position = rightpos
        outfitEditDialog.name = name
        outfitEditDialog.outfits = this
        outfitEditDialog.show(childFragmentManager, "OutfitEditDialog")
    }
}