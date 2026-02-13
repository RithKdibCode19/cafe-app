import 'package:flutter/material.dart';
import '../models/cart_item.dart';

class CartProvider extends ChangeNotifier {
  final List<CartItem> _items = [];
  String _orderType = 'TAKEAWAY';
  String? _deliveryAddress;
  String? _deliveryPhone;
  String? _deliveryNote;
  String? _orderNote;
  int? _branchId;
  bool _useLoyaltyPoints = false;
  int _availablePoints = 0;
  double _pointValue = 0.01; // $0.01 per point default

  List<CartItem> get items => List.unmodifiable(_items);
  int get itemCount => _items.fold(0, (sum, item) => sum + item.qty);
  double get subtotal => _items.fold(0.0, (sum, item) => sum + item.totalPrice);
  String get orderType => _orderType;
  String? get deliveryAddress => _deliveryAddress;
  String? get deliveryPhone => _deliveryPhone;
  String? get deliveryNote => _deliveryNote;
  String? get orderNote => _orderNote;
  int? get branchId => _branchId;
  bool get useLoyaltyPoints => _useLoyaltyPoints;
  int get availablePoints => _availablePoints;
  double get pointValue => _pointValue;
  bool get isEmpty => _items.isEmpty;

  double get deliveryFee => _orderType == 'DELIVERY' ? 1.0 : 0.0;
  double get loyaltyDiscount => _useLoyaltyPoints ? (_availablePoints * _pointValue) : 0.0;
  double get totalAmount => (subtotal + deliveryFee - loyaltyDiscount).clamp(0.0, double.infinity);

  void setBranch(int branchId) {
    if (_branchId != branchId) {
      _branchId = branchId;
      _items.clear();
      notifyListeners();
    }
  }

  void addItem(CartItem item) {
    // Check if same item+variant+addons already in cart
    final existingIndex = _items.indexWhere((i) =>
        i.menuItemId == item.menuItemId &&
        i.selectedVariant?.variantId == item.selectedVariant?.variantId &&
        _sameAddOns(i.selectedAddOns, item.selectedAddOns));

    if (existingIndex >= 0) {
      _items[existingIndex].qty += item.qty;
    } else {
      _items.add(item);
    }
    notifyListeners();
  }

  void updateQty(int index, int qty) {
    if (index >= 0 && index < _items.length) {
      if (qty <= 0) {
        _items.removeAt(index);
      } else {
        _items[index].qty = qty;
      }
      notifyListeners();
    }
  }

  void removeItem(int index) {
    if (index >= 0 && index < _items.length) {
      _items.removeAt(index);
      notifyListeners();
    }
  }

  void setOrderType(String type) {
    _orderType = type;
    notifyListeners();
  }

  void setDeliveryInfo({
    String? address,
    String? phone,
    String? note,
  }) {
    _deliveryAddress = address;
    _deliveryPhone = phone;
    _deliveryNote = note;
    notifyListeners();
  }

  void setOrderNote(String? note) {
    _orderNote = note;
    notifyListeners();
  }
  
  void setLoyaltyInfo({int? availablePoints, double? pointValue}) {
    if (availablePoints != null) _availablePoints = availablePoints;
    if (pointValue != null) _pointValue = pointValue;
    notifyListeners();
  }

  void toggleLoyaltyPoints(bool use) {
    _useLoyaltyPoints = use;
    notifyListeners();
  }

  Map<String, dynamic> toOrderJson() {
    return {
      'branchId': _branchId,
      'orderType': _orderType,
      'note': _orderNote,
      'items': _items.map((i) => i.toOrderItemJson()).toList(),
      'pointsRedeemed': _useLoyaltyPoints ? _availablePoints : 0,
      if (_orderType == 'DELIVERY') ...{
        'deliveryAddress': _deliveryAddress,
        'deliveryPhone': _deliveryPhone,
        'deliveryNote': _deliveryNote,
      },
    };
  }

  void clear() {
    _items.clear();
    _orderNote = null;
    _deliveryAddress = null;
    _deliveryPhone = null;
    _deliveryNote = null;
    _useLoyaltyPoints = false;
    notifyListeners();
  }

  bool _sameAddOns(
      List<dynamic> addOns1, List<dynamic> addOns2) {
    if (addOns1.length != addOns2.length) return false;
    final ids1 = addOns1.map((a) => a.addOnId).toSet();
    final ids2 = addOns2.map((a) => a.addOnId).toSet();
    return ids1.containsAll(ids2) && ids2.containsAll(ids1);
  }
}
