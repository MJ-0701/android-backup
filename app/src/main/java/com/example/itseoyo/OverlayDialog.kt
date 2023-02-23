package com.example.itseoyo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment

class OverlayDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = "다른앱 위에 표시"
        val message = "권한 설정 알림창"
        val clickListener =
            OnClickListener { _, _ ->
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                intent.data = Uri.parse("package:" + requireContext().packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
            .setTitle(title)
            .setPositiveButton("확인", clickListener)
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}