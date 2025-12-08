package com.kuemiin.animalfight.ui.dialog.permission

import android.content.Context
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseDialogAlert
import com.kuemiin.animalfight.databinding.DialogGrantPermissionBinding
import com.kuemiin.animalfight.utils.AnimationUtils

class GrantPermissionDialog(mContext : Context) : BaseDialogAlert<DialogGrantPermissionBinding>(mContext) {

    private var callBackOk : () -> Unit = {}

    fun setUpData(callback : () -> Unit): GrantPermissionDialog {
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

        binding.tvOpenSetting.setOnClickListener {
            callBackOk.invoke()
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

    override fun getLayout(): Int = R.layout.dialog_grant_permission

    override fun isCanceledOnTouchOut(): Boolean = true

    private fun setTextAndIcon(){

    }

}