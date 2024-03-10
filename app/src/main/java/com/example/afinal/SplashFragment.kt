package com.example.afinal

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.afinal.databinding.SplashLoginScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var binding: SplashLoginScreenBinding

    private val animatorSet = AnimatorSet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SplashLoginScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation(binding.image)
        val googleSignInInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("807068749773-94nm967ar1fb6n09rkoub3ektc6kvihk.apps.googleusercontent.com")
                .requestEmail()
                .build()
        val googleSignInClient =
            GoogleSignIn.getClient(requireContext(), googleSignInInOptions)
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val activity = requireActivity() as OnAuthLaunch
        if (account == null) {
            showSignInButton()
        } else {
            activity.showListFragment()
        }
        binding.signInButton.setOnClickListener {
            activity.launch(googleSignInClient.signInIntent)
        }
    }

    private fun showSignInButton() {
        binding.signInButton.visibility = View.VISIBLE
        animatorSet.cancel()
    }

    private fun startAnimation(image: ImageView) {
        val scaleXAnimation = ObjectAnimator.ofFloat(
            image, View.SCALE_X, 0.5f,
            1f
        )
        scaleXAnimation.repeatMode = ObjectAnimator.REVERSE
        scaleXAnimation.repeatCount = ObjectAnimator.INFINITE
        val scaleYAnimation = ObjectAnimator.ofFloat(
            image, View.SCALE_Y, 0.5f,
            1f
        )
        scaleYAnimation.repeatMode = ObjectAnimator.REVERSE
        scaleYAnimation.repeatCount = ObjectAnimator.INFINITE
        animatorSet.playTogether(scaleXAnimation, scaleYAnimation)
        animatorSet.duration = 1000
        animatorSet.start()
    }
}