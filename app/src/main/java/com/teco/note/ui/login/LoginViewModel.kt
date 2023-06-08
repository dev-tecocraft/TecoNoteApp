package com.teco.note.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.teco.note.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private var _loginUiState: MutableStateFlow<LoginUIState> =
        MutableStateFlow(LoginUIState.Initial)
    val loginUiState = _loginUiState.asStateFlow()

    var userEnteredEmail = ""
    var userEnteredPassword = ""

    private var firebaseAuth: FirebaseAuth = Firebase.auth
    val isUserAlreadyLogin: Boolean
        get() = firebaseAuth.currentUser != null

    private fun isValidData(): Boolean {
        var isValidData = true
        if (userEnteredEmail.isEmpty()) {
            isValidData = false
            _loginUiState.value = LoginUIState.RequireEmail
        }
        if (userEnteredPassword.isEmpty()) {
            isValidData = false
            _loginUiState.value = LoginUIState.RequirePassword
        }
        if (userEnteredEmail.isNotEmpty() && !userEnteredEmail.isValidEmail()) {
            isValidData = false
            _loginUiState.value = LoginUIState.InValidEmail
        }
        return isValidData
    }

    fun validateDataAndLogin() {
        if (isValidData()) {
            _loginUiState.value = LoginUIState.Loading
            firebaseAuth.signInWithEmailAndPassword(userEnteredEmail, userEnteredPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginUiState.value = LoginUIState.OnSuccessLogin
                    } else {
                        _loginUiState.value = LoginUIState.OnFailureLogin(
                            task.exception?.message ?: "Authentication Fail"
                        )
                    }
                }
        }
    }

    fun onEditableState() {
        _loginUiState.value = LoginUIState.Editing
    }


    sealed class LoginUIState {
        object Initial : LoginUIState()
        object RequireEmail : LoginUIState()
        object InValidEmail : LoginUIState()
        object RequirePassword : LoginUIState()
        object Editing : LoginUIState()
        object Loading : LoginUIState()
        object OnSuccessLogin : LoginUIState()
        data class OnFailureLogin(val errorMessage: String) : LoginUIState()
    }

}