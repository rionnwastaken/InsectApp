package com.example.randominsect

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etInsectName = view.findViewById<EditText>(R.id.et_insect_name)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        btnSave.setOnClickListener {
            val name = etInsectName.text.toString().trim()
            if (name.isNotEmpty()) {
                (activity as? MainActivity)?.insectList?.add(name)
                etInsectName.text.clear()
                Toast.makeText(requireContext(), "Saved $name!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
