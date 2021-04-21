package com.zzs.n1.bean

/**
@author  zzs
@Date 2021/4/21
@describe
 */
data class NetBean(val keycode: Int, val longclick: Boolean = false) {
    override fun toString(): String {
        return "{\"keycode\":$keycode,\"longclick\":$longclick}"
    }
}

data class Action(val action: String = "setting") {
    override fun toString(): String {
        return "{\"action\":\"$action\"}"
    }
}