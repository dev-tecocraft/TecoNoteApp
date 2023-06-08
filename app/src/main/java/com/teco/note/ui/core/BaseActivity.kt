package com.teco.note.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.teco.note.databinding.LayoutAppBarBinding

typealias activityInflater<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding>(private val activityInflater: activityInflater<VB>) :
    AppCompatActivity() {

    protected lateinit var binding: VB

    abstract fun onActivityCreated()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = activityInflater.invoke(layoutInflater)
        setContentView(binding.root)
        onActivityCreated()
    }

    protected fun initActionBar(
        appBar: LayoutAppBarBinding,
        title: String = ""
    ){
        with(appBar){
            tvTitle.text = title
        }
    }
}