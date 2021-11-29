#import "DocumentScannerPlugin.h"

@implementation DocumentScannerPlugin {
    NSObject<FlutterPluginRegistrar>* _registrar;
    FlutterMethodChannel* _channel;
    NSMutableDictionary* _mapControllers;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"document_scanner"
            binaryMessenger:[registrar messenger]];
    DocumentScannerPlugin* instance = [[DocumentScannerPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
   if ([@"getRectangle" isEqualToString:call.method]) {
       FlutterStandardTypedData* data = call.arguments[@"imageData"];
       CIImage *enhancedImage = [CIImage imageWithData:data.data];
       CIRectangleFeature *rectangleFeature = [self biggestRectangleInRectangles:[[self highAccuracyRectangleDetector] featuresInImage:enhancedImage]];

       if (rectangleFeature) {
           double width = enhancedImage.extent.size.width;
           double height = enhancedImage.extent.size.height;
           id rectangleCoordinates = rectangleFeature ? @{
                                                @"topRight": @{ @"x": @((rectangleFeature.topRight.x + 30)/width), @"y": @(1 - rectangleFeature.topRight.y/height)},
                                                @"topLeft": @{ @"x": @((rectangleFeature.topLeft.x + 30)/width), @"y": @(1 - rectangleFeature.topLeft.y/height)},
                                                @"bottomRight": @{ @"x": @(rectangleFeature.bottomRight.x/width), @"y": @(1 - rectangleFeature.bottomRight.y/height)},
                                                @"bottomLeft": @{ @"x": @(rectangleFeature.bottomLeft.x/width), @"y": @(1 - rectangleFeature.bottomLeft.y/height)},
                                                } : [NSNull null];
           result(rectangleCoordinates);
       } else {
           result([NSNull null]);
       }
   } else if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
   } else {
        result(FlutterMethodNotImplemented);
   }
}

- (CIDetector *)highAccuracyRectangleDetector {
    static CIDetector *detector = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^
    {
        detector = [CIDetector detectorOfType:CIDetectorTypeRectangle context:nil options:@{CIDetectorAccuracy : CIDetectorAccuracyHigh, CIDetectorReturnSubFeatures: @(YES) }];
    });
    return detector;
}

- (CIRectangleFeature *)biggestRectangleInRectangles:(NSArray *)rectangles {
    if (![rectangles count]) return nil;

    float halfPerimiterValue = 0;

    CIRectangleFeature *biggestRectangle = [rectangles firstObject];

    for (CIRectangleFeature *rect in rectangles)
    {
        CGPoint p1 = rect.topLeft;
        CGPoint p2 = rect.topRight;
        CGFloat width = hypotf(p1.x - p2.x, p1.y - p2.y);

        CGPoint p3 = rect.topLeft;
        CGPoint p4 = rect.bottomLeft;
        CGFloat height = hypotf(p3.x - p4.x, p3.y - p4.y);

        CGFloat currentHalfPerimiterValue = height + width;

        if (halfPerimiterValue < currentHalfPerimiterValue)
        {
            halfPerimiterValue = currentHalfPerimiterValue;
            biggestRectangle = rect;
        }
    }

    return biggestRectangle;
}
@end
