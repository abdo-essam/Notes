package com.example.notes.authentication

import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.notes.R
import com.example.notes.databinding.FragmentAuthenticationBinding
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthenticationFragment : Fragment() {

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        init()

    }


    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        binding.productivityAnimation.playAnimation()
        binding.googleLoginBtn.setOnClickListener { loginUser() }
    }


    private fun loginUser() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }


    var activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            binding.authContainer.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
            val intent = result.data
            if (intent != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.let { firebaseAuthWithGoogle(it) }
                } catch (e: ApiException) {
                    binding.authContainer.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "There was a problem signing in. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            run {
                if (task.isSuccessful) {
                    saveEmailInSharedPref(account)
                    goToNotesFragment()
                }else {
                    Toast.makeText(context,"Something went wrong, Please try again!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveEmailInSharedPref(acct: GoogleSignInAccount?){
        val preferences = context?.getSharedPreferences(getString(R.string.preference_name), AppCompatActivity.MODE_PRIVATE)
        preferences?.edit()?.putString("email",acct?.email.toString())?.apply()
    }

    private fun goToNotesFragment() {
        mView.findNavController().navigate(R.id.action_authenticationFragment_to_notesFragment)
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null){
            binding.productivityAnimation.cancelAnimation()
            goToNotesFragment()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // to avoid memory leaks
        _binding = null
    }


}