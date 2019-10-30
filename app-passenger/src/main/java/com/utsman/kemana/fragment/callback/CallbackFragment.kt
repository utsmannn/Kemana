package com.utsman.kemana.fragment.callback

interface CallbackFragment {
    fun onCollapse()
    fun onExpand()
    fun onHidden()
    fun onOrder(order: Boolean)
}