package net.mready.postit.ui.post

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.mready.postit.R
import net.mready.postit.custom.CenteredTitleToolbar
import net.mready.postit.databinding.FragmentAddPostBinding

@AndroidEntryPoint
class AddPostFragment : Fragment(R.layout.fragment_add_post) {
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddPostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAddPostBinding.bind(view)
        // Since we don't use NavigationUI framework, we have to handle up navigation ourselves
        // Horrible...
        val compatActivity = activity as AppCompatActivity
        compatActivity.supportActionBar?.title = getString(R.string.add_new_post)
        compatActivity.findViewById<CenteredTitleToolbar>(R.id.toolbar).apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }

        binding.editMessage.addTextChangedListener(afterTextChanged = {
            viewModel.validateMessage(it.toString())
        })
        // If message post returns from backend, react to it
        viewModel.postResult.observe(viewLifecycleOwner) { post ->
            if (post == null) {
                Toast.makeText(context, getString(R.string.failed_post), Toast.LENGTH_SHORT).show()
                return@observe
            }
            findNavController().popBackStack()
        }
        viewModel.isValid.observe(viewLifecycleOwner, binding.btnSubmit::setEnabled)

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.setOnClickListener {
            val message = binding.editMessage.text.toString()
            viewModel.postMessage(message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<CenteredTitleToolbar>(R.id.toolbar)?.navigationIcon = null
        _binding = null
    }
}
