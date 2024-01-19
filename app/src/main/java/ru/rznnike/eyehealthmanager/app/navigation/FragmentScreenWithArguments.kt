package ru.rznnike.eyehealthmanager.app.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.Creator
import com.github.terrakok.cicerone.androidx.FragmentScreen

class FragmentScreenWithArguments(
    val arguments: Map<String, Any?>,
    val fragmentCreator: Creator<FragmentFactory, Fragment>,
    override val screenKey: String,
    override val clearContainer: Boolean = true
) : FragmentScreen {
    override fun createFragment(factory: FragmentFactory) = fragmentCreator.create(factory)
}
