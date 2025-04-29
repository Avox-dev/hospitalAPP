package com.example.compose

object RootCheck {
    init {
        System.loadLibrary("rootcheck")
    }

    external fun isDeviceRootedNative(): Boolean
}