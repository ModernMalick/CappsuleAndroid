package com.example.cappsule.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cappsule.R
import com.example.cappsule.database.DatabaseHelperOutfit
import com.example.cappsule.fragment.Outfits
import com.example.cappsule.toaster

class OutfitEditDialog : DialogFragment() {
    var position: Int? = null
    var name: String? = null
    var outfits: Outfits? = null
    private var databaseHelperOutfit: DatabaseHelperOutfit? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.dialog_edit_outfit, null)
        val editText = layout.findViewById<EditText>(R.id.edit_outfit_name)
        editText.hint = name
        editText.setAutofillHints(name)
        val cancel = layout.findViewById<Button>(R.id.dialogEditOutfit_Cancel)
        val save = layout.findViewById<Button>(R.id.dialogEditOutfit_Save)
        cancel.setOnClickListener { dialog?.cancel() }
        databaseHelperOutfit = DatabaseHelperOutfit(context)
        save.setOnClickListener {
            if (editText.text.toString() != "") {
                toaster(context, requireContext().getString(R.string.OutfitNameUpdated))
                databaseHelperOutfit!!.updateName(position, editText.text.toString())
                outfits!!.outfits.clear()
                outfits!!.populateDataSet()
                dialog?.cancel()
            } else {
                editText.error = requireContext().getString(R.string.EnterName)
            }
        }
        builder.setView(layout)
        return builder.create()
    }
}