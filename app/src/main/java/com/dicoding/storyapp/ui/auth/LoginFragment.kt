package com.dicoding.storyapp.ui.auth
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.entity.UserEntity
import com.dicoding.storyapp.data.repository.Result
import com.dicoding.storyapp.data.source.remote.request.LoginRequest
import com.dicoding.storyapp.data.source.remote.response.LoginResultResponse
import com.dicoding.storyapp.databinding.FragmentLoginBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.insertStory.InsertActivity
import com.dicoding.storyapp.ui.setting.SettingActivity
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dicoding.storyapp.ui.main.MainViewModel


@Suppress("PrivatePropertyName")
class LoginFragment : Fragment() {

    private lateinit var appBinding: FragmentLoginBinding
    private lateinit var appViewModel: MainViewModel
    private var pressedTime: Long = 0
    private val PRESSED_INTERVAL = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return appBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(appBinding.ivAccount, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }

    private fun setupAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pressedTime + PRESSED_INTERVAL > System.currentTimeMillis()) {
                    requireActivity().finish()
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.press_back_string,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                pressedTime = System.currentTimeMillis()
            }
        })

        appBinding.apply {
            edLoginPassword.setOnEditorActionListener { _, actionId, _ ->
                clearFocusOnDoneAction(actionId)
            }

            cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                toggleLoginPasswordVisibility(isChecked)
            }

            tvSignUp.setOnClickListener { moveToRegisterFragment() }

            btnSignIn.setOnClickListener {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()

                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
       appBinding.apply {
           when {
               email.isEmpty() -> {
                   edLoginEmail.error = R.string.email_mandatory.toString()
               }
               password.isEmpty() -> {
                   edLoginPassword.error = R.string.password_mandatory.toString()
               }
               else -> {
                   executeLogin(email, password)
               }
           }
       }
    }

    private fun executeLogin(email: String, password: String) {
        appBinding.apply {
            appViewModel.login(LoginRequest(email, password)).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnSignIn.isEnabled = false
                        }
                        is Result.Success -> {
                            progressBar.visibility = View.GONE
                            btnSignIn.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.sign_in_success,
                                Toast.LENGTH_SHORT
                            ).show()

                            setLogin(result.data.loginResult)
                        }
                        is Result.Error -> {
                            progressBar.visibility = View.GONE
                            btnSignIn.isEnabled = true
                            Toast.makeText(
                                context,
                                R.string.sign_in_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun moveToRegisterFragment() {
        val registerFragment = RegisterFragment()
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().apply {
            replace(R.id.frame_container, registerFragment, RegisterFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
        }
    }

    private fun toggleLoginPasswordVisibility(isChecked: Boolean) {
        appBinding.apply {
            val selection = edLoginPassword.selectionEnd

            if (isChecked) {
                edLoginPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                edLoginPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            edLoginPassword.setSelection(selection)
        }
    }

    private fun clearFocusOnDoneAction(actionId: Int) : Boolean {
        appBinding.apply {
            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edLoginPassword.clearFocus()
                imm.hideSoftInputFromWindow(edLoginPassword.windowToken, 0)
                return true
            }

            return false
        }
    }

    private fun setLogin(loginResult: LoginResultResponse) { loginResult.apply { appViewModel.setLogin(UserEntity(userId, name, token)) } }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu_1, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_insert -> {
                startActivity(Intent(context, InsertActivity::class.java))
                true
            }
            R.id.menu_setting -> {
                startActivity(Intent(context, SettingActivity::class.java))
                true
            }
            else -> true
        }
    }
}