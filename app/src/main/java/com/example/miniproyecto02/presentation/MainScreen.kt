package com.example.miniproyecto02.presentation

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miniproyecto02.data.Tarea
import com.example.miniproyecto02.viewmodel.TareaViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    tareaViewModel: TareaViewModel = viewModel()
) {
    val context = LocalContext.current
    val tareas by tareaViewModel.tareas.collectAsState(initial = emptyList())

    var mostrarFormulario by remember { mutableStateOf(false) }
    var tareaEditar by remember { mutableStateOf<Tarea?>(null) }
    var filtro by remember { mutableStateOf("Todas") }
    var ordenarFecha by remember { mutableStateOf(false) }
    var mostrarMenu by remember { mutableStateOf(false) }

    val tareasVisibles = tareas.filter {
        when (filtro) {
            "Pendientes" -> !it.completada
            "Completadas" -> it.completada
            else -> true
        }
    }.let { lista ->
        if (ordenarFecha) lista.sortedBy { it.fecha } else lista
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                actions = {
                    TextButton(onClick = { mostrarMenu = true }) {
                        Text("Opciones")
                    }

                    DropdownMenu(
                        expanded = mostrarMenu,
                        onDismissRequest = { mostrarMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas las tareas") },
                            onClick = {
                                filtro = "Todas"
                                mostrarMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Pendientes") },
                            onClick = {
                                filtro = "Pendientes"
                                mostrarMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Completadas") },
                            onClick = {
                                filtro = "Completadas"
                                mostrarMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (ordenarFecha) "Ordenar por registro"
                                    else "Ordenar por fecha"
                                )
                            },
                            onClick = {
                                ordenarFecha = !ordenarFecha
                                mostrarMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Borrar completadas") },
                            onClick = {
                                tareaViewModel.eliminarCompletadas()
                                mostrarMenu = false
                                Toast.makeText(
                                    context,
                                    "Tareas completadas eliminadas",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    tareaEditar = null
                    mostrarFormulario = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar tarea"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Filtro: $filtro",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Total: ${tareasVisibles.size} tareas")
            Spacer(modifier = Modifier.height(12.dp))

            if (tareasVisibles.isEmpty()) {
                Text("No hay tareas para mostrar.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = tareasVisibles,
                        key = { it.id }
                    ) { tarea ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = tarea.completada,
                                    onCheckedChange = {
                                        tareaViewModel.actualizar(
                                            tarea.copy(completada = it)
                                        )
                                    }
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = tarea.titulo,
                                        style = MaterialTheme.typography.titleMedium,
                                        textDecoration = if (tarea.completada)
                                            TextDecoration.LineThrough
                                        else TextDecoration.None
                                    )
                                    if (tarea.descripcion.isNotBlank()) {
                                        Text(tarea.descripcion)
                                    }
                                    Text("Fecha: ${tarea.fecha}")
                                    Text("Prioridad: ${tarea.prioridad}")
                                }

                                IconButton(
                                    onClick = {
                                        tareaEditar = tarea
                                        mostrarFormulario = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar"
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        tareaViewModel.eliminar(tarea)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioTarea(
            tarea = tareaEditar,
            onCerrar = {
                mostrarFormulario = false
                tareaEditar = null
            },
            onGuardar = { tarea ->
                if (tarea.id == 0) {
                    tareaViewModel.insertar(tarea)
                } else {
                    tareaViewModel.actualizar(tarea)
                }
                mostrarFormulario = false
                tareaEditar = null
            }
        )
    }
}

@Composable
private fun FormularioTarea(
    tarea: Tarea?,
    onCerrar: () -> Unit,
    onGuardar: (Tarea) -> Unit
) {
    val context = LocalContext.current
    var titulo by remember(tarea) {
        mutableStateOf(tarea?.titulo ?: "")
    }
    var descripcion by remember(tarea) {
        mutableStateOf(tarea?.descripcion ?: "")
    }
    var fecha by remember(tarea) {
        mutableStateOf(tarea?.fecha ?: "")
    }
    var prioridad by remember(tarea) {
        mutableStateOf(tarea?.prioridad ?: "Media")
    }
    var mostrarPrioridades by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Text(if (tarea == null) "Nueva tarea" else "Editar tarea")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val calendario = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, anio, mes, dia ->
                                fecha = "%04d-%02d-%02d".format(
                                    anio, mes + 1, dia
                                )
                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                ) {
                    Text(
                        if (fecha.isBlank()) "Elegir fecha"
                        else fecha
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Prioridad: $prioridad")
                    TextButton(
                        onClick = { mostrarPrioridades = true }
                    ) {
                        Text("Cambiar")
                    }
                    DropdownMenu(
                        expanded = mostrarPrioridades,
                        onDismissRequest = { mostrarPrioridades = false }
                    ) {
                        listOf("Baja", "Media", "Alta").forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    prioridad = opcion
                                    mostrarPrioridades = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (titulo.isBlank()) {
                        Toast.makeText(
                            context,
                            "Escribe el título de la tarea",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (fecha.isBlank()) {
                        Toast.makeText(
                            context,
                            "Selecciona una fecha",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onGuardar(
                            Tarea(
                                id = tarea?.id ?: 0,
                                titulo = titulo.trim(),
                                descripcion = descripcion.trim(),
                                fecha = fecha,
                                prioridad = prioridad,
                                completada = tarea?.completada ?: false
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cancelar")
            }
        }
    )
}