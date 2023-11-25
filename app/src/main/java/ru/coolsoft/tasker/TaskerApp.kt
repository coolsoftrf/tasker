package ru.coolsoft.tasker

import android.app.Application
import java.io.File

class TaskerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        File(applicationContext.getExternalFilesDir(null), "config").apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }
}