package ru.rznnike.eyehealthmanager.app.global

import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

abstract class BaseBindingItem<Binding : ViewBinding> : AbstractBindingItem<Binding>() {
    var binding: Binding? = null

    abstract fun Binding.bindView()

    open fun Binding.onUnbindView() = Unit

    override fun bindView(binding: Binding, payloads: List<Any>) {
        this.binding = binding
        binding.bindView()
    }

    override fun unbindView(binding: Binding) {
        this.binding = null
        binding.onUnbindView()
    }
}