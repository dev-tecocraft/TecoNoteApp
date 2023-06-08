package com.teco.note.ui.create_account

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import com.teco.note.util.isValidEmail
import com.teco.note.util.isValidPasswordFormat
import com.teco.note.model.NoteAppUser
import com.teco.note.util.NoteAppConstants.KEY_FIRE_STORE_COLLECTION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_COLLECTION)
    private val fireStoreCollection: CollectionReference
) : ViewModel() {

    var userEnteredName = ""
    var userEnteredEmail = ""
    var userEnteredPassword = ""
    var userEnteredConfPassword = ""

    private var firebaseAuth: FirebaseAuth = Firebase.auth

    fun validateDataAndCreateAccount() {
        if (isValidData()) {
            _createAccountUiState.value = CreateAccountUIStates.Loading
            firebaseAuth.createUserWithEmailAndPassword(userEnteredEmail, userEnteredPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        firebaseAuth = Firebase.auth
                        firebaseAuth.currentUser?.let { firebaseUser ->
                            fireStoreCollection.document(firebaseUser.uid).set(
                                NoteAppUser(
                                    firebaseUser.uid,
                                    userEnteredName
                                )
                            ).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    _createAccountUiState.value =
                                        CreateAccountUIStates.OnSuccessCreateAccount
                                } else {
                                    _createAccountUiState.value =
                                        CreateAccountUIStates.OnFailCreateAccount(
                                            task.exception?.message ?: "Something wrong"
                                        )
                                }
                            }
                        }

                    } else {
                        _createAccountUiState.value = CreateAccountUIStates.OnFailCreateAccount(
                            task.exception?.message ?: "Something wrong"
                        )
                    }
                }

        }
    }

    private fun isValidData(): Boolean {
        var isValidData = true
        if (userEnteredName.isEmpty()) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.RequiredName
        }
        if (userEnteredEmail.isEmpty()) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.RequireEmail
        }
        if (userEnteredPassword.isEmpty()) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.RequirePassword
        }
        if (userEnteredConfPassword.isEmpty()) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.RequireConfPassword
        }
        if (userEnteredEmail.isNotEmpty() && !userEnteredEmail.isValidEmail()) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.InvalidEmail
        }
        if (userEnteredPassword.isNotEmpty() && !userEnteredPassword.isValidPasswordFormat()) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.InvalidPassword
        }
        if (userEnteredConfPassword.isNotEmpty() && userEnteredPassword != userEnteredConfPassword) {
            isValidData = false
            _createAccountUiState.value = CreateAccountUIStates.RequireConfPasswordSame
        }
        return isValidData
    }

    fun onEditableState() {
        _createAccountUiState.value = CreateAccountUIStates.Editing
    }

    private var _createAccountUiState: MutableStateFlow<CreateAccountUIStates> =
        MutableStateFlow(CreateAccountUIStates.Initial)
    val createAccountUiState = _createAccountUiState.asStateFlow()

    sealed class CreateAccountUIStates {
        object Initial : CreateAccountUIStates()
        object Editing : CreateAccountUIStates()
        object RequiredName : CreateAccountUIStates()
        object RequireEmail : CreateAccountUIStates()
        object RequirePassword : CreateAccountUIStates()
        object RequireConfPassword : CreateAccountUIStates()
        object InvalidPassword : CreateAccountUIStates()
        object InvalidEmail : CreateAccountUIStates()
        object RequireConfPasswordSame : CreateAccountUIStates()
        object Loading : CreateAccountUIStates()
        object OnSuccessCreateAccount : CreateAccountUIStates()
        data class OnFailCreateAccount(val errorMessage: String) : CreateAccountUIStates()
    }
}

