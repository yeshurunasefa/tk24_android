package com.tech24et.tech24technician.components

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tech24et.tech24technician.data.SampleData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TechnicianViewModel(app: Application) : AndroidViewModel(app) {

    private val tracker = LocationTracker(app)

    private val _state = MutableStateFlow(SampleData.initialState())
    val state: StateFlow<TechnicianUiState> = _state.asStateFlow()

    /** One-off messages for the snackbar. */
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val events: SharedFlow<String> = _events.asSharedFlow()

    private var locationJob: Job? = null

    init {
        startLocationUpdates()
    }

    // ---------------------------------------------------------------------------------
    // Location
    // ---------------------------------------------------------------------------------

    /**
     * Call again after the permission dialog returns. Safe to call multiple times.
     *
     * NOTE: this only streams while the app process is alive in the foreground. For
     * "always tracked during working hours" you'll want a foreground Service with a
     * persistent notification (and ACCESS_BACKGROUND_LOCATION) that pushes fixes to your backend.
     */
    fun startLocationUpdates() {
        if (locationJob?.isActive == true) return
        if (!tracker.hasPermission() || !_state.value.locationTrackingEnabled) return

        locationJob = viewModelScope.launch {
            tracker.updates().collect { fix ->
                _state.update { it.copy(location = fix) }
                // TODO: push `fix` to your backend so supervisors can see the technician live.
            }
        }
    }

    private fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
        _state.update { it.copy(location = null) }
    }

    // ---------------------------------------------------------------------------------
    // Cases
    // ---------------------------------------------------------------------------------

    /**
     * Moves a case to its next status (accept -> travel -> arrived -> work started -> completed)
     * and records where the technician was at that moment.
     */
    fun advanceCase(caseId: String) {
        val current = _state.value.cases.firstOrNull { it.id == caseId } ?: return
        val next = current.status.next ?: return
        if (_state.value.updatingCaseId != null) return

        viewModelScope.launch {
            _state.update { it.copy(updatingCaseId = caseId) }

            // Fresh reading right now, fall back to the last streamed fix.
            val fix = tracker.currentFix() ?: _state.value.location

            if (next in LOCATION_REQUIRED && fix == null) {
                _state.update { it.copy(updatingCaseId = null) }
                _events.emit("Can't read your location. Turn on GPS and try again.")
                return@launch
            }

            val event = StatusEvent(
                status = next,
                timestamp = System.currentTimeMillis(),
                latitude = fix?.latitude,
                longitude = fix?.longitude,
                accuracyMeters = fix?.accuracyMeters,
            )

            // TODO: send `event` to your API first and only commit locally on success
            //       (or queue it in Room + WorkManager so it syncs when offline).
            _state.update { s ->
                s.copy(
                    updatingCaseId = null,
                    cases = s.cases.map {
                        if (it.id == caseId) it.copy(status = next, history = it.history + event) else it
                    },
                )
            }

            _events.emit(
                when (next) {
                    CaseStatus.COMPLETED -> "Case closed. Location saved to the audit trail."
                    else -> "Status updated: ${next.label}"
                }
            )
        }
    }

    // ---------------------------------------------------------------------------------
    // Notifications & settings
    // ---------------------------------------------------------------------------------

    fun markAllRead() = _state.update { s ->
        s.copy(notifications = s.notifications.map { it.copy(read = true) })
    }

    fun markRead(id: String) = _state.update { s ->
        s.copy(notifications = s.notifications.map { if (it.id == id) it.copy(read = true) else it })
    }

    fun setDarkMode(enabled: Boolean) = _state.update { it.copy(darkMode = enabled) }

    fun setNotificationsEnabled(enabled: Boolean) =
        _state.update { it.copy(notificationsEnabled = enabled) }

    fun setLocationTracking(enabled: Boolean) {
        _state.update { it.copy(locationTrackingEnabled = enabled) }
        if (enabled) startLocationUpdates() else stopLocationUpdates()
    }

    fun logout() {
        // TODO: clear session/token, stop tracking, navigate to login.
        stopLocationUpdates()
    }

    private companion object {
        /** Statuses that must have a GPS position, otherwise the change is rejected. */
        val LOCATION_REQUIRED = setOf(CaseStatus.ARRIVED, CaseStatus.COMPLETED)
    }
}
