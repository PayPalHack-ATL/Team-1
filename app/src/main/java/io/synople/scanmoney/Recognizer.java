package io.synople.scanmoney;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.*;
import io.synople.scanmoney.model.Money;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Recognizer {
    private AmazonRekognitionClient client;

    public Recognizer(Context context) {
        client = new AmazonRekognitionClient(new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:51ce95b9-0d3d-4eb6-b729-1d40d5b39ba7", // Identity pool ID
                Regions.US_EAST_1 // Region
        ));
    }

    public List<Money> getFaces(Image image) {
        IndexFacesRequest request = new IndexFacesRequest().withImage(convertImage(image));
        request.setCollectionId("bills");
        request.setDetectionAttributes(new ArrayList<String>() {{
            add("ALL");
        }});
        IndexFacesResult result = client.indexFaces(request);
        List<FaceRecord> records = result.getFaceRecords();

        List<Money> moneyList = new ArrayList<>();
        for (FaceRecord face : records) {
            String id = face.getFace().getFaceId();
            SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                    .withCollectionId("bills")
                    .withFaceId(id)
                    .withFaceMatchThreshold(70F);
            List<FaceMatch> matches = client.searchFaces(searchFacesRequest).getFaceMatches();
            while (matches.get(0).getFace().getExternalImageId() == null) {
                matches.remove(0);
            }
            String bestMatch = matches.get(0).getFace().getExternalImageId();
            face.getFace().setExternalImageId(bestMatch);
            BoundingBox box = face.getFaceDetail().getBoundingBox();
            moneyList.add(new Money(box.getTop() + box.getHeight() * image.getHeight(),
                    box.getTop() + box.getHeight() * image.getWidth(),
                    bestMatch));
        }

        return moneyList;
    }

    private static com.amazonaws.services.rekognition.model.Image convertImage(Image image) {
        byte[] data = NV21toJPEG(
                YUV_420_888toNV21(image),
                image.getWidth(), image.getHeight());
        return new com.amazonaws.services.rekognition.model.Image().withBytes(ByteBuffer.wrap(data));
    }

    private static byte[] YUV_420_888toNV21(Image image) {
        byte[] nv21;
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();
        nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);
        return nv21;
    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }
}
