package com.example.miniproyecto02.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto02.data.AppDatabase
import com.example.miniproyecto02.data.Tarea
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TareaViewModel(application: Application) :
    AndroidViewModel(application) {

    private val tareaDao = AppDatabase
        .obtenerBaseDatos(application)
        .tareaDao()

    val tareas: Flow<List<Tarea>> = tareaDao.obtenerTareas()

    fun insertar(tarea: Tarea) {
        viewModelScope.launch {
            tareaDao.insertar(tarea)
        }
    }

    fun actualizar(tarea: Tarea) {
        viewModelScope.launch {
            tareaDao.actualizar(tarea)
        }
    }

    fun eliminar(tarea: Tarea) {
        viewModelScope.launch {
            tareaDao.eliminar(tarea)
        }
    }

    fun eliminarCompletadas() {
        viewModelScope.launch {
            tareaDao.eliminarCompletadas()
        }
    }
}
