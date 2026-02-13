import 'menu_item.dart';

class CartItem {
  final int menuItemId;
  final String name;
  final String? imageUrl;
  final Variant? selectedVariant;
  final List<AddOn> selectedAddOns;
  int qty;
  final String? note;

  CartItem({
    required this.menuItemId,
    required this.name,
    this.imageUrl,
    this.selectedVariant,
    this.selectedAddOns = const [],
    this.qty = 1,
    this.note,
  });

  double get unitPrice {
    double price = selectedVariant?.price ?? 0;
    for (final addon in selectedAddOns) {
      price += addon.price;
    }
    return price;
  }

  double get totalPrice => unitPrice * qty;

  String get variantLabel => selectedVariant?.size ?? '';

  String get addOnsLabel {
    if (selectedAddOns.isEmpty) return '';
    return selectedAddOns.map((a) => a.name).join(', ');
  }

  Map<String, dynamic> toOrderItemJson() {
    return {
      'menuItemId': menuItemId,
      'qty': qty,
      if (selectedVariant != null) 'variantId': selectedVariant!.variantId,
      if (selectedAddOns.isNotEmpty)
        'addOnIds': selectedAddOns.map((a) => a.addOnId).toList(),
    };
  }
}
