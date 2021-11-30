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
        topLeft.put("x", 0);
        topLeft.put("y", 0);

        HashMap topRight = new HashMap();
        topRight.put("x", 1);
        topRight.put("y", 0);

        HashMap bottomRight = new HashMap();
        bottomRight.put("x", 1);
        bottomRight.put("y", 1);

        HashMap bottomLeft = new HashMap();
        bottomLeft.put("x", 0);
        bottomLeft.put("y", 1);

        rectangleCoordinates.put("topLeft", topLeft);
        rectangleCoordinates.put("topRight", topRight);
        rectangleCoordinates.put("bottomRight", bottomRight);
        rectangleCoordinates.put("bottomLeft", bottomLeft);

        return rectangleCoordinates;
    }

    public HashMap previewPointsAsHash() {
        HashMap rectangleCoordinates = new HashMap();

        HashMap topLeft = new HashMap();
        topLeft.put("x", this.originalPoints[0].x / this.originalSize.width);
        topLeft.put("y", this.originalPoints[0].y / this.originalSize.height);

        HashMap topRight = new HashMap();
        topRight.put("x", this.originalPoints[1].x / this.originalSize.width);
        topRight.put("y", this.originalPoints[1].y / this.originalSize.height);

        HashMap bottomRight = new HashMap();
        bottomRight.put("x", this.originalPoints[2].x / this.originalSize.width);
        bottomRight.put("y", this.originalPoints[2].y / this.originalSize.height);

        HashMap bottomLeft = new HashMap();
        bottomLeft.put("x", this.originalPoints[3].x / this.originalSize.width);
        bottomLeft.put("y", this.originalPoints[3].y / this.originalSize.height);

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
