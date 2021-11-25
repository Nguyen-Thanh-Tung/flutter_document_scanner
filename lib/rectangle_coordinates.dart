import 'dart:convert';

class RectangleCoordinates {
  RectangleCoordinates({
    required this.bottomLeft,
    required this.bottomRight,
    required this.topLeft,
    required this.topRight,
  });

  Corner? bottomLeft;
  Corner? bottomRight;
  Corner? topLeft;
  Corner? topRight;

  factory RectangleCoordinates.fromJson(String str) =>
      RectangleCoordinates.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory RectangleCoordinates.fromMap(Map<String, dynamic> json) =>
      RectangleCoordinates(
        bottomLeft: json["bottomLeft"] == null
            ? null
            : Corner.fromMap(Map<String, dynamic>.from(json["bottomLeft"])),
        bottomRight: json["bottomRight"] == null
            ? null
            : Corner.fromMap(Map<String, dynamic>.from(json["bottomRight"])),
        topLeft:
            json["topLeft"] == null ? null : Corner.fromMap(Map<String, dynamic>.from(json["topLeft"])),
        topRight:
            json["topRight"] == null ? null : Corner.fromMap(Map<String, dynamic>.from(json["topRight"])),
      );

  Map<String, dynamic> toMap() => {
      "bottomLeft": bottomLeft == null ? null : bottomLeft!.toMap(),
      "bottomRight": bottomRight == null ? null : bottomRight!.toMap(),
      "topLeft": topLeft == null ? null : topLeft!.toMap(),
      "topRight": topRight == null ? null : topRight!.toMap(),
  };
}

class Corner {
  Corner({
    required this.x,
    required this.y,
  });

  double x;
  double y;

  factory Corner.fromJson(String str) => Corner.fromMap(json.decode(str));

  String toJson() => json.encode(toMap());

  factory Corner.fromMap(Map<String, dynamic> json) => Corner(
        x: json["x"] == null ? null : json["x"],
        y: json["y"] == null ? null : json["y"],
      );

  Map<String, dynamic> toMap() => {
      "x": x == null ? null : x,
      "y": y == null ? null : y,
  };
}
