package com.fluidcloud.module

import android.app.Application
import android.util.Log
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class App : Application(), XposedServiceHelper.OnServiceListener {

    @Volatile
    private var xposedService: XposedService? = null

    private var serviceReadyLatch = CountDownLatch(1)

    override fun onCreate() {
        super.onCreate()
        instance = this
        XposedServiceHelper.registerListener(this)
    }

    override fun onServiceBind(service: XposedService) {
        xposedService = service
        Log.d(TAG, "XposedService bound")
        serviceReadyLatch.countDown()
    }

    override fun onServiceDied(service: XposedService) {
        xposedService = null
        Log.d(TAG, "XposedService died")
        serviceReadyLatch = CountDownLatch(1)
    }

    fun isModuleActive(): Boolean {
        val service = xposedService
        if (service == null) {
            // Try waiting briefly, but don't block long
            serviceReadyLatch.await(500, TimeUnit.MILLISECONDS)
        }

        val finalService = xposedService ?: run {
            Log.d(TAG, "XposedService is null")
            return false
        }
        return try {
            val scope = finalService.scope
            Log.d(TAG, "Current scope: $scope")
            "com.android.systemui" in scope
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check scope", e)
            false
        }
    }

    companion object {
        private const val TAG = "FluidCloud[App]"
        lateinit var instance: App
            private set
    }
}
