package com.segunfrancis.tasktracker.di

import android.content.Context
import com.segunfrancis.tasktracker.data.local.TaskTrackerDao
import com.segunfrancis.tasktracker.data.local.TaskTrackerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideDao(@ApplicationContext context: Context): TaskTrackerDao {
        return TaskTrackerDatabase.getDatabase(context).dao()
    }

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
