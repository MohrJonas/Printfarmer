package mohr.jonas.printfarmer.data.printer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QueuedJob(
    @SerialName("filename") val fileName: String,
    @SerialName("job_id") val jobId: String,
    @SerialName("time_added") val timeAdded: Double,
    @SerialName("time_in_queue") val timeInQueue: Double
)
