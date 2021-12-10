package com.example.cappsule.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cappsule.R
import com.example.cappsule.toaster


class MoreDialog : DialogFragment()  {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val layoutInflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.dialog_more, null)
        val cancel = layout.findViewById<ImageButton>(R.id.dialog_more_close)
        cancel.setOnClickListener {
            dialog?.cancel()
        }
        val more = layout.findViewById<TextView>(R.id.textViewMore)
        more.setOnClickListener{
            val webpage: Uri = Uri.parse("https://mn10games.github.io")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            requireActivity().startActivity(intent)
            dialog?.cancel()
        }
        val rate = layout.findViewById<TextView>(R.id.textViewRate)
        rate.setOnClickListener{
            toaster(requireContext(), "RATED")
            dialog?.cancel()
        }

        builder.setView(layout)
        return builder.create()
    }
}