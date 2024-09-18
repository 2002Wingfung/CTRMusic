package com.example.jetpackmvvm.ext.util

import android.content.Context
import java.io.File

@Suppress("DEPRECATION")
fun Context.defaultOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, packageName).apply { mkdir() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
}