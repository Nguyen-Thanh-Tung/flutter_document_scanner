package com.example.document_scanner;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.example.document_scanner.helpers.Quadrilateral;
import com.example.document_scanner.helpers.*;


public class DocumentScannerPlugin
        implements FlutterPlugin, MethodChannel.MethodCallHandler {
  static MethodChannel methodChannel;
  public DocumentScannerPlugin() {}

  // FlutterPlugin
  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {

    final MethodChannel channel = new MethodChannel(binding.getFlutterEngine().getDartExecutor(), "document_scanner");
    channel.setMethodCallHandler(new DocumentScannerPlugin());

    methodChannel= channel;
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {
    methodChannel.setMethodCallHandler(null);
  }


  @Override
  public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
    if (methodCall.method.equals("getRectangle")) {
      final byte[] imageData = methodCall.argument("imageData");
      result.success(processImage(imageData));
    } else if (methodCall.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  private HashMap processImage(byte[] imageData) {
    Mat frame = Imgcodecs.imdecode(new MatOfByte(imageData), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    HashMap data = new HashMap();
    if (detectPreviewDocument(frame)) {
      ScannedDocument doc = detectDocument(frame);
      data = doc.previewPointsAsHash();
      doc.release();
    }
    frame.release();
    return data;
  }

  private boolean detectPreviewDocument(Mat inputRgba) {
    ArrayList<MatOfPoint> contours = findContours(inputRgba);

    Quadrilateral quad = getQuadrilateral(contours, inputRgba.size());
    return quad != null;
  }

  private ScannedDocument detectDocument(Mat inputRgba) {
    ArrayList<MatOfPoint> contours = findContours(inputRgba);

    ScannedDocument sd = new ScannedDocument(inputRgba);

    sd.originalSize = inputRgba.size();
    Quadrilateral quad = getQuadrilateral(contours, sd.originalSize);

    double ratio = sd.originalSize.height / 500;
    sd.heightWithRatio = Double.valueOf(sd.originalSize.width / ratio).intValue();
    sd.widthWithRatio = Double.valueOf(sd.originalSize.height / ratio).intValue();

    if (quad != null) {

      sd.originalPoints = new Point[4];

      sd.originalPoints[0] = new Point(sd.widthWithRatio - quad.points[3].y, quad.points[3].x); // Topleft
      sd.originalPoints[1] = new Point(sd.widthWithRatio - quad.points[0].y, quad.points[0].x); // TopRight
      sd.originalPoints[2] = new Point(sd.widthWithRatio - quad.points[1].y, quad.points[1].x); // BottomRight
      sd.originalPoints[3] = new Point(sd.widthWithRatio - quad.points[2].y, quad.points[2].x); // BottomLeft
      sd.quadrilateral = quad;
    }
    return sd;
  }

  private ArrayList<MatOfPoint> findContours(Mat src) {

    Mat grayImage = null;
    Mat cannedImage = null;
    Mat resizedImage = null;

    double ratio = src.size().height / 500;
    int height = Double.valueOf(src.size().height / ratio).intValue();
    int width = Double.valueOf(src.size().width / ratio).intValue();
    Size size = new Size(width, height);

    resizedImage = new Mat(size, CvType.CV_8UC4);
    grayImage = new Mat(size, CvType.CV_8UC4);
    cannedImage = new Mat(size, CvType.CV_8UC1);

    Imgproc.resize(src, resizedImage, size);
    Imgproc.cvtColor(resizedImage, grayImage, Imgproc.COLOR_RGBA2GRAY, 4);
    Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5), 0);
    Imgproc.Canny(grayImage, cannedImage, 80, 100, 3, false);

    ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    Mat hierarchy = new Mat();

    Imgproc.findContours(cannedImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

    hierarchy.release();

    Collections.sort(contours, new Comparator<MatOfPoint>() {

      @Override
      public int compare(MatOfPoint lhs, MatOfPoint rhs) {
        return Double.valueOf(Imgproc.contourArea(rhs)).compareTo(Imgproc.contourArea(lhs));
      }
    });

    resizedImage.release();
    grayImage.release();
    cannedImage.release();

    return contours;
  }

  private Quadrilateral getQuadrilateral(ArrayList<MatOfPoint> contours, Size srcSize) {

    double ratio = srcSize.height / 500;
    int height = Double.valueOf(srcSize.height / ratio).intValue();
    int width = Double.valueOf(srcSize.width / ratio).intValue();
    Size size = new Size(width, height);

    for (MatOfPoint c : contours) {
      MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
      double peri = Imgproc.arcLength(c2f, true);
      MatOfPoint2f approx = new MatOfPoint2f();
      Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);

      Point[] points = approx.toArray();

      // select biggest 4 angles polygon
      // if (points.length == 4) {
      Point[] foundPoints = sortPoints(points);

      if (insideArea(foundPoints, size)) {

        return new Quadrilateral(c, foundPoints);
      }
      // }
    }

    return null;
  }

  private Point[] sortPoints(Point[] src) {

    ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));

    Point[] result = { null, null, null, null };

    Comparator<Point> sumComparator = new Comparator<Point>() {
      @Override
      public int compare(Point lhs, Point rhs) {
        return Double.valueOf(lhs.y + lhs.x).compareTo(rhs.y + rhs.x);
      }
    };

    Comparator<Point> diffComparator = new Comparator<Point>() {

      @Override
      public int compare(Point lhs, Point rhs) {
        return Double.valueOf(lhs.y - lhs.x).compareTo(rhs.y - rhs.x);
      }
    };

    // top-left corner = minimal sum
    result[0] = Collections.min(srcPoints, sumComparator);

    // bottom-right corner = maximal sum
    result[2] = Collections.max(srcPoints, sumComparator);

    // top-right corner = minimal diference
    result[1] = Collections.min(srcPoints, diffComparator);

    // bottom-left corner = maximal diference
    result[3] = Collections.max(srcPoints, diffComparator);

    return result;
  }

  private boolean insideArea(Point[] rp, Size size) {

    int width = Double.valueOf(size.width).intValue();
    int height = Double.valueOf(size.height).intValue();

    int minimumSize = width / 10;

    boolean isANormalShape = rp[0].x != rp[1].x && rp[1].y != rp[0].y && rp[2].y != rp[3].y && rp[3].x != rp[2].x;
    boolean isBigEnough = ((rp[1].x - rp[0].x >= minimumSize) && (rp[2].x - rp[3].x >= minimumSize)
            && (rp[3].y - rp[0].y >= minimumSize) && (rp[2].y - rp[1].y >= minimumSize));

    double leftOffset = rp[0].x - rp[3].x;
    double rightOffset = rp[1].x - rp[2].x;
    double bottomOffset = rp[0].y - rp[1].y;
    double topOffset = rp[2].y - rp[3].y;

    boolean isAnActualRectangle = ((leftOffset <= minimumSize && leftOffset >= -minimumSize)
            && (rightOffset <= minimumSize && rightOffset >= -minimumSize)
            && (bottomOffset <= minimumSize && bottomOffset >= -minimumSize)
            && (topOffset <= minimumSize && topOffset >= -minimumSize));

    return isANormalShape && isAnActualRectangle && isBigEnough;
  }
}