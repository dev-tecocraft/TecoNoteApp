package com.teco.note.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class NoteModel(
    val noteId: String? = null,
    var title: String? = null,
    var noteDesc: String? = null,
    var isImportant: Boolean = false,
    var date: Long = 0L
) : Parcelable