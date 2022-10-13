package com.example.bledemoapp.utils

import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.bledemoapp.R
import com.example.bledemoapp.databinding.DialogAlertBinding

/**
 * Custom Alertview to display alert messages to user.
 *
 * @author Aanal Shah
 */
open class AlertView(private var activity: FragmentActivity?) {

    companion object {
        // is alert currently showing or not
        var isAlertShowing = false
    }

    private var dialog: AlertDialog? = null

    /**
     * Show alert message to user
     *
     * @param msg message string to display
     * @param title title string to display.if not set title will not display
     * @param positiveButton action button string for positive button
     * @param positiveButtonListener listener to invoke whenever user click on positive button.
     * dialog will dismiss if not listener provided
     * @param negativeButton action button string for negative button.if not set negative button will not display
     * @param negativeButtonListener listener to invoke whenever user click on negative button.
     * dialog will dismiss if not listener provided
     */
    fun showAlert(
        msg: String,
        title: String? = "",
        positiveButton: String? = activity?.getString(R.string.dialog_ok),
        positiveButtonListener: (() -> Unit)? = null,
        negativeButton: String? = null,
        negativeButtonListener: (() -> Unit)? = null
    ): AlertView {
        activity?.let {
            try {
                val views =
                    DialogAlertBinding.inflate(activity!!.layoutInflater)
                positiveButton?.let {
                    views.btnPositive.text = positiveButton
                    views.btnPositive.setOnClickListener {
                        positiveButtonListener?.invoke()
                        dismissAlert()
                    }
                }
                negativeButton?.let {
                    views.btnNegative.visibility = View.VISIBLE
                    views.btnNegative.text = negativeButton
                    views.btnNegative.setOnClickListener {
                        negativeButtonListener?.invoke()
                        dismissAlert()
                    }
                }
                views.tvMsg.text = msg
                if (title.isNullOrEmpty()) {
                    views.tvTitle.visibility = View.GONE
                }
                if (msg.isEmpty()) {
                    views.tvMsg.visibility = View.GONE
                }
                views.tvTitle.text = title
                dialog = AlertDialog.Builder(activity!!, R.style.AlertDialogTheme).apply {
                    setView(views.root)
                    setCancelable(false)
                }.create()
                isAlertShowing = true
                dialog?.setOnShowListener {
                    views.btnNegative.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        views.btnPositive.textSize
                    )
                }
                dialog?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return this
    }

    /**
     * Dismiss alert message
     */
    private fun dismissAlert() {
        dialog?.dismiss()
        isAlertShowing = false
    }

}
