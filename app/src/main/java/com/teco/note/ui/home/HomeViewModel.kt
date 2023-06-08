package com.teco.note.ui.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.teco.note.model.NoteAppUser
import com.teco.note.model.NoteModel
import com.teco.note.util.NoteAppConstants.KEY_FIRE_STORE_USER_NOTES_COLLECTION
import com.teco.note.util.NoteAppUtil.getGreetingMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    @Named(KEY_FIRE_STORE_USER_NOTES_COLLECTION)
    private val fireStoreCollection: CollectionReference,
    private val userDocumentReference: DocumentReference
) : ViewModel() {

    fun fetchNotes(onSuccessFetchNotes: (notes: ArrayList<NoteModel>) -> Unit) {
        fireStoreCollection.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val noteList = ArrayList<NoteModel>()
                for (note in task.result.documents) {
                    val noteModel = note.toObject<NoteModel>()
                    if (noteModel != null) {
                        noteList.add(noteModel)
                    }
                }
                onSuccessFetchNotes(noteList)
            }
        }
    }

    fun getTimeGreetingMessage(onSuccessGreetingMessage: (String) -> Unit) {
        userDocumentReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.toObject<NoteAppUser>()?.let { noteAppUser ->
                    noteAppUser.name?.getGreetingMessage()?.let {
                        onSuccessGreetingMessage(it)
                    }
                }
            }
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}