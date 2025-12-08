package com.kuemiin.animalfight.ui.dialog

import android.content.Context
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseDialogAlert
import com.kuemiin.animalfight.databinding.DialogInternetBinding
import com.kuemiin.animalfight.utils.AnimationUtils
import com.kuemiin.animalfight.utils.NetworkUtils

class NoInternetDialog(mContext : Context) : BaseDialogAlert<DialogInternetBinding>(mContext) {

    private var callBackOk : () -> Unit = {}

    fun setUpData(callback : () -> Unit): NoInternetDialog {
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
        binding.tvTryAgain.setOnClickListener {
            if(NetworkUtils.isNetworkConnected(context)){
                callBackOk.invoke()
                dismiss()
            }
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

    override fun getLayout(): Int = R.layout.dialog_internet

    override fun isCanceledOnTouchOut(): Boolean = true

    private fun setTextAndIcon(){

    }

}