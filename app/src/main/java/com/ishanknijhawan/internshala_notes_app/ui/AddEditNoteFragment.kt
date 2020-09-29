package com.ishanknijhawan.internshala_notes_app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ishanknijhawan.internshala_notes_app.R
import com.ishanknijhawan.internshala_notes_app.entity.Note
import kotlinx.android.synthetic.main.fragment_add_edit_note.*

class AddEditNoteFragment : Fragment() {
    private lateinit var mView: View
    private lateinit var userEmail: String
    private lateinit var action: String
    private lateinit var updatedNote: Note
    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        mainFragmentViewModel = ViewModelProvider(this).get(MainFragmentViewModel::class.java)
        userEmail = arguments?.getString("email")!!
        action = arguments?.getString("action")!!
        updatedNote = (arguments?.getSerializable("note") as Note?)!!

        if (action == "edit"){
            btn_save_note.text = "Update"
            et_note_title.setText(updatedNote.title)
            et_note_desc.setText(updatedNote.description)
        } else {
            btn_save_note.text = "Save"
        }

        btn_save_note.setOnClickListener {
            saveNote()
        }

        iv_back.setOnClickListener {
            handleBackButton()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackButton()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    private fun handleBackButton() {
        if (et_note_desc.text.isEmpty() and et_note_title.text.isEmpty()) {
            mView.findNavController().popBackStack()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save Note ?")
                .setMessage("Do you want to save this note ?")
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    dialog.dismiss()
                    mView.findNavController().popBackStack()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    dialog.dismiss()
                    saveNote()
                }
                .show()
        }
    }

    private fun saveNote() {
        val noteTitle = et_note_title.text.toString()
        val noteDesc = et_note_desc.text.toString()
        val note = Note(
            title = noteTitle,
            description = noteDesc,
            userEmail = userEmail
        )

        if (noteTitle.isEmpty() and noteDesc.isEmpty()) {
            Toast.makeText(requireContext(), "Empty note discarded", Toast.LENGTH_SHORT).show()
        } else {
            if (action == "edit"){
                updatedNote.title = noteTitle
                updatedNote.description = noteDesc
                mainFragmentViewModel.updateNote(updatedNote)
            } else {
                mainFragmentViewModel.insertNote(note)
            }
        }
        mView.findNavController().popBackStack()
    }
}