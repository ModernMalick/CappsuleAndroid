package com.example.cappsule.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cappsule.R
import com.example.cappsule.database.DatabaseHelperArticle
import com.example.cappsule.fragment.Wardrobe
import com.example.cappsule.toaster

class ArticleEditDialog : DialogFragment() {
    @JvmField
    var position: Int? = null
    @JvmField
    var bitmap: Bitmap? = null
    @JvmField
    var type: String? = null
    @JvmField
    var warmth: String? = null
    @JvmField
    var wardrobe: Wardrobe? = null
    private var databaseHelperArticle: DatabaseHelperArticle? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.dialog_edit_article, null)
        val img = layout.findViewById<ImageView>(R.id.dialogEdit_Image)
        img.setImageBitmap(bitmap)
        val spinnerViewType = layout.findViewById<Spinner>(R.id.dialogEdit_Type_Spinner)
        val spinnerViewWarmth = layout.findViewById<Spinner>(R.id.dialogEdit_Warmth_Spinner)
        when (type) {
            requireContext().getString(R.string.article_type) -> spinnerViewType.setSelection(0)
            requireContext().getString(R.string.Layer) -> spinnerViewType.setSelection(1)
            requireContext().getString(R.string.Top) -> spinnerViewType.setSelection(2)
            requireContext().getString(R.string.Bottom) -> spinnerViewType.setSelection(3)
            requireContext().getString(R.string.Shoes) -> spinnerViewType.setSelection(4)
        }
        when (warmth) {
            requireContext().getString(R.string.article_warmth) -> spinnerViewWarmth.setSelection(0)
            requireContext().getString(R.string.light) -> spinnerViewWarmth.setSelection(1)
            requireContext().getString(R.string.Both) -> spinnerViewWarmth.setSelection(2)
            requireContext().getString(R.string.heavy) -> spinnerViewWarmth.setSelection(3)
        }
        val cancel = layout.findViewById<Button>(R.id.dialogEdit_Cancel)
        val save = layout.findViewById<Button>(R.id.dialogEdit_Save)
        cancel.setOnClickListener { dialog?.cancel() }
        databaseHelperArticle = DatabaseHelperArticle(context)
        save.setOnClickListener {
            val typeSaved = spinnerViewType.selectedItem.toString()
            val warmthSaved = spinnerViewWarmth.selectedItem.toString()
            databaseHelperArticle!!.updateType(position, typeSaved)
            databaseHelperArticle!!.updateWarmth(position, warmthSaved)
            wardrobe!!.populateDataSet()
            dialog?.cancel()
            toaster(context, requireContext().getString(R.string.ArticleUpdated))
        }
        builder.setView(layout)
        return builder.create()
    }
}