package com.example.myweatherkotlinmvvm.di

import com.example.myweatherkotlinmvvm.framework.ui.details.DetailsViewModel
import com.example.myweatherkotlinmvvm.framework.ui.history.HistoryViewModel
import com.example.myweatherkotlinmvvm.framework.ui.main.MainViewModel
import com.example.myweatherkotlinmvvm.model.entities.repository.Repository
import com.example.myweatherkotlinmvvm.model.entities.repository.RepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Repository> { RepositoryImpl() }
    viewModel { MainViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
}