package com.example.assignment2

import android.icu.text.SimpleDateFormat
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(startDialog: MutableState<Boolean>,
                    endDialog: MutableState<Boolean>,
                    setStartDateString: (String) -> Unit,
                    setEndDateString: (String) -> Unit,) {
    val startState = rememberDatePickerState()
    val endState = rememberDatePickerState()

    val closeDialog: () -> Unit = {
        startDialog.value = false
        endDialog.value = false
    }

    DateDialog(startDialog.value, startState, closeDialog)
    DateDialog(endDialog.value, endState, closeDialog)

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    // selectedDateMillis is returned in UTC milliseconds which is -6hs from CST
    var startDateMillis = startState.selectedDateMillis?.plus(43200000L)
    var endDateMills = endState.selectedDateMillis?.plus(43200000L)

    // Set Values Here
    setStartDateString(simpleDateFormat.format(startDateMillis  ?: Instant.now().minusSeconds(345600).toEpochMilli()))
    setEndDateString(simpleDateFormat.format(endDateMills ?: Instant.now().toEpochMilli()))
}

fun dateValidator(): (Long) -> Boolean {
    return {
            timeInMillis ->
        timeInMillis < Instant.now().toEpochMilli() - 43200000L
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDialog(dialog: Boolean, state: DatePickerState, closeDialog: () -> Unit) {
    if (dialog) {
        DatePickerDialog(
            onDismissRequest = {
                closeDialog()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        closeDialog()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        closeDialog()
                    }
                ) {
                    Text("CANCEL")
                }
            }
        ) {
            DatePicker(
                dateValidator = dateValidator() ,
                state = state
            )
        }
    }
}
