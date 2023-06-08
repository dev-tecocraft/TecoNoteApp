package com.teco.note.ui.add_edit_notes

import android.content.Intent
import android.os.Build
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.teco.note.R
import com.teco.note.databinding.ActivityAddEditNoteBinding
import com.teco.note.model.NoteModel
import com.teco.note.ui.core.BaseActivity
import com.teco.note.util.NoteAppConstants.KEY_CODE_NOTE_DELETED
import com.teco.note.util.NoteAppConstants.KEY_CODE_NOTE_UPDATED
import com.teco.note.util.NoteAppConstants.KEY_NOTE_MODEL
import com.teco.note.util.onTextAfterChange
import com.teco.note.util.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditNoteActivity :
    BaseActivity<ActivityAddEditNoteBinding>(ActivityAddEditNoteBinding::inflate) {

    private val viewModel: AddEditNoteViewModel by viewModels()
    var note: NoteModel? = null

    override fun onActivityCreated() {
        initArgs()
        initViews()
        initStateListeners()
    }

    private fun initStateListeners() {
        lifecycleScope.launch { 
            viewModel.addEditUiState.collect{ addEditUiState ->
                when(addEditUiState){
                    AddEditNoteViewModel.AddEditUIState.Editable -> {}
                    AddEditNoteViewModel.AddEditUIState.Initial -> {
                        initInitialStateViews()
                    }
                    AddEditNoteViewModel.AddEditUIState.Loading -> {
                        initLoadingViews()
                    }
                    is AddEditNoteViewModel.AddEditUIState.OnFailedAddEditNote -> {
                        initInitialStateViews()
                    }
                    is AddEditNoteViewModel.AddEditUIState.OnSuccessAddNote -> {
                        initInitialStateViews()
                        setResult(RESULT_OK, Intent().putExtra(KEY_NOTE_MODEL, addEditUiState.noteModel))
                        finish()
                    }
                    is AddEditNoteViewModel.AddEditUIState.OnSuccessEditNote -> {
                        initInitialStateViews()
                        setResult(KEY_CODE_NOTE_UPDATED, Intent().putExtra(KEY_NOTE_MODEL, addEditUiState.noteModel))
                        onBackPressedDispatcher.onBackPressed()
                    }
                    is AddEditNoteViewModel.AddEditUIState.OnSuccessDeleteNote -> {
                        initInitialStateViews()
                        setResult(KEY_CODE_NOTE_DELETED, Intent().putExtra(KEY_NOTE_MODEL, addEditUiState.noteModel))
                        onBackPressedDispatcher.onBackPressed()
                    }
                    AddEditNoteViewModel.AddEditUIState.RequiredTitle -> {
                        binding.tilNoteTitle.error = getString(R.string.enter_note_title)
                    }
                }
            }
        }
    }

    private fun initLoadingViews() {
        with(binding){
            tilNoteTitle.isEnabled = false
            tilNoteDesc.isEnabled = false
            btnSaveNote.isEnabled = false
        }
    }

    private fun initInitialStateViews() {
        with(binding){
            tilNoteTitle.isEnabled = true
            tilNoteDesc.isEnabled = true
            btnSaveNote.isEnabled = true
        }
    }

    private fun initViews() {
        initActionBar(
            binding.appBar,
            if(note == null) getString(R.string.new_note) else getString(R.string.edit_note)
        )
        with(binding) {
            note?.let {
                appBar.ivDelete.apply {
                    isVisible = true
                    setOnClickListener {
                        onNoteDeleteClick()
                    }
                }
            }
            tilNoteTitle.apply {
                onTextAfterChange {
                    viewModel.userEnteredTitle = it
                    viewModel.onEditState()
                }
                editText?.setText(note?.title)
            }
            tilNoteDesc.apply {
                onTextAfterChange {
                    viewModel.userEnteredDesc = it
                }
                editText?.setText(note?.noteDesc)
            }
            btnSaveNote.setOnClickListener {
                note?.let {
                    viewModel.updateNote(it)
                } ?: viewModel.validateAndAddNote()
            }
        }
    }

    private fun onNoteDeleteClick() {
        showAlertDialog(
            title = "Delete Note!!",
            message = "Are you sure want to delete note?",
            positiveButtonTitle = "Delete",
            negativeButtonTitle = "Cancel"
        ){ isPositiveButtonClick ->
            if(isPositiveButtonClick){
                viewModel.deleteNote(note)
            }
        }
    }

    private fun initArgs() {
        note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(KEY_NOTE_MODEL, NoteModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(KEY_NOTE_MODEL)
        }
    }
}