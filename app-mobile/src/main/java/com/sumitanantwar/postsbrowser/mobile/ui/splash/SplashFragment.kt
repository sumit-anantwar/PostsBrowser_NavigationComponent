package com.sumitanantwar.postsbrowser.mobile.ui.splash

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import butterknife.BindView
import com.airbnb.lottie.LottieAnimationView
import com.sumitanantwar.mvi.BaseFragment
import com.sumitanantwar.postsbrowser.mobile.R
import com.sumitanantwar.postsbrowser.mobile.util.AnimatorListenerAdapter
import timber.log.Timber

class SplashFragment : BaseFragment() {

    @BindView(R.id.splash_animation_view)
    lateinit var splashAnimationView: LottieAnimationView

    // Fragment Layout Id
    override val layoutId: Int = R.layout.splash_fragment


    override fun onViewBound(view: View) {

    }

    //======= Fragment Lifecycle =======
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        splashAnimationView.repeatCount = 3
        splashAnimationView.addAnimatorListener(AnimatorListenerAdapter(
            onEnd = {
                Timber.d("Animation End")
                findNavController().navigate(SplashFragmentDirections.actionNext())
            }
        ))
    }

    override fun onStart() {
        super.onStart()

        splashAnimationView.playAnimation()
    }

}
