package com.teco.note.ui.create_account

import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.checkbox.MaterialCheckBox
import com.teco.note.R
import com.teco.note.databinding.ActivityCreateActivityBinding
import com.teco.note.ui.core.BaseActivity
import com.teco.note.ui.home.HomeActivity
import com.teco.note.util.launchActivity
import com.teco.note.util.onTextAfterChange
import com.teco.note.util.togglePasswordView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateAccountActivity :
    BaseActivity<ActivityCreateActivityBinding>(ActivityCreateActivityBinding::inflate) {
    private val viewModel: CreateAccountViewModel by viewModels()
    override fun onActivityCreated() {
        initUiStateListener()
        initActionBar(
            binding.appBar,
            getString(R.string.create_account)
        )
        initViews()
    }

    private fun initUiStateListener() {
        lifecycleScope.launch {
            viewModel.createAccountUiState.collect { createAccountUiState ->
                when (createAccountUiState) {
                    CreateAccountViewModel.CreateAccountUIStates.Initial -> {
                        initInitializeState()
                    }

                    CreateAccountViewModel.CreateAccountUIStates.InvalidEmail -> {
                        binding.tilEmail.error = getString(R.string.enter_valid_email)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.InvalidPassword -> {
                        binding.tilPassword.error = getString(R.string.enter_valid_password)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.Loading -> {
                        initLoadingViews()
                    }

                    CreateAccountViewModel.CreateAccountUIStates.RequireConfPassword -> {
                        binding.tilPasswordConf.error = getString(R.string.enter_conf_password)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.RequireConfPasswordSame -> {
                        binding.tilPasswordConf.error =
                            getString(R.string.confirm_password_must_be_same)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.RequireEmail -> {
                        binding.tilEmail.error = getString(R.string.enter_email)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.RequirePassword -> {
                        binding.tilPassword.error = getString(R.string.enter_password)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.RequiredName -> {
                        binding.tilUserName.error = getString(R.string.enter_user_name)
                    }

                    CreateAccountViewModel.CreateAccountUIStates.Editing -> {}
                    CreateAccountViewModel.CreateAccountUIStates.OnSuccessCreateAccount -> {
                        initInitializeState()
                        navigateToHomeScreen()
                    }

                    is CreateAccountViewModel.CreateAccountUIStates.OnFailCreateAccount -> {
                        initInitializeState()
                        Toast.makeText(
                            this@CreateAccountActivity,
                            createAccountUiState.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        setResult(RESULT_OK)
        launchActivity<HomeActivity>()
        finish()
    }

    private fun initLoadingViews() {
        with(binding) {
            btnLoader.root.isVisible = true
            tilUserName.isEnabled = false
            tilEmail.isEnabled = false
            tilPassword.isEnabled = false
            tilPasswordConf.isEnabled = false
        }
    }

    private fun initInitializeState() {
        with(binding) {
            tilUserName.isEnabled = true
            tilEmail.isEnabled = true
            tilPassword.isEnabled = true
            tilPasswordConf.isEnabled = true

            tilUserName.error = null
            tilEmail.error = null
            tilPassword.error = null
            tilPasswordConf.error = null

            btnLoader.root.isVisible = false
        }
    }

    private fun initViews() {
        with(binding) {
            tilUserName.onTextAfterChange { changedText ->
                viewModel.onEditableState()
                viewModel.userEnteredName = changedText
            }
            tilEmail.onTextAfterChange { changedText ->
                viewModel.userEnteredEmail = changedText
            }
            tilPassword.onTextAfterChange { changedText ->
                viewModel.userEnteredPassword = changedText
            }
            tilPasswordConf.onTextAfterChange { changedText ->
                viewModel.userEnteredConfPassword = changedText
            }
            btnCreateAccount.setOnClickListener {
                viewModel.validateDataAndCreateAccount()
                /*setResult(RESULT_OK)
                finish()*/
            }
            llAlreadyHaveAccount.setOnClickListener {
                finish()
            }
            cbShowPassword.addOnCheckedStateChangedListener { _, state ->
                if (state == MaterialCheckBox.STATE_CHECKED) {
                    tilPassword.togglePasswordView(isShowPassword = true)
                    tilPasswordConf.togglePasswordView(isShowPassword = true)
                } else {
                    tilPassword.togglePasswordView(isShowPassword = false)
                    tilPasswordConf.togglePasswordView(isShowPassword = false)
                }
            }
        }
    }
}