package com.codingwithmitch.cleannotes.framework.presentation.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.codingwithmitch.cleannotes.di.AppComponent
import com.codingwithmitch.cleannotes.framework.presentation.BaseApplication
import com.codingwithmitch.cleannotes.framework.presentation.MainActivity
import com.codingwithmitch.cleannotes.framework.presentation.UIController
import com.codingwithmitch.cleannotes.util.TodoCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.lang.ClassCastException



typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseNoteFragment
<VB: ViewBinding>(
    private val inflate: Inflate<VB>
): Fragment() {

    lateinit var uiController: UIController
    private var _binding: VB? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun displayToolbarTitle(textView: TextView, title: String?, useAnimation: Boolean) {
        if(title != null){
            showToolbarTitle(textView, title, useAnimation)
        }
        else{
            hideToolbarTitle(textView, useAnimation)
        }
    }

    private fun hideToolbarTitle(textView: TextView, animation: Boolean){
        if(animation){
            textView.fadeOut(
                object: TodoCallback {
                    override fun execute() {
                        textView.text = ""
                    }
                }
            )
        }
        else{
            textView.text = ""
            textView.gone()
        }
    }

    private fun showToolbarTitle(
        textView: TextView,
        title: String,
        animation: Boolean
    ){
        textView.text = title
        if(animation){
            textView.fadeIn()
        }
        else{
            textView.visible()
        }
    }

    abstract fun inject()

    fun getAppComponent(): AppComponent{
        return activity?.run {
            (application as BaseApplication).appComponent
        }?: throw Exception("AppComponent is null.")
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
        setUIController(null) // null in production
    }

    fun setUIController(mockController: UIController?){

        // TEST: Set interface from mock
        if(mockController != null){
            this.uiController = mockController
        }
        else{ // PRODUCTION: if no mock, get from context
            activity?.let {
                if(it is MainActivity){
                    try{
                        uiController = context as UIController
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}

















