class Customer {
  final int customerId;
  final String? name;
  final String? phone;
  final String? email;
  final int loyaltyPoints;
  final double loyaltyRedeemRate;
  final String? membershipLevel;

  Customer({
    required this.customerId,
    this.name,
    this.phone,
    this.email,
    this.loyaltyPoints = 0,
    this.loyaltyRedeemRate = 0.01,
    this.membershipLevel,
  });

  factory Customer.fromJson(Map<String, dynamic> json) {
    return Customer(
      customerId: json['customerId'],
      name: json['name'],
      phone: json['phone'],
      email: json['email'],
      loyaltyPoints: json['loyaltyPoints'] ?? 0,
      loyaltyRedeemRate: (json['loyaltyRedeemRate'] != null)
          ? (json['loyaltyRedeemRate'] as num).toDouble()
          : 0.01,
      membershipLevel: json['membershipLevel'],
    );
  }
}
