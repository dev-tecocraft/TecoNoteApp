package com.teco.note.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import androidx.activity.result.ActivityResultLauncher
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.teco.note.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern


inline fun <reified T : Any> Context.launchActivity(
    activityLauncher: ActivityResultLauncher<Intent>? = null,
    bundle: Bundle = Bundle()
) {
    val intent = Intent(
        this@launchActivity,
        T::class.java
    ).apply {
        putExtras(bundle)
    }
    activityLauncher?.launch(intent) ?: startActivity(intent)
}

fun TextInputLayout.onTextAfterChange(changedText: (String) -> Unit) {
    editText?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            error = null
        }

        override fun afterTextChanged(editable: Editable?) {
            editable?.toString()?.let { changedText ->
                editText?.setSelection(changedText.length)
                changedText(changedText)
            }
        }

    })
}

fun TextInputLayout.togglePasswordView(isShowPassword: Boolean) {
    editText?.transformationMethod = if (isShowPassword)
        HideReturnsTransformationMethod.getInstance()
    else
        PasswordTransformationMethod.getInstance()
}

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPasswordFormat(): Boolean {
    val passwordREGEX = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$"
    )
    return passwordREGEX.matcher(this).matches()
}


@SuppressLint("SimpleDateFormat")
fun Long.toDateFormat(): String = SimpleDateFormat("hh:mm a, MMM d yyyy").format(Date(this))

fun Context.showAlertDialog(
    title: String,
    message: String,
    positiveButtonTitle: String,
    negativeButtonTitle: String,
    onDialogButtonClick: (isPositiveButtonClick: Boolean) -> Unit
){
    MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
        .setCancelable(false)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonTitle){ dialogInterface, _ ->
            dialogInterface.dismiss()
            onDialogButtonClick(true)
        }.setNegativeButton(negativeButtonTitle){ dialogInterface, _ ->
            dialogInterface.dismiss()
            onDialogButtonClick(false)
        }.create().show()
}