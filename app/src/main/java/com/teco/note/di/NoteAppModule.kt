package com.teco.note.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teco.note.util.NoteAppConstants.KEY_FIRE_STORE_COLLECTION
import com.teco.note.util.NoteAppConstants.KEY_FIRE_STORE_USER_NOTES_COLLECTION
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NoteAppModule {

    @Provides
    @Named(KEY_FIRE_STORE_COLLECTION)
    fun provideFireStoreCollection() = Firebase.firestore.collection("user_notes")

    @Provides
    @Named(KEY_FIRE_STORE_USER_NOTES_COLLECTION)
    fun provideFireStoreNoteCollection(
        @Named(KEY_FIRE_STORE_COLLECTION)
        fireStoreCollection: CollectionReference
    ) = fireStoreCollection.document(Firebase.auth.currentUser?.uid!!).collection("notes")

    @Provides
    fun provideFirebaseUserDocument(
        @Named(KEY_FIRE_STORE_COLLECTION)
        fireStoreCollection: CollectionReference
    ) = fireStoreCollection.document(Firebase.auth.currentUser?.uid!!)

}