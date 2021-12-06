package com.example.cappsule.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cappsule.R
import com.example.cappsule.fragment.Outfits
import com.example.cappsule.fragment.Wardrobe

class DeleteDialog : DialogFragment() {
    @JvmField
    var position = 0
    @JvmField
    var wardrobe: Wardrobe? = null
    @JvmField
    var outfits: Outfits? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.dialog_delete_confirmation, null)
        val cancel = layout.findViewById<Button>(R.id.dialogDeleteCancel)
        val save = layout.findViewById<Button>(R.id.dialogDeleteOk)
        cancel.setOnClickListener { dialog?.cancel() }
        save.setOnClickListener { onClick() }
        builder.setView(layout)
        return builder.create()
    }

    private fun onClick() {
        if (wardrobe != null) {
            wardrobe!!.actualDeletion(position)
        } else {
            outfits!!.actualDeletion(position)
        }
        dialog?.cancel()
    }
}