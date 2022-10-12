package com.example.bledemoapp.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.bledemoapp.R
import com.example.bledemoapp.databinding.ViewProgressdialogBinding

/**
 * App displays loading progress bar whenever app is doing any long running blocking tasks like
 * Calling API,Saving data in DB, Establishing connection with BLE device,etc..
 */
object ProgressUtils {

    // Single instance progress dialog
    private var progressDialog: Dialog? = null

    /**
     * Check whether progress bar is currently visible or not
     */
    val isShowing: Boolean
        get() = progressDialog != null

    /**
     * Show progress dialog to user
     * @param context current activity context
     * @param msg optional message to display to user.
     */
    fun showProgressDialog(context: Context, msg: String = "") {
        if (progressDialog == null || !progressDialog!!.isShowing) {
            progressDialog = Dialog(context)
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val mInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val views = ViewProgressdialogBinding.inflate(mInflater)
            if (msg.isNotEmpty()) {
                views.progressMsg.text = msg
            }
            progressDialog?.setContentView(views.root)
            progressDialog?.show()
        }
    }

    fun forceShowProgressDialog(context: Context) {
        progressDialog = Dialog(context)
        progressDialog?.setCancelable(true)
        progressDialog?.setContentView(R.layout.view_progressdialog)
        progressDialog?.show()
    }

    /**
     * Hide progress dialog if currently showing
     */
    fun hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog?.dismiss()
                progressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            progressDialog = null
        }
    }
}
