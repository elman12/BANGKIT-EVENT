package com.elmansidik.dicodingevent.ui.favorite

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
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.databinding.FragmentFavoriteBinding
import com.elmansidik.dicodingevent.utils.UiHandler.handleError
import com.elmansidik.dicodingevent.utils.UiHandler.showLoading
import com.elmansidik.dicodingevent.viewmodel.AdapterFavoriteEvent
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var adapterFavoriteEvent: AdapterFavoriteEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupAdapter()
        observeViewModel()

        return binding.root
    }

    // Pengaturan RecyclerView dan Adapter
    private fun setupRecyclerView() {
        binding.rvFavoriteEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(createItemDecoration())
        }
    }

    private fun createItemDecoration(): DividerItemDecoration {
        val layoutManager = LinearLayoutManager(requireContext())
        return DividerItemDecoration(requireContext(), layoutManager.orientation)
    }

    // Pengaturan Adapter
    private fun setupAdapter() {
        adapterFavoriteEvent = AdapterFavoriteEvent { eventId ->
            eventId?.let { navigateToEventDetail(it) }
        }
        binding.rvFavoriteEvent.adapter = adapterFavoriteEvent
    }

    // Fungsi Navigasi ke Detail Event
    private fun navigateToEventDetail(eventId: Int) {
        val bundle = Bundle().apply { putInt("eventId", eventId) }
        findNavController().navigate(R.id.navigation_detail, bundle)
    }

    // Observasi ViewModel
    private fun observeViewModel() {
        observeFavoriteEvents()
        observeLoadingState()
        observeErrorMessages()
    }

    // Observasi Favorite Events dari ViewModel
    private fun observeFavoriteEvents() {
        mainViewModel.allFavoriteEvents.observe(viewLifecycleOwner) { listItems ->
            Log.d("FavoriteFragment", "Observed Favorite Events: $listItems")
            setFavoriteEvent(listItems)
        }
    }

    // Observasi State Loading
    private fun observeLoadingState() {
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading, binding.progressBar)
        }
    }

    // Observasi Error Message
    private fun observeErrorMessages() {
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            handleError(
                isError = errorMessage != null,
                message = errorMessage,
                errorTextView = binding.tvErrorMessage,
                refreshButton = binding.btnRefresh,
                recyclerView = binding.rvFavoriteEvent
            ) {
                mainViewModel.getAllFavoriteEvent()
            }
        }
    }

    // Set Data untuk Adapter
    private fun setFavoriteEvent(listFavoriteEvent: List<FavoriteEvent>) {
        adapterFavoriteEvent.submitList(listFavoriteEvent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
