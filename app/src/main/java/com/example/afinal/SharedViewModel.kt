package com.example.afinal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedCity = MutableLiveData<String>()

    fun selectCity(city: String) {
        selectedCity.value = city
    }
}