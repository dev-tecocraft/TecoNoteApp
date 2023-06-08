package com.teco.note.ui.home

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teco.note.R
import com.teco.note.adapter.NoteListAdapter
import com.teco.note.databinding.ActivityHomeBinding
import com.teco.note.databinding.ItemListNoteBinding
import com.teco.note.model.NoteModel
import com.teco.note.ui.add_edit_notes.AddEditNoteActivity
import com.teco.note.ui.core.BaseActivity
import com.teco.note.ui.login.LoginActivity
import com.teco.note.util.GridDividerItemDecoration
import com.teco.note.util.NoteAppConstants.KEY_CODE_NOTE_DELETED
import com.teco.note.util.NoteAppConstants.KEY_CODE_NOTE_UPDATED
import com.teco.note.util.NoteAppConstants.KEY_NOTE_MODEL
import com.teco.note.util.launchActivity
import com.teco.note.util.showAlertDialog
import dagger.hilt.android.AndroidEntryPoint


@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(ActivityHomeBinding::inflate) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var noteListAdapter: NoteListAdapter

    private var createNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                activityResult.data?.let { intent ->
                    val noteModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(KEY_NOTE_MODEL, NoteModel::class.java)
                    } else {
                        intent.getParcelableExtra(KEY_NOTE_MODEL)
                    }

                    noteModel?.let {
                        noteListAdapter.addNote(it)
                    }
                }
            }
        }

    private var updateNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if(activityResult.resultCode == KEY_CODE_NOTE_UPDATED){
                activityResult.data?.let { intent ->
                    val noteModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(KEY_NOTE_MODEL, NoteModel::class.java)
                    } else {
                        intent.getParcelableExtra(KEY_NOTE_MODEL)
                    }

                    noteModel?.let {
                        noteListAdapter.updateNote(it)
                    }
                }
            }else if(activityResult.resultCode == KEY_CODE_NOTE_DELETED){
                activityResult.data?.let { intent ->
                    val noteModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(KEY_NOTE_MODEL, NoteModel::class.java)
                    } else {
                        intent.getParcelableExtra(KEY_NOTE_MODEL)
                    }

                    noteModel?.let {
                        noteListAdapter.removeNote(it)
                    }
                }
            }
        }

    override fun onActivityCreated() {
        initViews()
        fetchNotes()
    }

    private fun fetchNotes() {
        viewModel.fetchNotes { notes ->
            noteListAdapter.addNotes(notes)
            if (notes.isEmpty()) {
                initNoNotesView()
            }
        }
    }

    private fun initNoNotesView() {
        binding.llNoNotes.isVisible = true
    }

    private fun initViews() {
        noteListAdapter = NoteListAdapter { noteModel: NoteModel, binding: ItemListNoteBinding ->
            openNote(noteModel, binding)
        }
        with(binding) {
            appBar.ivLogout.apply {
                initActionBar(
                    appBar,
                    getString(R.string.app_name)
                )
                isVisible = true
                setOnClickListener {
                    onLogoutClick()
                }
            }

            viewModel.getTimeGreetingMessage { greetingMessage ->
                tvGreetingMessage.text = greetingMessage
            }
            rvNotes.apply {
                layoutManager = GridLayoutManager(this@HomeActivity, 2)
                adapter = noteListAdapter
                addItemDecoration(
                    GridDividerItemDecoration(15)
                )
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0 || dy < 0 && btnSaveNote.isShown) {
                            btnSaveNote.hide()
                        }
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                            btnSaveNote.show()
                        }
                        super.onScrollStateChanged(recyclerView, newState)
                    }
                })
            }
            btnSaveNote.setOnClickListener {
                onCreateNewNoteClick()
            }
        }
    }

    private fun onLogoutClick() {
        showAlertDialog(
            title = "Logout!!",
            message = "Are you sure want to logout?",
            positiveButtonTitle = "Logout",
            negativeButtonTitle = "Cancel"
        ){ isPositiveButtonClick ->
            if(isPositiveButtonClick){
                viewModel.logout()
                navigateToLoginScreen()
            }
        }
    }

    private fun navigateToLoginScreen() {
        launchActivity<LoginActivity>()
        finish()
    }

    private fun onCreateNewNoteClick() {
        launchActivity<AddEditNoteActivity>(activityLauncher = createNoteLauncher)
    }

    private fun openNote(noteModel: NoteModel, binding: ItemListNoteBinding) {
        val titleSharedElement =
            Pair.create<View, String>(binding.tvTitle, getString(R.string.transition_title))
        val descSharedElement =
            Pair.create<View, String>(binding.tvDesc, getString(R.string.transition_desc))
        val fabSharedElement =
            Pair.create<View, String>(this.binding.btnSaveNote, getString(R.string.transition_fab))
        val transitionActivityOptions: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@HomeActivity,
                titleSharedElement,
                descSharedElement,
                fabSharedElement
            )
        updateNoteLauncher.launch(
            Intent(this@HomeActivity, AddEditNoteActivity::class.java).apply {
                putExtra(KEY_NOTE_MODEL, noteModel)
            },
            transitionActivityOptions
        )
    }
}