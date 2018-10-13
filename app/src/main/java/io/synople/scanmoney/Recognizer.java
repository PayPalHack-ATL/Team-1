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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Recognizer {
    private AmazonRekognitionClient client;
    private Image image;

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

    public Recognizer(Context context) {
        client = new AmazonRekognitionClient(new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:51ce95b9-0d3d-4eb6-b729-1d40d5b39ba7", // Identity pool ID
                Regions.US_EAST_1 // Region
        ));
    }

    public List<FaceRecord> getFaces(Image image) {
        this.image = image;
//        GetFaceDetectionResult result = client.getFaceDetection(new GetFaceDetectionRequest().);
//         img = new com.amazonaws.services.rekognition.model.Image().
//        DetectFacesRequest request = new DetectFacesRequest().withImage(convertImage(image)).withAttributes(Attribute.ALL.toString());
//        DetectFacesResult result = client.detectFaces(request);
        Log.v("Recognizer", convertImage(image).toString());
        IndexFacesRequest request = new IndexFacesRequest().withImage(convertImage(image));
        request.setCollectionId("bills");
        request.setDetectionAttributes(new ArrayList<String>() {{
            add("ALL");
        }});
        IndexFacesResult result = client.indexFaces(request);
        Log.v("Recognizer", result.toString());
        List<FaceRecord> records = result.getFaceRecords();
        Log.d("Recognizer", records.toString());
        matchFaces(records);

        return records;
    }
//    public void matchFaces(List<FaceRecord> faces) {
//        for(FaceRecord face: faces) {
////            face.getFace()
//            com.amazonaws.services.rekognition.model.Image faceImage = new com.amazonaws.services.rekognition.model.Image().setS3Object(new S3Object().withName(face.getFace().getImageId()));
//            SearchFacesByImageRequest request = new SearchFacesByImageRequest().setImage(face.getFace().getImageId()).withCollectionId("bills");
//            client.searchFacesByImage(new SearchFacesByImageRequest())
//
//
//
//        }
//
//    }

    public void matchFaces(List<FaceRecord> faces) {
        for (FaceRecord face : faces) {
            String id = face.getFace().getFaceId();
            SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                    .withCollectionId("bills")
                    .withFaceId(id)
                    .withFaceMatchThreshold(70F);
            List<FaceMatch> matches = client.searchFaces(searchFacesRequest).getFaceMatches();
            String bestMatch = matches.get(0).getFace().getExternalImageId();
            face.getFace().setExternalImageId(bestMatch);
            Log.d("Recognizer", bestMatch);
        }
    }
}
