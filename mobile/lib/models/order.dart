class Order {
  final int orderId;
  final String orderNo;
  final String orderType;
  final String status;
  final String? note;
  final double subTotal;
  final double discountAmount;
  final double taxAmount;
  final double totalAmount;
  final double? deliveryFee;
  final String? deliveryAddress;
  final String? deliveryPhone;
  final int pointsRedeemed;
  final String? branchName;
  final List<OrderItem> items;
  final String? createdAt;

  Order({
    required this.orderId,
    required this.orderNo,
    required this.orderType,
    required this.status,
    this.note,
    required this.subTotal,
    required this.discountAmount,
    required this.taxAmount,
    required this.totalAmount,
    this.deliveryFee,
    this.deliveryAddress,
    this.deliveryPhone,
    this.pointsRedeemed = 0,
    this.branchName,
    this.items = const [],
    this.createdAt,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    return Order(
      orderId: json['orderId'],
      orderNo: json['orderNo'] ?? '',
      orderType: json['orderType'] ?? '',
      status: json['status'] ?? '',
      note: json['note'],
      subTotal: (json['subTotal'] ?? 0).toDouble(),
      discountAmount: (json['discountAmount'] ?? 0).toDouble(),
      taxAmount: (json['taxAmount'] ?? 0).toDouble(),
      totalAmount: (json['totalAmount'] ?? 0).toDouble(),
      deliveryFee: json['deliveryFee']?.toDouble(),
      deliveryAddress: json['deliveryAddress'],
      deliveryPhone: json['deliveryPhone'],
      pointsRedeemed: json['pointsRedeemed'] ?? 0,
      branchName: json['branchName'],
      items: (json['items'] as List<dynamic>?)
              ?.map((i) => OrderItem.fromJson(i))
              .toList() ??
          [],
      createdAt: json['createdAt'],
    );
  }
}

class OrderItem {
  final int orderItemId;
  final String menuItemName;
  final String? variantSize;
  final int qty;
  final double unitPrice;
  final List<String> addonNames;

  OrderItem({
    required this.orderItemId,
    required this.menuItemName,
    this.variantSize,
    required this.qty,
    required this.unitPrice,
    this.addonNames = const [],
  });

  factory OrderItem.fromJson(Map<String, dynamic> json) {
    return OrderItem(
      orderItemId: json['orderItemId'],
      menuItemName: json['menuItemName'] ?? '',
      variantSize: json['variantSize'],
      qty: json['qty'] ?? 1,
      unitPrice: (json['unitPrice'] ?? 0).toDouble(),
      addonNames: (json['addonNames'] as List<dynamic>?)
              ?.map((n) => n.toString())
              .toList() ??
          [],
    );
  }
}

class OrderSummary {
  final int orderId;
  final String orderNo;
  final String orderType;
  final String status;
  final double totalAmount;
  final int itemCount;
  final String? branchName;
  final String? createdAt;

  OrderSummary({
    required this.orderId,
    required this.orderNo,
    required this.orderType,
    required this.status,
    required this.totalAmount,
    required this.itemCount,
    this.branchName,
    this.createdAt,
  });

  factory OrderSummary.fromJson(Map<String, dynamic> json) {
    return OrderSummary(
      orderId: json['orderId'],
      orderNo: json['orderNo'] ?? '',
      orderType: json['orderType'] ?? '',
      status: json['status'] ?? '',
      totalAmount: (json['totalAmount'] ?? 0).toDouble(),
      itemCount: json['itemCount'] ?? 0,
      branchName: json['branchName'],
      createdAt: json['createdAt'],
    );
  }
}
