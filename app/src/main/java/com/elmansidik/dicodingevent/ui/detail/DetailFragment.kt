package com.elmansidik.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.elmansidik.dicodingevent.R
import com.elmansidik.dicodingevent.data.local.database.FavoriteEvent
import com.elmansidik.dicodingevent.data.response_retrofit.response.Event
import com.elmansidik.dicodingevent.databinding.FragmentDetailBinding
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        setupActionBar()
        val eventId = arguments?.getInt("eventId")
        eventId?.let {
            loadEventData(it)
            setupFavoriteButton(it)
        }

        return binding?.root ?: error("Binding is null!")
    }

    private fun setupActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadEventData(eventId: Int) {
        viewModel.getDetailEvent(eventId)
        viewModel.detailEvent.observe(viewLifecycleOwner) { event ->
            displayEventData(event)
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::toggleLoading)
        viewModel.errorMessage.observe(viewLifecycleOwner, ::handleError)
    }

    private fun displayEventData(event: Event) {
        binding?.apply {
            tvEventName.text = event.name
            tvOwnerName.text = event.ownerName
            tvEventTime.text = getString(R.string.event_time, event.beginTime)
            tvQuota.text = getString(R.string.quota_remaining, event.calculateRemainingQuota())
            tvDescription.text = event.getFormattedDescription()
            btnEventLink.setOnClickListener { openEventLink(event.link) }

            Glide.with(this@DetailFragment)
                .load(event.mediaCover)
                .into(ivMediaCover)
        }
    }

    private fun Event.calculateRemainingQuota(): Int {
        return quota?.minus(registrants ?: 0) ?: 0
    }

    private fun Event.getFormattedDescription() = description?.let {
        HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun openEventLink(link: String?) {
        link?.let {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        }
    }

    private fun setupFavoriteButton(eventId: Int) {
        viewModel.getFavoriteEventById(eventId).observe(viewLifecycleOwner) { favoriteEvent ->
            binding?.fabLove?.setImageResource(
                if (favoriteEvent == null) R.drawable.ic_favorite else R.drawable.ic_favorite_filled
            )

            binding?.fabLove?.setOnClickListener {
                toggleFavoriteStatus(favoriteEvent)
            }
        }
    }

    private fun toggleFavoriteStatus(favoriteEvent: FavoriteEvent?) {
        val currentEvent = viewModel.detailEvent.value
        currentEvent?.let { event ->
            if (favoriteEvent == null) {
                addFavoriteEvent(event)
            } else {
                removeFavoriteEvent(favoriteEvent)
            }
        }
    }

    private fun addFavoriteEvent(event: Event) {
        val favoriteEvent = FavoriteEvent(
            eventId = event.id,
            name = event.name,
            description = event.summary,
            imageUrl = event.imageLogo
        )
        viewModel.insertFavoriteEvent(favoriteEvent)
    }

    private fun removeFavoriteEvent(favoriteEvent: FavoriteEvent) {
        viewModel.deleteFavoriteEvent(favoriteEvent)
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleError(errorMessage: String?) {
        binding?.apply {
            handlingLayout.visibility = if (errorMessage.isNullOrEmpty()) View.GONE else View.VISIBLE
            tvErrorMessage.text = errorMessage
            btnRefresh.visibility = if (errorMessage.isNullOrEmpty()) View.GONE else View.VISIBLE
            btnRefresh.setOnClickListener {
                arguments?.getInt("eventId")?.let { eventId ->
                    viewModel.getDetailEvent(eventId)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
