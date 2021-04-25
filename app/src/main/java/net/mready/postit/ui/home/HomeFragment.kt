package net.mready.postit.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import net.mready.postit.R
import net.mready.postit.custom.MarginItemDecoration
import net.mready.postit.data.Post
import net.mready.postit.databinding.FragmentHomeBinding
import net.mready.postit.databinding.LayoutItemPostBinding
import java.text.DateFormat

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentHomeBinding.bind(view)

        val adapter = PostsAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val decor = MarginItemDecoration(resources.getDimensionPixelOffset(R.dimen.decor_margin), 1)
        binding.recyclerView.addItemDecoration(decor)

        viewModel.updateData()
        viewModel.postsLiveData.observe(viewLifecycleOwner) {
            // After the list has been committed, execute a Runnable to scroll to beginning
            adapter.submitList(it) { binding.recyclerView.scrollToPosition(0) }
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.action_add_post)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_login) {
            val navController = findNavController()
            if (viewModel.authRepository.isLoggedIn) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_confirm))
                    .setPositiveButton(R.string.logout) { _, _ ->
                        viewModel.authRepository.logout()
                        navController.navigate(R.id.action_login)
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .create()
                dialog.show()
            } else {
                navController.navigate(R.id.action_login)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private class PostsAdapter : ListAdapter<Post, PostsAdapter.ViewHolder>(DIFF_CALLBACK) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LayoutItemPostBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let { holder.bind(it) }
        }

        class ViewHolder(val binding: LayoutItemPostBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: Post) = with(binding) {
                textName.text =
                    item.displayName ?: binding.root.context.getString(R.string.anonymous)
                textName.setTextColor(Color.parseColor(if (item.displayName != null) "#EE8A27" else "#EEC627"))
                textMessage.text = item.message
                textDate.text = dateFormat.format(item.createdAt)
            }
        }

        companion object {
            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Post>() {
                override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: Post, newItem: Post) =
                    oldItem == newItem
            }
            private val dateFormat = DateFormat.getDateTimeInstance()
        }
    }
}