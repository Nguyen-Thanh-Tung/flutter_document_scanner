# flutter_document_scanner

A plugin for Flutter apps that adds document scanning functionality on Android.

## Warning
 Working only on ios for now
#

## Setup

### IOS

Add a boolean property to the app's Info.plist file with the key io.flutter.embedded_views_preview and the value true to enable embedded views preview.

    <key>io.flutter.embedded_views_preview</key>
    <true/>

Add a String property to the app's Info.plist file with the key NSCameraUsageDescription and the value as the description for why your app needs camera access.

	<key>NSCameraUsageDescription</key>
	<string>Camera Permission Description</string>

### Android
TBD

## How to use ?

first add as a dependency in pubspec.yaml

import:

```
import 'package:document_scanner/document_scanner.dart';
```

then use it as a widget:
```
DocumentScanner(
    onPictureTaken: (String image) {
        print("document : " + image);
    },
                          
)
```

## License
This project is licensed under the MIT License - see the LICENSE.md file for details

