package mohr.jonas.printfarmer.data.printer

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PrinterQueue(
    @SerialName("queued_jobs") private val _queueStatus: String,
    @SerialName("queue_state") private val _queuedJobs: List<QueuedJob>
) {
    @Transient
    var queueStatus = mutableStateOf(_queueStatus)

    @Transient
    var queuedJobs = mutableStateListOf(*_queuedJobs.toTypedArray())
}