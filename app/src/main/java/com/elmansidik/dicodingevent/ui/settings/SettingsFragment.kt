package com.elmansidik.dicodingevent.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.*
import com.elmansidik.dicodingevent.data.response_retrofit.response.ListEventsItem
import com.elmansidik.dicodingevent.databinding.FragmentSettingsBinding
import com.elmansidik.dicodingevent.utils.DailyReminderWorker
import com.elmansidik.dicodingevent.viewmodel.MainViewModel
import com.elmansidik.dicodingevent.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Toast.makeText(
                requireContext(),
                if (isGranted) "Notifications permission granted" else "Notifications permission rejected",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupWorkManager()
        observeThemeSettings()
        setupDarkModeSwitch()

        return requireNotNull(binding?.root) { "Binding is null!" }
    }

    private fun setupWorkManager() {
        workManager = WorkManager.getInstance(requireContext())
    }

    private fun observeThemeSettings() {
        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            binding?.switchDarkMode?.isChecked = isDarkModeActive
        }
    }

    private fun setupDarkModeSwitch() {
        binding?.switchDarkMode?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupDailyReminder() {
        mainViewModel.getUpcomingEvent()
        mainViewModel.upcomingEvent.observe(viewLifecycleOwner) { listItems ->
            listItems.filter { event ->
                event.beginTime?.let {
                    val eventDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .parse(it)?.time ?: 0
                    eventDate >= System.currentTimeMillis()
                } ?: false
            }.minByOrNull { event ->
                event.beginTime?.let {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it)?.time
                } ?: Long.MAX_VALUE
            }?.let { event -> scheduleDailyReminder(event) }
        }
    }

    private fun scheduleDailyReminder(event: ListEventsItem) {
        val data = Data.Builder()
            .putString("event_name", event.name)
            .putString("event_time", event.beginTime)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(periodicWorkRequest)
    }

    private fun cancelDailyReminder() {
        if (this::periodicWorkRequest.isInitialized) {
            workManager.cancelWorkById(periodicWorkRequest.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
