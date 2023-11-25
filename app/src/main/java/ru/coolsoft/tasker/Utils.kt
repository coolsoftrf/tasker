package ru.coolsoft.tasker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

private val tag = Utils::class.simpleName!!

object Utils {
    fun Context.loadConfig(onLoaded: (task: Task) -> Unit) {
        File(getExternalFilesDir(null), "config").apply {
            BufferedReader(FileReader(this)).apply {
                Log.d(tag, "config reader created")

                with(readLine()) {
                    Log.d(tag, "config line read")

                    indexOf(' ').let {
                        if (it < 0 || length == it + 1) return

                        val token = substring(0, it)
                        Log.d(tag, "resolving token $token")
                        TaskType.entries.first { entry -> entry.token == token }
                            .apply {
                                val arg = substring(it + 1)
                                Log.d(tag, "resolving parameter $arg")
                                if (validate(arg)) {
                                    onLoaded(Task(this, this@loadConfig, arg))
                                }
                            }
                    }
                }
            }
        }
    }
}

private fun createOpenFileIntent(context: Context, fileName: String): Intent {
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        File(fileName)
    )

    return Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

private fun createIntentFromSpec(spec: String): Intent {
    val reader = JSONObject(spec)
    val data = try {
        Uri.parse(reader.getString("dat"))
    } catch (e: JSONException) {
        null
    }

    return Intent(reader.getString("act"), data).apply {
        addFlags(reader.getInt("flg"))
        addCategory(reader.getJSONArray("cat").getString(0))

        reader.getString("cmp").split("/").let {
            setClassName(it[0], "${it[0]}${it[1]}")
        }
    }
}

enum class TaskType(
    val token: String,
    val validate: (String) -> Boolean,
    val getIntent: (context: Context, param: String) -> Intent
) {
    FILE("f", { File(it).exists() }, ::createOpenFileIntent),
    INTENT("i", { it.isNotEmpty() }, { _, param -> createIntentFromSpec(param) }),
}

class Task(taskType: TaskType, context: Context, param: String) {
    val intent = taskType.getIntent(context, param)
}