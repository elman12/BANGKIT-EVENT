package com.elmansidik.dicodingevent.ui.home

import android.os.Bundle
import android.util.Log
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
import com.elmansidik.dicodingevent.databinding.FragmentHomeBinding
import com.elmansidik.dicodingevent.utils.UiHandler.handleError
import com.elmansidik.dicodingevent.utils.UiHandler.showLoading
import com.elmansidik.dicodingevent.viewmodel.AdapterHorizontalEvent
import com.elmansidik.dicodingevent.viewmodel.AdapterVerticalEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapterVertical: AdapterVerticalEvent
    private lateinit var adapterHorizontal: AdapterHorizontalEvent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initViews()
        initObservers()

        return binding?.root ?: throw IllegalStateException("Binding is null!")
    }

    private fun initViews() {
        binding?.apply {
            setupRecyclerViews()
            setupAdapters()
        }
    }

    private fun setupRecyclerViews() {
        binding?.apply {
            rvUpcomingEvent.layoutManager = createHorizontalLayoutManager()

            rvFinishedEvent.layoutManager = createVerticalLayoutManager()
        }
    }

    private fun createHorizontalLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun createVerticalLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext())
    }

    private fun createItemDecoration(layoutManager: LinearLayoutManager): DividerItemDecoration {
        return DividerItemDecoration(requireContext(), layoutManager.orientation)
    }

    private fun setupAdapters() {
        adapterVertical = AdapterVerticalEvent { eventId -> navigateToEventDetail(eventId) }
        adapterHorizontal = AdapterHorizontalEvent { eventId -> navigateToEventDetail(eventId) }

        binding?.apply {
            rvUpcomingEvent.adapter = adapterHorizontal
            rvFinishedEvent.adapter = adapterVertical
        }
    }

    private fun navigateToEventDetail(eventId: Int?) {
        eventId?.let {
            val bundle = Bundle().apply { putInt("eventId", it) }
            findNavController().navigate(R.id.navigation_detail, bundle)
        }
    }

    private fun initObservers() {
        observeUpcomingEvents()
        observeFinishedEvents()
        observeLoadingState()
        observeErrorMessages()
    }

    private fun observeUpcomingEvents() {
        mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
            if (listItems.isNullOrEmpty()) {
                Log.e("HomeFragment", "Upcoming events are empty or null.")
            } else {
                Log.d("HomeFragment", "Upcoming events: $listItems")
            }
            setUpcomingEvent(listItems)
            mainViewModel.clearErrorMessage()
        }
    }

    private fun observeFinishedEvents() {
        mainViewModel.finishedEvent.observe(viewLifecycleOwner) { listItems ->
            if (listItems.isNullOrEmpty()) {
                Log.e("HomeFragment", "Finished events are empty or null.")
            } else {
                Log.d("HomeFragment", "Finished events: $listItems")
            }
            setFinishedEvent(listItems)
            mainViewModel.clearErrorMessage()
        }
    }

    private fun observeLoadingState() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding?.progressBar?.let {
                showLoading(isLoading, it)
            }
        }
    }

    private fun observeErrorMessages() {
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            binding?.apply {
                handleError(
                    isError = errorMessage != null,
                    message = errorMessage,
                    errorTextView = tvErrorMessage,
                    refreshButton = btnRefresh,
                    recyclerView = rvFinishedEvent
                ) {
                    mainViewModel.getUpcomingEvent()
                    mainViewModel.getFinishedEvent()
                }
            }
        }
    }

    private fun setUpcomingEvent(listUpcomingEvent: List<ListEventsItem>) {
        val limitedList = listUpcomingEvent.take(5)
        adapterHorizontal.submitList(limitedList)
    }

    private fun setFinishedEvent(listFinishedEvent: List<ListEventsItem>) {
        val limitedList = listFinishedEvent.takeLast(15)
        adapterVertical.submitList(limitedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
