package com.kuemiin.animalfight.utils.refresh

abstract class MaterialRefreshListener {
    fun onfinish() {}
    abstract fun onRefresh(materialRefreshLayout: MaterialRefreshLayout)
    fun onRefreshLoadMore(materialRefreshLayout: MaterialRefreshLayout) {}
}
