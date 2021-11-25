import 'dart:async';
import 'dart:io' show Platform;

import 'package:document_scanner/rectangle_coordinates.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const String _methodChannelIdentifier = 'document_scanner';

class RectangleDetect extends StatefulWidget {
  /// onDocumentScanned gets called when the scanner successfully scans a rectangle (document)
  final Function(RectangleCoordinates) onRectangleDetected;

  RectangleDetect({
    required this.onRectangleDetected,
  });

  final MethodChannel _channel = const MethodChannel(_methodChannelIdentifier);

  @override
  _DocState createState() => _DocState();
}

class _DocState extends State<RectangleDetect> {
  @override
  void initState() {
    print("initializing document scanner state");
    widget._channel.setMethodCallHandler(_onDocumentScanned);
    super.initState();
  }

  Future<dynamic> _onDocumentScanned(MethodCall call) async {
    if (call.method == "onPictureTaken") {
      Map<String, dynamic> argsAsMap = Map<String, dynamic>.from(call.arguments);
      Map<String, dynamic> rectangleMap = Map<String, dynamic>.from(argsAsMap["rectangleCoordinates"]);
      RectangleCoordinates rectangleCoordinates = RectangleCoordinates.fromMap(rectangleMap);
      widget.onRectangleDetected(rectangleCoordinates);
    }

    return;
  }

  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return AndroidView(
        viewType: _methodChannelIdentifier,
        creationParamsCodec: const StandardMessageCodec(),
        creationParams: _getParams(),
      );
    } else if (Platform.isIOS) {
      print("platform ios");
      return UiKitView(
        viewType: _methodChannelIdentifier,
        creationParams: _getParams(),
        creationParamsCodec: const StandardMessageCodec(),
      );
    } else {
      throw ("Current Platform is not supported");
    }
  }

  Map<String, dynamic> _getParams() {
    Map<String, dynamic> allParams = {
      "noGrayScale": true,
    };

    Map<String, dynamic> nonNullParams = {};
    allParams.forEach((key, value) {
      if (value != null) {
        nonNullParams.addAll({key: value});
      }
    });

    return nonNullParams;
  }
}
