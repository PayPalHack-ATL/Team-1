package io.synople.scanmoney

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.fragment_ar.*

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image = (uxFragment as ArFragment).arSceneView.arFrame.acquireCameraImage()
        
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
