package com.teco.note.ui.add_edit_notes

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.teco.note.model.NoteModel
import com.teco.note.util.NoteAppConstants.KEY_FIRE_STORE_USER_NOTES_COLLECTION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_USER_NOTES_COLLECTION)
    private val noteCollection: CollectionReference
) : ViewModel() {

    var userEnteredTitle = ""
    var userEnteredDesc = ""

    private var _addEditNoteUiState = MutableStateFlow<AddEditUIState>(AddEditUIState.Initial)
    val addEditUiState = _addEditNoteUiState.asStateFlow()

    private fun isValidData(): Boolean {
        var isValidData = true
        if (userEnteredTitle.isEmpty()) {
            isValidData = false
            _addEditNoteUiState.value = AddEditUIState.RequiredTitle
        }
        return isValidData
    }

    fun validateAndAddNote() {
        if (isValidData()) {
            _addEditNoteUiState.value = AddEditUIState.Loading
            noteCollection.document().apply {
                val noteModel = NoteModel(
                    noteId = this.id,
                    title = userEnteredTitle,
                    noteDesc = userEnteredDesc,
                    isImportant = false,
                    date = Date().time
                )
                set(noteModel).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _addEditNoteUiState.value = AddEditUIState.OnSuccessAddNote(noteModel)
                    } else {
                        _addEditNoteUiState.value = AddEditUIState.OnFailedAddEditNote(
                            task.exception?.message ?: "Something went wrong"
                        )
                    }
                }
            }
        }
    }

    fun onEditState() {
        _addEditNoteUiState.value = AddEditUIState.Editable
    }

    fun updateNote(note: NoteModel) {
        if (isValidData()) {
            _addEditNoteUiState.value = AddEditUIState.Loading
            note.noteId?.let {
                note.apply {
                    title = userEnteredTitle
                    noteDesc = userEnteredDesc
                    date = Date().time
                }
                noteCollection.document(note.noteId)
                    .set(note)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _addEditNoteUiState.value = AddEditUIState.OnSuccessEditNote(note)
                        } else {
                            _addEditNoteUiState.value = AddEditUIState.OnFailedAddEditNote(
                                task.exception?.message ?: "Something went wrong"
                            )
                        }
                    }
            }

        }
    }

    fun deleteNote(note: NoteModel?) {
        note?.noteId?.let {
            _addEditNoteUiState.value = AddEditUIState.Loading
            noteCollection.document(note.noteId)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _addEditNoteUiState.value = AddEditUIState.OnSuccessDeleteNote(note)
                    } else {
                        _addEditNoteUiState.value = AddEditUIState.OnFailedAddEditNote(
                            task.exception?.message ?: "Something went wrong"
                        )
                    }
                }
        }
    }

    sealed class AddEditUIState {
        object Initial : AddEditUIState()
        object Editable : AddEditUIState()
        object RequiredTitle : AddEditUIState()
        object Loading : AddEditUIState()
        data class OnSuccessAddNote(val noteModel: NoteModel) : AddEditUIState()
        data class OnSuccessEditNote(val noteModel: NoteModel) : AddEditUIState()
        data class OnSuccessDeleteNote(val noteModel: NoteModel) : AddEditUIState()
        data class OnFailedAddEditNote(val errorMessage: String) : AddEditUIState()
    }

}