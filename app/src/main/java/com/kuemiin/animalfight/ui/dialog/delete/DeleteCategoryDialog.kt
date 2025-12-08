package com.kuemiin.animalfight.ui.dialog.delete

import android.content.Context
import com.kuemiin.animalfight.R
import com.kuemiin.animalfight.base.BaseDialogAlert
import com.kuemiin.animalfight.databinding.DialogDeleteCategoryBinding
import com.kuemiin.animalfight.utils.AnimationUtils

class DeleteCategoryDialog(mContext : Context) : BaseDialogAlert<DialogDeleteCategoryBinding>(mContext) {

    private var callBackOk : () -> Unit = {}

    fun setUpData(callback : () -> Unit): DeleteCategoryDialog {
        callBackOk = callback
        return this
    }

    override fun setAnimation() {
        AnimationUtils.animateDialog(binding.llConfirmParent)
    }

    override fun initView() {
        setTextAndIcon()
        binding.llConfirmParent.setOnClickListener {
            cancel()
        }

        binding.tvChange.setOnClickListener {
            callBackOk.invoke()
            cancel()
        }
        binding.tvCancel.setOnClickListener {
            cancel()
        }
    }

    override fun getLayout(): Int = R.layout.dialog_delete_category

    override fun isCanceledOnTouchOut(): Boolean = true

    private fun setTextAndIcon(){

    }

}