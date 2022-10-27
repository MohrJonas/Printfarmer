package mohr.jonas.printfarmer

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import mohr.jonas.printfarmer.data.printer.PrinterQueue
import org.apache.commons.lang3.StringUtils
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.math.round

object MoonrakerClient {

    private val client = HttpClient(Apache) {
        expectSuccess = true
        engine {
            followRedirects = true
            socketTimeout = 30000
            connectTimeout = 30000
            connectionRequestTimeout = 40000
        }
    }

    suspend fun getPrinterStatus(ip: String): String {
        return try {
            val res: JsonObject = Json.decodeFromString(client.get("$ip/printer/objects/query?print_stats=state").bodyAsText())
            StringUtils.capitalize((res["result"]?.jsonObject?.get("status")?.jsonObject?.get("print_stats")?.jsonObject?.get("state")?.jsonPrimitive?.content))
                ?: "Unknown"
        } catch (t: Throwable) {
            "Error"
        }
    }

    suspend fun getPrinterQueue(ip: String): PrinterQueue {
        return try {
            val res: JsonObject = Json.decodeFromString(client.get("$ip/server/job_queue/status").bodyAsText())
            PrinterQueue(StringUtils.capitalize(res["result"]?.jsonObject?.get("queue_state")?.jsonPrimitive?.content) ?: "Unknown",
                res["result"]?.jsonObject?.get("queued_jobs")?.jsonArray?.map { Json.decodeFromJsonElement(it) } ?: emptyList())
        } catch (t: Throwable) {
            PrinterQueue("Error", emptyList())
        }
    }

    suspend fun getCurrentPrintProgress(ip: String): Float {
        return try {
            val res: JsonObject = Json.decodeFromString(client.get("$ip/printer/objects/query?virtual_sdcard=progress").bodyAsText())
            round(100f * (res["result"]?.jsonObject?.get("status")?.jsonObject?.get("virtual_sdcard")?.jsonObject?.get("progress")?.jsonPrimitive?.float ?: 0f)) / 100f
        } catch (t: Throwable) {
            0f
        }
    }

    suspend fun getExtruderTemp(ip: String): Float {
        return try {
            val res: JsonObject = Json.decodeFromString(client.get("$ip/printer/objects/query?extruder=temperature").bodyAsText())
            res["result"]?.jsonObject?.get("status")?.jsonObject?.get("extruder")?.jsonObject?.get("temperature")?.jsonPrimitive?.float ?: 0f
        } catch (t: Throwable) {
            0f
        }
    }

    suspend fun getBedTemp(ip: String): Float {
        return try {
            val res: JsonObject = Json.decodeFromString(client.get("$ip/printer/objects/query?heater_bed=temperature").bodyAsText())
            res["result"]?.jsonObject?.get("status")?.jsonObject?.get("heater_bed")?.jsonObject?.get("temperature")?.jsonPrimitive?.float ?: 0f
        } catch (t: Throwable) {
            0f
        }
    }

    suspend fun queuePrint(ip: String, fileName: String) {
        client.post("$ip/server/job_queue/job?filenames=${URLEncoder.encode(fileName, Charsets.UTF_8)}")
    }

    suspend fun dequeuePrint(ip: String, jobId: String) {
        client.delete("$ip/server/job_queue/job?job_ids=$jobId")
    }

    suspend fun uploadFile(ip: String, path: Path) {
        client.submitFormWithBinaryData(
            "$ip/server/files/upload",
            formData = formData {
                append("", Files.readAllBytes(path), Headers.build {
                    append(HttpHeaders.ContentType, ContentType.MultiPart.FormData)
                    append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"${path.name}\"")
                })
            }
        )
    }
}