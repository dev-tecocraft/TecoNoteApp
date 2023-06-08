package com.teco.note.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.teco.note.R
import com.teco.note.databinding.ActivityLoginBinding
import com.teco.note.util.launchActivity
import com.teco.note.util.onTextAfterChange
import com.teco.note.ui.create_account.CreateAccountActivity
import com.teco.note.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels()


    private var activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUserIsAlreadyLoginOrNot()
        initStateListener()
        initViews()
    }

    private fun initStateListener() {
        lifecycleScope.launch {
            viewModel.loginUiState.collect { loginUiState ->
                when (loginUiState) {
                    LoginViewModel.LoginUIState.Editing -> {}
                    LoginViewModel.LoginUIState.InValidEmail -> {
                        binding.tilEmail.error = getString(R.string.enter_valid_email)
                    }

                    LoginViewModel.LoginUIState.Initial -> {
                        initInitializeViews()
                    }

                    LoginViewModel.LoginUIState.Loading -> {
                        initLoadingViews()
                    }

                    is LoginViewModel.LoginUIState.OnFailureLogin -> {
                        initInitializeViews()
                        Toast.makeText(
                            this@LoginActivity,
                            loginUiState.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    LoginViewModel.LoginUIState.OnSuccessLogin -> {
                        navigateToHomeScreen()
                    }

                    LoginViewModel.LoginUIState.RequireEmail -> {
                        binding.tilEmail.error = getString(R.string.enter_email)
                    }

                    LoginViewModel.LoginUIState.RequirePassword -> {
                        binding.tilPassword.error = getString(R.string.enter_password)
                    }
                }
            }
        }
    }

    private fun initInitializeViews() {
        with(binding) {
            tilEmail.isEnabled = true
            tilPassword.isEnabled = true
            btnLoader.root.isVisible = false
        }
    }

    private fun initLoadingViews() {
        with(binding) {
            tilEmail.isEnabled = false
            tilPassword.isEnabled = false
            btnLoader.root.isVisible = true
        }
    }


    private fun checkUserIsAlreadyLoginOrNot() {
        if (viewModel.isUserAlreadyLogin) {
            navigateToHomeScreen()
        }
    }

    private fun navigateToHomeScreen() {
        launchActivity<HomeActivity>()
        finish()
    }

    private fun initViews() {
        with(binding) {
            tilEmail.onTextAfterChange {
                viewModel.onEditableState()
                viewModel.userEnteredEmail = it
            }
            tilPassword.onTextAfterChange {
                viewModel.onEditableState()
                viewModel.userEnteredPassword = it
            }
            btnLogin.setOnClickListener {
                viewModel.validateDataAndLogin()
            }
            llCreateAccount.setOnClickListener {
                onCreateAccountClick()
            }
        }
    }

    private fun onCreateAccountClick() {
        launchActivity<CreateAccountActivity>(activityLauncher)
    }
}