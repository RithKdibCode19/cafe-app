class Branch {
  final int branchId;
  final String name;
  final String code;
  final String? phone;
  final String? location;
  final double? latitude;
  final double? longitude;

  Branch({
    required this.branchId,
    required this.name,
    required this.code,
    this.phone,
    this.location,
    this.latitude,
    this.longitude,
  });

  factory Branch.fromJson(Map<String, dynamic> json) {
    return Branch(
      branchId: json['branchId'],
      name: json['name'],
      code: json['code'] ?? '',
      phone: json['phone'],
      location: json['location'],
      latitude: json['latitude']?.toDouble(),
      longitude: json['longitude']?.toDouble(),
    );
  }
}
