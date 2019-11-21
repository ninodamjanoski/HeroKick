package com.endumedia.herokick.util

import androidx.navigation.NavOptions
import com.endumedia.herokick.R

/**
 * Created by Nino on 21.01.19
 */
object CustomNavOptions {


    fun forward(launchSingleTop : Boolean = true): NavOptions.Builder {
        return  NavOptions.Builder()
                .setLaunchSingleTop(launchSingleTop)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
    }


    fun backward(launchSingleTop : Boolean = true): NavOptions.Builder {
        return NavOptions.Builder()
                .setLaunchSingleTop(launchSingleTop)
                .setEnterAnim(R.anim.slide_in_left)
                .setExitAnim(R.anim.slide_out_right)
    }


    fun up(launchSingleTop : Boolean = true): NavOptions.Builder {
        return NavOptions.Builder()
                .setLaunchSingleTop(launchSingleTop)
                .setEnterAnim(R.anim.slide_in_up)
    }



    fun down(launchSingleTop : Boolean = true): NavOptions.Builder {
        return NavOptions.Builder()
                .setLaunchSingleTop(launchSingleTop)
                .setExitAnim(R.anim.slide_out_up)
    }
}