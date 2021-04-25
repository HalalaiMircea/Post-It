package net.mready.postit.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.mready.postit.R
import net.mready.postit.data.LoggedInUser
import net.mready.postit.data.Result
import net.mready.postit.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.btnLogin
        val loadingProgressBar = binding.loading

        // Update form fields and button to indicate valid inputs
        loginViewModel.loginFormState.observe(viewLifecycleOwner) { loginFormState ->
            if (loginFormState == null) return@observe
            loginButton.isEnabled = loginFormState.isDataValid
            loginFormState.usernameError?.let {
                usernameEditText.error = getString(it)
            }
            loginFormState.passwordError?.let {
                passwordEditText.error = getString(it)
            }
        }

        // Upon login result, update UI
        loginViewModel.loginResult.observe(viewLifecycleOwner) { loginResult ->
            loginResult ?: return@observe
            loadingProgressBar.visibility = View.GONE
            when (loginResult) {
                is Result.Error -> {
                    showLoginFailed(loginResult.exception.message ?: "Generic error")
                }
                is Result.Success -> {
                    updateUiWithUser(loginResult.data)
                    findNavController().popBackStack()
                }
            }
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        // Show spinning loading bar and send request
        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_register_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as AppCompatActivity).supportActionBar?.show()
        _binding = null
    }

    /**
     * Shows something that the user has logged in successfully
     */
    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
    }
}