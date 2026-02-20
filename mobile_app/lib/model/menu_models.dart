class MenuItem {
  final int id;
  final String name;
  final String description;
  final double price;
  final String imageUrl;
  final String category;
  final bool isAvailable;

  MenuItem({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.imageUrl,
    required this.category,
    this.isAvailable = true,
  });

  factory MenuItem.fromJson(Map<String, dynamic> json) {
    return MenuItem(
      id: json['menuItemId'] ?? 0,
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      price: (json['basePrice'] ?? 0.0).toDouble(),
      imageUrl: json['imageUrl'] ?? '',
      category: json['categoryName'] ?? '',
      isAvailable: json['isAvailable'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'menuItemId': id,
      'name': name,
      'description': description,
      'basePrice': price,
      'imageUrl': imageUrl,
      'categoryName': category,
      'isAvailable': isAvailable,
    };
  }
}

class Addon {
  final String name;
  final double price;
  bool isSelected;

  Addon({required this.name, required this.price, this.isSelected = false});
}

class CartItem {
  final MenuItem menuItem;
  int quantity;
  String? size;
  List<Addon>? addons;

  // The menuItem.price already includes all customizations (size + addons)
  // addons list is kept for display purposes only
  double get itemPrice => menuItem.price;

  double get subtotal => itemPrice * quantity;

  CartItem({required this.menuItem, this.quantity = 1, this.size, this.addons});
}

class Order {
  final String orderId;
  final List<CartItem> items;
  final double totalAmount;
  final String paymentMethod;
  final DateTime orderDate;
  final String status;
  final String? deliveryAddress;
  final String? deliveryPhone;
  final String? deliveryName;
  final String? notes;
  final double? deliveryLatitude;
  final double? deliveryLongitude;

  Order({
    required this.orderId,
    required this.items,
    required this.totalAmount,
    required this.paymentMethod,
    required this.orderDate,
    this.status = 'Pending',
    this.deliveryAddress,
    this.deliveryPhone,
    this.deliveryName,
    this.notes,
    this.deliveryLatitude,
    this.deliveryLongitude,
  });

  int get totalItems => items.fold(0, (total, item) => total + item.quantity);

  bool get hasMapLocation =>
      deliveryLatitude != null && deliveryLongitude != null;
}

class Category {
  final int id;
  final String name;
  final String description;
  final int itemCount;

  Category({
    required this.id,
    required this.name,
    required this.description,
    this.itemCount = 0,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      id: json['categoryId'] ?? 0,
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      itemCount: json['itemCount'] ?? 0,
    );
  }
}
