package io.synople.scanmoney

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.fragment_ar.*
import com.amazonaws.regions.Regions
import com.amazonaws.auth.CognitoCachingCredentialsProvider


class MainFragment : Fragment() {

    lateinit var recognizer: Recognizer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recognizer = Recognizer(context)

        scan.setOnClickListener {
            val image = (uxFragment as ArFragment).arSceneView.arFrame.acquireCameraImage()

            val money = recognizer.recognize(image)
            Log.v("Money", money.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
