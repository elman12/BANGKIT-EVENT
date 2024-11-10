package com.elmansidik.dicodingevent.ui.finished_event

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.elmansidik.dicodingevent.R
import com.elmansidik.dicodingevent.databinding.FragmentFinishedEventBinding
import com.elmansidik.dicodingevent.viewmodel.AdapterVerticalEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterVertical: AdapterVerticalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()

        return binding.root
    }

    // Setup RecyclerView and Adapter
    private fun setupRecyclerView() {
        binding.rvFinishedEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinishedEvent.addItemDecoration(createItemDecoration())

        adapterVertical = AdapterVerticalEvent { eventId ->
            eventId?.let { navigateToDetail(it) }
        }

        binding.rvFinishedEvent.adapter = adapterVertical
    }

    // Create DividerItemDecoration
    private fun createItemDecoration(): DividerItemDecoration {
        val verticalLayout = LinearLayoutManager(requireContext())
        return DividerItemDecoration(requireContext(), verticalLayout.orientation)
    }

    // Handle navigation to event detail
    private fun navigateToDetail(eventId: Int) {
        val bundle = Bundle().apply { putInt("eventId", eventId) }
        findNavController().navigate(R.id.navigation_detail, bundle)
    }

    // Observe LiveData from ViewModel
    private fun observeViewModel() {
        observeLoadingState()
        observeFinishedEvents()
        observeSearchEvents()
    }

    // Show or hide loading progress bar
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    // Observe loading state from ViewModel
    private fun observeLoadingState() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { showLoading(it) }
    }

    // Observe finished events from ViewModel
    private fun observeFinishedEvents() {
        mainViewModel.finishedEvent.observe(viewLifecycleOwner) { listItems ->
            adapterVertical.submitList(listItems)
        }
    }

    // Observe search events from ViewModel
    private fun observeSearchEvents() {
        mainViewModel.searchEvent.observe(viewLifecycleOwner) { listItems ->
            adapterVertical.submitList(listItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
