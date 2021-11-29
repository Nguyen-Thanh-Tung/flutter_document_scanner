import 'package:document_scanner/rectangle_detect.dart';
export 'package:document_scanner/rectangle_detect.dart';
export 'package:document_scanner/rectangle_coordinates.dart';

class DocumentScanner {
  DocumentScanner._();
  static final RectangleDetect rectangleDetect = RectangleDetect.instance;
}