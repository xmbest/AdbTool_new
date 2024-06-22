package com.xiaming.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoming.db.DAOImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val v = mutableStateOf("")
@Composable
fun SettingScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(v.value, onValueChange = {
            v.value = it
        })
        Button(onClick = {
            CoroutineScope(Dispatchers.Default).launch {
                DAOImpl.putString(v.value, v.value)
            }
        }){
            Text("set")
        }

        Button(onClick = {
            CoroutineScope(Dispatchers.Default).launch {
                DAOImpl.findAll().forEach{
                    println("key = ${it.k},value = ${it.v}")
                }
            }
        }){
            Text("get")
        }
    }
}