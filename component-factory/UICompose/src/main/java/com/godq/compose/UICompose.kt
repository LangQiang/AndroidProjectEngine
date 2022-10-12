package com.godq.compose

import android.content.Context

object UICompose {

    internal var applicationContext: Context? = null

    internal var config: UIComposeConfig? = null

    fun init(context: Context, config: UIComposeConfig) {
        this.applicationContext = context.applicationContext
        this.config = config
    }
}
