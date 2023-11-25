package ru.coolsoft.tasker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ru.coolsoft.tasker.Utils.loadConfig

private val tag = BootReceiver::class.simpleName!!

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(tag, "onReceive called")
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d(tag, "onReceive intent validated")
            context.loadConfig {
                Log.d(tag, "starting the task")
                it.intent.apply {
                    context.startActivity(this)
                }
            }
        }
    }
}