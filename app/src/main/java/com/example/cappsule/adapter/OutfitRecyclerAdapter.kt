package com.example.cappsule.adapter

import android.app.Activity
import com.example.cappsule.objects.Outfit
import com.example.cappsule.database.DatabaseHelperOutfit
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.cappsule.R
import android.widget.TextView
import java.util.ArrayList

class OutfitRecyclerAdapter(private val context: Activity, private val outfits: ArrayList<Outfit>, private val databaseHelperOutfit: DatabaseHelperOutfit, private val onClickListener: OnClickListener) : RecyclerView.Adapter<OutfitRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.outfit_recycler_item, parent, false), databaseHelperOutfit, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outfit = outfits[position]
        holder.imageViewLayer.setImageBitmap(outfit.layer)
        holder.imageViewTop.setImageBitmap(outfit.top)
        holder.imageViewBottom.setImageBitmap(outfit.bottom)
        holder.imageViewShoes.setImageBitmap(outfit.shoes)
        holder.textViewName.text = outfit.name
    }

    override fun getItemCount(): Int {
        return outfits.size
    }

    class ViewHolder(view: View, databaseHelperOutfit: DatabaseHelperOutfit, onClickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textViewName: TextView
        val imageViewLayer: ImageView
        val imageViewTop: ImageView
        val imageViewBottom: ImageView
        val imageViewShoes: ImageView
        private val buttonDelete: Button
        private val databaseHelperOutfit: DatabaseHelperOutfit
        private val onClickListener: OnClickListener
        override fun onClick(v: View) {
            onClickListener.onClickOutfitListener(adapterPosition)
        }

        init {
            textViewName = view.findViewById(R.id.outfit_item_name)
            imageViewLayer = view.findViewById(R.id.imageViewLayer)
            imageViewTop = view.findViewById(R.id.imageViewTop)
            imageViewBottom = view.findViewById(R.id.imageViewBottom)
            imageViewShoes = view.findViewById(R.id.imageViewShoes)
            buttonDelete = view.findViewById(R.id.delete_outfit)
            this.databaseHelperOutfit = databaseHelperOutfit
            this.onClickListener = onClickListener
            buttonDelete.setOnClickListener { onClickListener.onClickDeleteListener(adapterPosition) }
            view.setOnClickListener(this)
        }
    }

    interface OnClickListener {
        fun onClickDeleteListener(position: Int)
        fun onClickOutfitListener(position: Int)
    }
}