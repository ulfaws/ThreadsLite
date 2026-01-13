package com.example.threadslite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var dbRef: DatabaseReference
    private val threadList = ArrayList<ThreadModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        listView = view.findViewById(R.id.listViewThreads)
        dbRef = FirebaseDatabase.getInstance().getReference("threads")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded || context == null) return

                threadList.clear()
                for (data in snapshot.children) {
                    val thread = data.getValue(ThreadModel::class.java)
                    if (thread != null) {
                        threadList.add(thread)
                    }
                }

                context?.let { safeContext ->
                    val adapter = ThreadAdapter(safeContext, threadList)
                    listView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        listView.setOnItemClickListener { _, _, i, _ ->
            context?.let { safeContext ->
                val intent = Intent(safeContext, AddEditThreadActivity::class.java)
                intent.putExtra("THREAD_ID", threadList[i].id)
                intent.putExtra("THREAD_CONTENT", threadList[i].content)
                startActivity(intent)
            }
        }

        return view
    }
}