package net.mready.postit.ui.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.mready.postit.R
import net.mready.postit.data.LoggedInUser
import net.mready.postit.data.RegisterRequest
import net.mready.postit.data.Result
import net.mready.postit.databinding.FragmentRegisterBinding

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentRegisterBinding.bind(view)

        viewModel.regFormState.observe(viewLifecycleOwner) { formState ->
            if (formState == null) return@observe
            binding.button.isEnabled = formState.isDataValid
            formState.usernameError?.let {
                binding.editUsername.error = getString(it)
            }
            formState.passwordError?.let {
                binding.editPassword.error = getString(it)
            }
            formState.repeatError?.let {
                binding.editRepeatPass.error = getString(it)
            }
        }

        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            when (result) {
                is Result.Error -> {
                    showRegisterFailed(result.exception.message ?: "Generic error")
                }
                is Result.Success -> {
                    updateUiWithUser(result.data)
                    findNavController().popBackStack(R.id.nav_home, false)
                }
            }
        }

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) = with(binding) {
                viewModel.registerDataChanged(
                    registerData = RegisterRequest(
                        username = editUsername.text.toString(),
                        password = editPassword.text.toString(),
                        displayName = editFullName.text.toString()
                    ),
                    repeatPass = editRepeatPass.text.toString()
                )
            }
        }

        binding.run {
            editUsername.addTextChangedListener(afterTextChangedListener)
            editPassword.addTextChangedListener(afterTextChangedListener)
            editRepeatPass.addTextChangedListener(afterTextChangedListener)
            editRepeatPass.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    button.performClick()
                }
                false
            }
            button.setOnClickListener {
                viewModel.register(
                    RegisterRequest(
                        username = editUsername.text.toString(),
                        password = editPassword.text.toString(),
                        displayName = editFullName.text.toString()
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUiWithUser(model: LoggedInUser) {
        val welcome = getString(R.string.welcome) + model.displayName
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showRegisterFailed(errorString: String) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_LONG).show()
    }
}