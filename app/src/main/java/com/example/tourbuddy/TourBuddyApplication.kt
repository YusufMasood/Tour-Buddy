package com.example.tourbuddy

import android.app.Application
import com.example.tourbuddy.data.local.AppDatabase

class TourBuddyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}