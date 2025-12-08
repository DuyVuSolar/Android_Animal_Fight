package com.kuemiin.animalfight.ui.dialog

import android.content.Context
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseDialogAlert
import com.kuemiin.animalfight.databinding.DialogExitPreviewBinding
import com.kuemiin.animalfight.utils.AnimationUtils

class ExitPreviewDialog(val mContext : Context) : BaseDialogAlert<DialogExitPreviewBinding>(mContext) {

    private var callBackOk : () -> Unit = {}

    fun setUpData(callback : () -> Unit): ExitPreviewDialog {
        callBackOk = callback
        return this
    }

    override fun setAnimation() {
        AnimationUtils.animateDialog(binding.llConfirmParent)
    }

    override fun initView() {
        setTextAndIcon()
        binding.llConfirmParent.setOnClickListener {
            dismiss()
        }

        binding.tvDiscard.setOnClickListener {
            callBackOk.invoke()
            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

    override fun getLayout(): Int = R.layout.dialog_exit_preview

    override fun isCanceledOnTouchOut(): Boolean = true

    private fun setTextAndIcon(){

    }

}