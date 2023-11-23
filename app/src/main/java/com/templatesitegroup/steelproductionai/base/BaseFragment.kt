package com.templatesitegroup.steelproductionai.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.navigation.NavController
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.templatesitegroup.steelproductionai.R
import com.templatesitegroup.steelproductionai.base.Extensions.hideKeyboard

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<
    VB : ViewBinding,
    State : ScreenState,
    SupportedCommandType : Command>
(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    val binding
        get() = _binding!!

    protected lateinit var navController: NavController
        private set

    private lateinit var attachedViewModel: BaseViewModel<State, SupportedCommandType>

    fun attachViewModel(viewModel: BaseViewModel<State, SupportedCommandType>) {
        attachedViewModel = viewModel
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            view.hideKeyboard()
        }
        try {
            navController = Navigation.findNavController(view)
        } catch (e: Throwable) {
            // Падают ошибки при старте фрагментов используемых в адаптерах BmElementVPAdapter для ViewPager2
            // TODO("Исправить ошибку инициализации LeftoverComponentDetailsPageFragment")
            Log.w("navController", "Nav controller not found: ${e.message}")
        }
        initView()
    }

    open fun initView() {}
    open fun initCreate() {}
    open fun initDestroy() {}


    @CallSuper
    override fun onResume() {
        super.onResume()
        subscribeToViewModelLiveData()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun subscribeToViewModelLiveData() {
        attachedViewModel.screenStateLiveData.observe(viewLifecycleOwner) { screenState ->
            renderView(screenState)
        }
        attachedViewModel.commandsLiveData.observe(viewLifecycleOwner) { command ->
            executeCommand(command)
        }
    }

    protected abstract fun renderView(screenState: State)

    protected open fun executeCommand(command: SupportedCommandType) {}

    fun showToast(messageId: Int) {
        Toast.makeText(requireActivity(), messageId, Toast.LENGTH_SHORT).show()
    }

    fun showToast(message: String?, useLong: Boolean = false) {
        val duration = if (useLong) Toast.LENGTH_LONG
        else Toast.LENGTH_SHORT
        // если версия 33 и выше, то toast обрезается, нужен кастомный toast
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            showCustomToast(message, duration)
        } else {
            Toast.makeText(requireActivity(), message, duration).show()
        }
    }

    @SuppressLint("InflateParams")
    private fun showCustomToast(message: String?, duration: Int) {
        val toast = Toast(requireActivity())
        toast.duration = duration
        val layout = layoutInflater.inflate(R.layout.layout_toast, null)
        layout.findViewById<TextView>(R.id.message).text = message
        toast.view = layout
        toast.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initDestroy()
        _binding = null
    }
}