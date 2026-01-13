package com.example.threadslite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_data, container, false)
        view.findViewById<FloatingActionButton>(R.id.btnAdd).setOnClickListener {
            val intent = Intent(requireContext(), AddEditThreadActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}