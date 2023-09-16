package com.example.notes.notes

import ConvertToDate
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.notes.NotesViewModel
import com.example.notes.R
import com.example.notes.database.Note
import com.example.notes.databinding.FragmentNewNoteBinding
import kotlinx.coroutines.launch
import java.util.*


class NewNoteFragment : Fragment() {
    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var mUserEmail: String
    private lateinit var preferences: SharedPreferences
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var mView: View
    private lateinit var mOldNote: Note


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        init()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun init() {
        preferences = context?.getSharedPreferences("com.example.notes", Context.MODE_PRIVATE)!!
        mUserEmail = preferences.getString("email", "none").toString()
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        binding.noteTime.text = ConvertToDate().convertTimeStampToDate(System.currentTimeMillis())


        val toEdit: Boolean = arguments!!.getBoolean("edit")
        if (toEdit) {
            mOldNote = (arguments?.getSerializable("note") as Note?)!!
            binding.noteTitle.setText(mOldNote.title)
            binding.noteContent.setText(mOldNote.note_content)
            binding.noteTime.text = mOldNote.date
            binding.saveNoteBtn.text = "Update"

            binding.deleteNoteBtn.visibility = View.VISIBLE
                binding.deleteNoteBtn.setOnClickListener {
                    notesViewModel.let { viewModel ->
                        lifecycleScope.launch {
                            viewModel.deleteNote(mOldNote)
                        }
                    }
                    Toast.makeText(context, "Note is Deleted", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
                }

        }

        binding.saveNoteBtn.setOnClickListener {
            if (checkIfEmpty()) {
                if (toEdit) {
                    mOldNote.title = binding.noteTitle.text.toString()
                    mOldNote.note_content = binding.noteContent.text.toString()
                    mOldNote.date =
                        ConvertToDate().convertTimeStampToDate(System.currentTimeMillis())
                    lifecycleScope.launch { notesViewModel.updateNote(mOldNote) }
                    Toast.makeText(context, "Note is Updated", Toast.LENGTH_SHORT).show()
                    goToNotesFragment()
                } else {
                    val newNote = Note(
                        title = binding.noteTitle.text.toString(),
                        note_content = binding.noteContent.text.toString(),
                        date = ConvertToDate().convertTimeStampToDate(System.currentTimeMillis()),
                        card_color = getRandomColor(),
                        email = mUserEmail
                    )

                    lifecycleScope.launch { notesViewModel.insertNote(newNote) }
                    Toast.makeText(context, "Note is Added", Toast.LENGTH_SHORT).show()
                    mView.findNavController().popBackStack()
                    hideSoftKeyboard()
                }
            }
        }
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToNotesFragment()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        binding.backBtn.setOnClickListener { goToNotesFragment() }
    }

    private fun hideSoftKeyboard() {
        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun checkIfEmpty(): Boolean {
        return if (binding.noteTitle.length() == 0 || binding.noteContent.length() == 0) {
            Toast.makeText(context, "Field cannot be empty", Toast.LENGTH_SHORT).show()
            false
        } else true
    }

    private fun getRandomColor(): Int {
        val androidColors = resources.getIntArray(R.array.card_colors)
        return androidColors[Random().nextInt(androidColors.size)]
    }

    private fun goToNotesFragment() {
        hideSoftKeyboard()
        mView.findNavController().navigate(R.id.action_newNoteFragment_to_notesFragment)
    }

}