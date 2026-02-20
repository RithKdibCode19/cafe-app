class MenuItem {
  final int menuItemId;
  final String name;
  final double basePrice;
  final String? imageUrl;
  final bool isAvailable;
  final int? categoryId;
  final String? categoryName;
  final List<Variant> variants;

  MenuItem({
    required this.menuItemId,
    required this.name,
    required this.basePrice,
    this.imageUrl,
    this.isAvailable = true,
    this.categoryId,
    this.categoryName,
    this.variants = const [],
  });

  factory MenuItem.fromJson(Map<String, dynamic> json) {
    return MenuItem(
      menuItemId: json['menuItemId'],
      name: json['name'],
      basePrice: (json['basePrice'] ?? 0).toDouble(),
      imageUrl: json['imageUrl'],
      isAvailable: json['isAvailable'] ?? true,
      categoryId: json['categoryId'],
      categoryName: json['categoryName'],
      variants: (json['variants'] as List<dynamic>?)
              ?.map((v) => Variant.fromJson(v))
              .toList() ??
          [],
    );
  }

  double get displayPrice {
    if (variants.isNotEmpty) {
      return variants.first.price;
    }
    return basePrice;
  }
}

class Variant {
  final int variantId;
  final String size;
  final double price;

  Variant({
    required this.variantId,
    required this.size,
    required this.price,
  });

  factory Variant.fromJson(Map<String, dynamic> json) {
    return Variant(
      variantId: json['variantId'],
      size: json['size'],
      price: (json['price'] ?? 0).toDouble(),
    );
  }
}

class AddOn {
  final int addOnId;
  final String name;
  final double price;

  AddOn({
    required this.addOnId,
    required this.name,
    required this.price,
  });

  factory AddOn.fromJson(Map<String, dynamic> json) {
    return AddOn(
      addOnId: json['addOnId'],
      name: json['name'],
      price: (json['price'] ?? 0).toDouble(),
    );
  }
}

class MenuItemDetail {
  final int menuItemId;
  final String name;
  final double basePrice;
  final String? imageUrl;
  final String? description;
  final bool isAvailable;
  final String? categoryName;
  final List<Variant> variants;
  final List<AddOn> addOns;

  MenuItemDetail({
    required this.menuItemId,
    required this.name,
    required this.basePrice,
    this.imageUrl,
    this.description,
    this.isAvailable = true,
    this.categoryName,
    this.variants = const [],
    this.addOns = const [],
  });

  factory MenuItemDetail.fromJson(Map<String, dynamic> json) {
    return MenuItemDetail(
      menuItemId: json['menuItemId'],
      name: json['name'],
      basePrice: (json['basePrice'] ?? 0).toDouble(),
      imageUrl: json['imageUrl'],
      description: json['description'],
      isAvailable: json['isAvailable'] ?? true,
      categoryName: json['categoryName'],
      variants: (json['variants'] as List<dynamic>?)
              ?.map((v) => Variant.fromJson(v))
              .toList() ??
          [],
      addOns: (json['addOns'] as List<dynamic>?)
              ?.map((a) => AddOn.fromJson(a))
              .toList() ??
          [],
    );
  }
}

class CategoryWithItems {
  final int categoryId;
  final String name;
  final List<MenuItem> items;

  CategoryWithItems({
    required this.categoryId,
    required this.name,
    required this.items,
  });

  factory CategoryWithItems.fromJson(Map<String, dynamic> json) {
    return CategoryWithItems(
      categoryId: json['categoryId'],
      name: json['name'],
      items: (json['items'] as List<dynamic>?)
              ?.map((i) => MenuItem.fromJson(i))
              .toList() ??
          [],
    );
  }
}
