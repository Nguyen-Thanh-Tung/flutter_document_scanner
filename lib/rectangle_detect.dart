import 'dart:async';
import 'dart:typed_data';
import 'package:document_scanner/rectangle_coordinates.dart';
import 'package:flutter/services.dart';

class RectangleDetect {
  static Future<dynamic> getRectangle(Uint8List data) async {
    const MethodChannel _channel = const MethodChannel('document_scanner');
    dynamic responseData =  await _channel.invokeMethod('getRectangle', <String, dynamic>{
      'imageData': data,
    });

    try {
      Map<String, dynamic> argsAsMap = Map<String, dynamic>.from(responseData);
      RectangleCoordinates rectangleCoordinates = RectangleCoordinates.fromMap(argsAsMap);
      return rectangleCoordinates;
    } catch(e) {
      print('Error: ' + e.toString());
      return RectangleCoordinates(bottomLeft: Corner(x: 0, y: 1), bottomRight: Corner(x: 1, y: 1), topLeft: Corner(x: 0, y: 0), topRight: Corner(x: 1, y: 0));
    }
  }
}
