package io.synople.scanmoney;

import android.content.Context;
import android.media.Image;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import io.synople.scanmoney.model.Money;

import java.util.ArrayList;
import java.util.List;

public class Recognizer {

    private AmazonRekognitionClient client;

    public Recognizer(Context context) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:b6109a34-c24d-4fda-ad92-673c51a384f8", // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        client = new AmazonRekognitionClient(credentialsProvider);
    }

    public List<Money> recognize(Image image) {
        List<Money> results = new ArrayList<>();

        return results;
    }
}