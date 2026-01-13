package com.example.threadslite

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ThreadAdapter(
    context: Context,
    private val list: List<ThreadModel>
) : ArrayAdapter<ThreadModel>(context, 0, list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_thread, parent, false)

        val tvAuthor = view.findViewById<TextView>(R.id.tvAuthor)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)

        val item = list[position]

        tvAuthor.text = item.author
        tvContent.text = item.content

        val currentUser = FirebaseAuth.getInstance().currentUser

        view.setOnClickListener {
            if (currentUser != null && currentUser.uid == item.uid) {
                val intent = Intent(context, AddEditThreadActivity::class.java)
                intent.putExtra("THREAD_ID", item.id)
                intent.putExtra("THREAD_CONTENT", item.content)
                context.startActivity(intent)
            }
        }

        view.setOnLongClickListener {
            if (currentUser != null && currentUser.uid == item.uid) {
                showDeleteDialog(item)
            } else {
                Toast.makeText(context, "Hanya pemilik yang bisa menghapus", Toast.LENGTH_SHORT).show()
            }
            true
        }

        return view
    }

    private fun showDeleteDialog(item: ThreadModel) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Postingan")
            .setMessage("Apakah kamu yakin ingin menghapus thread ini?")
            .setPositiveButton("Hapus") { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("threads").child(item.id)
                dbRef.removeValue().addOnSuccessListener {
                    Toast.makeText(context, "Berhasil dihapus!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}