package com.example.document_scanner.helpers;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.HashMap;

/**
 * Created by allgood on 05/03/16.
 */
public class ScannedDocument {

    public Mat original;
    public Mat processed;
    public Quadrilateral quadrilateral;
    public Size originalSize;

    public Point[] originalPoints;

    public int heightWithRatio;
    public int widthWithRatio;

    public ScannedDocument(Mat original) {
        this.original = original;
    }

    public static HashMap initScannedDocument() {
        HashMap rectangleCoordinates = new HashMap();

        HashMap topLeft = new HashMap();
        topLeft.put("x", 0.0);
        topLeft.put("y", 0.0);

        HashMap topRight = new HashMap();
        topRight.put("x", 1.0);
        topRight.put("y", 0.0);

        HashMap bottomRight = new HashMap();
        bottomRight.put("x", 1.0);
        bottomRight.put("y", 1.0);

        HashMap bottomLeft = new HashMap();
        bottomLeft.put("x", 0.0);
        bottomLeft.put("y", 1.0);

        rectangleCoordinates.put("topLeft", topLeft);
        rectangleCoordinates.put("topRight", topRight);
        rectangleCoordinates.put("bottomRight", bottomRight);
        rectangleCoordinates.put("bottomLeft", bottomLeft);

        return rectangleCoordinates;
    }

    public HashMap previewPointsAsHash() {
        HashMap rectangleCoordinates = new HashMap();
        double ratio = this.originalSize.height / 500;
        double width = this.originalSize.width;
        double height = this.originalSize.height;

        HashMap topLeft = new HashMap();
        topLeft.put("x", this.originalPoints[0].y * ratio / width);
        topLeft.put("y", 1 - this.originalPoints[0].x * ratio / height);

        HashMap topRight = new HashMap();
        topRight.put("x", this.originalPoints[1].y * ratio / width);
        topRight.put("y", 1 - this.originalPoints[1].x * ratio / height);

        HashMap bottomRight = new HashMap();
        bottomRight.put("x", this.originalPoints[2].y * ratio / width);
        bottomRight.put("y", 1 - this.originalPoints[2].x * ratio / height);

        HashMap bottomLeft = new HashMap();
        bottomLeft.put("x", this.originalPoints[3].y * ratio / width);
        bottomLeft.put("y", 1 - this.originalPoints[3].x * ratio / height);

        rectangleCoordinates.put("topLeft", topLeft);
        rectangleCoordinates.put("topRight", topRight);
        rectangleCoordinates.put("bottomRight", bottomRight);
        rectangleCoordinates.put("bottomLeft", bottomLeft);

        return rectangleCoordinates;
    }

    public void release() {
        if (processed != null) {
            processed.release();
        }
        if (original != null) {
            original.release();
        }

        if (quadrilateral != null && quadrilateral.contour != null) {
            quadrilateral.contour.release();
        }
    }
}
