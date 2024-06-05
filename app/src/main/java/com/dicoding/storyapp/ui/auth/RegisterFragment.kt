package com.dicoding.storyapp.ui.auth
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.repository.Result.Error
import com.dicoding.storyapp.data.repository.Result.Loading
import com.dicoding.storyapp.data.repository.Result.Success
import com.dicoding.storyapp.data.source.remote.request.RegisterRequest
import com.dicoding.storyapp.databinding.FragmentRegisterBinding
import com.dicoding.storyapp.factory.ViewModelFactory
import com.dicoding.storyapp.ui.insertStory.InsertActivity
import com.dicoding.storyapp.ui.main.MainViewModel
import com.dicoding.storyapp.ui.setting.SettingActivity
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



class RegisterFragment : Fragment() {

    private lateinit var appBinding: FragmentRegisterBinding
    private lateinit var appViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appBinding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        appBinding.apply {
            edRegisterPassword.apply {
                setOnEditorActionListener { _, actionId, _ -> clearFocusOnDoneAction(actionId) }
            }

            cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                toggleLoginPasswordVisibility(isChecked)
            }

            tvSignIn.setOnClickListener { moveToLoginFragment() }

            btnSignUp.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                register(name, email, password)
            }
        }
    }

    private fun clearFocusOnDoneAction(actionId: Int) : Boolean {
        appBinding.apply {
            val imm = requireContext().getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edRegisterPassword.clearFocus()
                edRegisterPassword.error = null
                imm.hideSoftInputFromWindow(edRegisterPassword.windowToken, 0)
                return true
            }

            return false
        }
    }

    private fun toggleLoginPasswordVisibility(isChecked: Boolean) {
        appBinding.apply {
            val selection = edRegisterPassword.selectionEnd

            if (isChecked) {
                edRegisterPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                edRegisterPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            edRegisterPassword.setSelection(selection)
            edRegisterPassword.error = null
        }
    }

    private fun setupViewModel() {
        appViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(requireContext())
        )[MainViewModel::class.java]
    }


    private fun register(name: String, email: String, password: String) {
        appBinding.apply {
            when {
                name.isEmpty() -> {
                    edRegisterName.error = R.string.please_fill_your_name.toString()
                }
                email.isEmpty() -> {
                    edRegisterEmail.error = R.string.email_mandatory.toString()
                }
                password.isEmpty() -> {
                    edRegisterPassword.error = R.string.password_mandatory.toString()
                }
                password.length < 8 -> {
                    edRegisterPassword.error = R.string.password_must_be_at_least_8_character.toString()
                }
                else -> {
                    executeRegister(name, email, password)
                }
            }
        }
    }


    private fun executeRegister(name: String, email: String, password: String) {
        appBinding.apply {
            appViewModel.register(
                RegisterRequest(name, email, password)
            ).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Loading -> {
                            progressBar.visibility = View.VISIBLE
                            btnSignUp.isEnabled = false
                        }
                        is Success -> {
                            progressBar.visibility = View.GONE
                            btnSignUp.isEnabled = true
                            Toast.makeText(context,
                                R.string.account_success,
                                Toast.LENGTH_SHORT
                            ).show()
                            moveToLoginFragment()
                        }
                        is Error -> {
                            btnSignUp.isEnabled = true
                            Toast.makeText(context,
                                R.string.account_failed,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun moveToLoginFragment() {
        val fragmentManager = parentFragmentManager
        fragmentManager.popBackStack()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu_1, menu)
    }


}