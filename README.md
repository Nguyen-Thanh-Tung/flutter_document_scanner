# document_scanner

A platform plugin for Flutter apps that get rectangle from image functionality on Android/IOS.


## How to use ?

first add as a dependency in pubspec.yaml

```
document_scanner:
    git:
          url: git://github.com/Nguyen-Thanh-Tung/flutter_document_scanner.git
          ref: develop
```

import:

```
import 'package:document_scanner/document_scanner.dart';
```

then use it as a widget:
```
Uint8List imageData = File(filePath).readAsBytesSync();
dynamic result = await RectangleDetect.getRectangle(imageData);
```

## License
This project is licensed under the MIT License - see the LICENSE.md file for details

