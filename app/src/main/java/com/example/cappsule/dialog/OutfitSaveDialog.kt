package com.example.cappsule.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cappsule.R
import com.example.cappsule.database.DatabaseHelperOutfit
import com.example.cappsule.fragment.Home
import com.example.cappsule.getBytes
import com.example.cappsule.toaster

class OutfitSaveDialog : DialogFragment() {
    @JvmField
    var layer: Bitmap? = null
    @JvmField
    var top: Bitmap? = null
    @JvmField
    var bottom: Bitmap? = null
    @JvmField
    var shoes: Bitmap? = null
    @JvmField
    var home: Home? = null
    private var databaseHelperOutfit: DatabaseHelperOutfit? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.dialog_new_outfit, null)
        val editText = layout.findViewById<EditText>(R.id.save_outfit_name)
        val cancel = layout.findViewById<Button>(R.id.dialogEdit_Cancel)
        val save = layout.findViewById<Button>(R.id.dialogEdit_Save)
        cancel.setOnClickListener {
            dialog?.cancel()
        }
        databaseHelperOutfit = DatabaseHelperOutfit(context)
        save.setOnClickListener {
            if (editText.text.toString() != "") {
                toaster(context, requireContext().getString(R.string.ArticleSaved))
                databaseHelperOutfit!!.addOutfit(
                    editText.text.toString(), getBytes(layer), getBytes(top), getBytes(bottom),
                    getBytes(shoes)
                )
                dialog?.cancel()
            } else {
                editText.error = requireContext().getString(R.string.EnterName)
            }
        }
        builder.setView(layout)
        return builder.create()
    }
}