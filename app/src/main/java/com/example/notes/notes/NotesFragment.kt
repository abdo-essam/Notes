package com.example.notes.notes

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notes.NotesViewModel
import com.example.notes.R
import com.example.notes.adapter.NotesAdapter
import com.example.notes.database.Note
import com.example.notes.databinding.CustomToolbarBinding
import com.example.notes.databinding.FragmentNotesBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class NotesFragment : Fragment(), NotesAdapter.OnItemClickListener {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding

    // Declare the included layout's binding variable
    private var customToolbarBinding: CustomToolbarBinding? = null
    private lateinit var mView: View
    private lateinit var preferences: SharedPreferences
    private lateinit var mUserEmail: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var getNotes: LiveData<List<Note>>
    private lateinit var Notes: List<Note>
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val view = binding?.root

        // Inflate the included custom_toolbar layout and access its binding
        customToolbarBinding = CustomToolbarBinding.bind(view!!.findViewById(R.id.customToolBar))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        init()
        fetchAllNotes()
        binding?.addNote?.setOnClickListener {
            newNoteFragment()
        }

        customToolbarBinding?.logOutBtn?.setOnClickListener {
            signOut()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
    }

    private fun init() {
        preferences = context?.getSharedPreferences(
            getString(R.string.preference_name),
            Context.MODE_PRIVATE
        )!!
        mUserEmail = preferences.getString("email", "none").toString()
        firebaseAuth = FirebaseAuth.getInstance()
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
    }

    private fun fetchAllNotes() {
        getNotes = notesViewModel.getNotes(mUserEmail)
        getNotes.observe(requireActivity()) {
            // todo error here while add notes
            Notes = getNotes?.value!!
            if (Notes.isEmpty()) {
                binding?.notesRecyclerView?.visibility = View.GONE
                binding?.emptyListText?.visibility = View.VISIBLE
            } else {
                // setHasFixedSize(true) help improve the performance of your app by reducing unnecessary layout computations.
                binding?.notesRecyclerView?.setHasFixedSize(true)
                binding?.notesRecyclerView?.layoutManager =
                    StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                notesAdapter = NotesAdapter(Notes, this)
                binding?.notesRecyclerView?.adapter = notesAdapter
            }
        }
    }

    private fun signOut() {
        context?.let { it ->
            androidx.appcompat.app.AlertDialog.Builder(it)
                .setMessage("Are you sure you want to log out ?")
                .setPositiveButton("Log Out") { _: DialogInterface?, _: Int ->
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    val mGoogleSignInClient = context?.let { GoogleSignIn.getClient(it, gso) }
                    Toast.makeText(context, "Logged Out!", Toast.LENGTH_SHORT).show()
                    mGoogleSignInClient?.signOut()?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            setEmailEmpty()
                            firebaseAuth.signOut()
                            mView.findNavController()
                                .navigate(R.id.action_notesFragment_to_authenticationFragment)
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }



    private fun newNoteFragment() {
        val bundle = Bundle()
        bundle.putBoolean("edit", false)
        NewNoteFragment().arguments = bundle
        mView.findNavController().navigate(R.id.action_notesFragment_to_newNoteFragment, bundle)
    }

    private fun setEmailEmpty() {
        preferences.edit()?.putString("email", "none")?.apply()
    }

    override fun onItemClicked(position: Int) {
        val bundle = Bundle()
        bundle.putString("email", mUserEmail)
        bundle.putSerializable("note", Notes[position])
        bundle.putBoolean("edit", true)
        mView.findNavController().navigate(R.id.action_notesFragment_to_newNoteFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // we set _binding to null to prevent memory leaks.
        _binding = null
    }


}