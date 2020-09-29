package com.ishanknijhawan.internshala_notes_app.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.system.Os.accept
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ishanknijhawan.internshala_notes_app.MainActivity
import com.ishanknijhawan.internshala_notes_app.R
import com.ishanknijhawan.internshala_notes_app.adapter.NoteAdapter
import com.ishanknijhawan.internshala_notes_app.adapter.OnItemClickListener
import com.ishanknijhawan.internshala_notes_app.entity.Note
import com.ishanknijhawan.internshala_notes_app.repository.NoteRepository
import kotlinx.android.synthetic.main.fragment_notes.*

class NotesFragment : Fragment(), OnItemClickListener {
    private lateinit var mView: View
    private lateinit var userEmail: String
    private lateinit var getAllNotes: LiveData<List<Note>>
    private lateinit var allNotes: List<Note>
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var mainFragmentViewModel: MainFragmentViewModel
    private lateinit var prefs: SharedPreferences
    private var gridLayout: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        prefs = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)!!
        val darkTheme = prefs.getBoolean(getString(R.string.dark_theme), false)
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        gridLayout = prefs.getBoolean(getString(R.string.change_layout), false)
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        userEmail = arguments?.getString("email")!!
        tv_email_title.text = userEmail

        mainFragmentViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        getAllNotes = mainFragmentViewModel.getAllNotes(userEmail)

        getAllNotes.observe(
            requireActivity(),
            Observer {
                allNotes = getAllNotes.value!!
                if (allNotes.isEmpty()) {
                    tv_appear_notes?.visibility = View.VISIBLE
                    rv_notes?.visibility = View.GONE
                } else {
                    tv_appear_notes?.visibility = View.GONE
                    rv_notes?.visibility = View.VISIBLE
                    if (gridLayout) {
                        rv_notes?.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                    } else {
                        rv_notes?.layoutManager = LinearLayoutManager(requireContext())
                    }
                    noteAdapter = NoteAdapter(allNotes, requireContext(), this)
                    rv_notes?.adapter = noteAdapter
                }
            }
        )

        toolbar.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener,
            androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.item_logout -> {
                        showAlertDialog()
                    }
                    R.id.item_theme -> {
                        changeTheme()
                    }
                    R.id.item_layout -> {
                        changeLayout()
                    }
                }
                return true
            }
        })

        fabAddNote.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("email", userEmail)
            bundle.putString("action", "add")
            bundle.putSerializable("note", Note (title = "", description = "", userEmail = userEmail))
            AddEditNoteFragment().arguments = bundle
            mView.findNavController().navigate(R.id.action_notesFragment_to_addEditNoteFragment, bundle)
        }
    }

    private fun changeLayout() {
        if (!gridLayout){
            gridLayout = true
            rv_notes.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            noteAdapter.notifyDataSetChanged()
            with (prefs.edit()) {
                putBoolean(getString(R.string.change_layout), gridLayout)
                commit()
            }
        } else {
            gridLayout = false
            rv_notes.layoutManager = LinearLayoutManager(requireContext())
            noteAdapter.notifyDataSetChanged()
            with (prefs.edit()) {
                putBoolean(getString(R.string.change_layout), gridLayout)
                commit()
            }
        }
    }

    private fun changeTheme() {
        when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK){
            Configuration.UI_MODE_NIGHT_NO -> {
                with (prefs.edit()) {
                    putBoolean(getString(R.string.dark_theme), true)
                    commit()
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                with (prefs.edit()) {
                    putBoolean(getString(R.string.dark_theme), false)
                    commit()
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout from Notes app ?")
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient =
                    GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut()

                mView.findNavController().navigate(R.id.action_notesFragment_to_loginFragment)
            }
            .show()
    }

    override fun onItemClicked(position: Int) {
        val items = arrayOf("Edit", "Delete")

        MaterialAlertDialogBuilder(requireContext())
            .setItems(items) { dialog, i ->
                if (i == 0){
                    val bundle = Bundle()
                    bundle.putString("email", userEmail)
                    bundle.putString("action", "edit")
                    bundle.putSerializable("note", allNotes[position])
                    AddEditNoteFragment().arguments = bundle
                    mView.findNavController().navigate(R.id.action_notesFragment_to_addEditNoteFragment, bundle)
                } else {
                    mainFragmentViewModel.deleteNote(allNotes[position])
                }
            }
            .show()
    }
}