package io.synople.scanmoney

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.services.rekognition.AmazonRekognitionClient
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.fragment_ar.*
import com.amazonaws.regions.Regions
import com.amazonaws.auth.CognitoCachingCredentialsProvider


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
        val credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            "us-east-1:b6109a34-c24d-4fda-ad92-673c51a384f8", // Identity pool ID
            Regions.US_EAST_1 // Region
        )

        val client = AmazonRekognitionClient(credentialsProvider)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}
