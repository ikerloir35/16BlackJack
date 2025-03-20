package com.example.droid.config

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.util.Properties

object Config {
    private var properties: Properties? = null

    fun initialize(context: Context) {
        properties = Properties()
        try {
            // Try to load from assets first
            val envFile = context.assets.open(".env")
            properties?.load(envFile)
        } catch (e: Exception) {
            // If not found in assets, try to load from app's private directory
            val envFile = File(context.filesDir, ".env")
            if (envFile.exists()) {
                FileInputStream(envFile).use { fis ->
                    properties?.load(fis)
                }
            }
        }
    }

    fun getGoogleApiKey(): String {
        return properties?.getProperty("GOOGLE_API_KEY") ?: throw IllegalStateException("GOOGLE_API_KEY not found in environment variables")
    }

    fun getGoogleSitesApiKey(): String {
        return properties?.getProperty("GOOGLE_SITES_API_KEY") ?: throw IllegalStateException("GOOGLE_SITES_API_KEY not found in environment variables")
    }

    fun getFirebaseWebClientId(): String {
        return properties?.getProperty("FIREBASE_WEB_CLIENT_ID") ?: throw IllegalStateException("FIREBASE_WEB_CLIENT_ID not found in environment variables")
    }
} 