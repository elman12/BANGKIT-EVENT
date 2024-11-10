package com.elmansidik.dicodingevent.ui.upcoming_event

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
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.FragmentUpcomingEventBinding
import com.elmansidik.dicodingevent.utils.UiHandler.handleError
import com.elmansidik.dicodingevent.utils.UiHandler.showLoading
import com.elmansidik.dicodingevent.viewmodel.AdapterVerticalEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapterVerticalEvent: AdapterVerticalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)

        initializeViews()
        setupObservers()

        return binding.root
    }

    private fun initializeViews() {
        // Setup RecyclerView
        setupRecyclerView()

        // Setup Adapter
        setupEventAdapter()
    }

    private fun setupRecyclerView() {
        binding.rvUpcomingEvent.layoutManager = LinearLayoutManager(requireContext())

        // Adding divider between items
        val itemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.rvUpcomingEvent.addItemDecoration(itemDecoration)
    }

    private fun setupEventAdapter() {
        adapterVerticalEvent = AdapterVerticalEvent { eventId ->
            eventId?.let {
                val bundle = Bundle().apply { putInt("eventId", it) }
                findNavController().navigate(R.id.navigation_detail, bundle)
            }
        }
        binding.rvUpcomingEvent.adapter = adapterVerticalEvent
    }

    private fun setupObservers() {
        // Observe upcoming events
        mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { events ->
            events?.let { setUpcomingEvent(it) }
            mainViewModel.clearErrorMessage()
        }

        // Observe loading state
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading, binding.progressBar)
        }

        // Observe error message
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            handleError(
                isError = errorMessage != null,
                message = errorMessage,
                errorTextView = binding.tvErrorMessage,
                refreshButton = binding.btnRefresh,
                recyclerView = binding.rvUpcomingEvent
            ) {
                mainViewModel.getUpcomingEvent()
            }
        }
    }

    private fun setUpcomingEvent(events: List<ListEventsItem>) {
        // Submit the events to the adapter
        adapterVerticalEvent.submitList(events)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding when the view is destroyed
    }
}
