import 'dart:async';
import 'dart:typed_data';
import 'package:document_scanner/rectangle_coordinates.dart';
import 'package:flutter/services.dart';

const String _methodChannelIdentifier = 'document_scanner';

class RectangleDetect {

  RectangleDetect._();

  static final RectangleDetect instance = RectangleDetect._();
  final MethodChannel _channel = const MethodChannel(_methodChannelIdentifier);

  Future<dynamic> getRectangle(Uint8List data) async {
    dynamic responseData =  await _channel.invokeMethod('getRectangle', <String, dynamic>{
      'imageData': data,
    });

    Map<String, dynamic> argsAsMap = Map<String, dynamic>.from(responseData);
    RectangleCoordinates rectangleCoordinates = RectangleCoordinates.fromMap(argsAsMap);
    return rectangleCoordinates;
  }
}
