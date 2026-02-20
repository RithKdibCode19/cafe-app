import 'package:flutter/material.dart';
import '../../model/menu_models.dart';

class AddonDialog extends StatefulWidget {
  final MenuItem menuItem;

  const AddonDialog({super.key, required this.menuItem});

  @override
  State<AddonDialog> createState() => _AddonDialogState();
}

class _AddonDialogState extends State<AddonDialog> {
  static const Color primaryBrown = Color(0xFF6F4E37);
  static const Color darkBrown = Color(0xFF3E2723);

  int _quantity = 1;
  final List<Addon> _addons = [
    Addon(name: 'Extra Sugar', price: 0.0),
    Addon(name: 'Less Sugar', price: 0.0),
    Addon(name: 'No Sugar', price: 0.0),
    Addon(name: 'Extra Milk', price: 0.50),
    Addon(name: 'Soy Milk', price: 0.75),
    Addon(name: 'Oat Milk', price: 0.75),
    Addon(name: 'Almond Milk', price: 0.75),
    Addon(name: 'Extra Shot', price: 1.00),
    Addon(name: 'Whipped Cream', price: 0.50),
    Addon(name: 'Less Ice', price: 0.0),
    Addon(name: 'No Ice', price: 0.0),
  ];

  double get _addonTotal {
    return _addons
        .where((addon) => addon.isSelected)
        .fold(0, (sum, addon) => sum + addon.price);
  }

  double get _totalPrice {
    return (widget.menuItem.price + _addonTotal) * _quantity;
  }

  void _addToCart() {
    final selectedAddons = _addons.where((addon) => addon.isSelected).toList();

    String itemName = widget.menuItem.name;
    if (selectedAddons.isNotEmpty) {
      final addonNames = selectedAddons.map((a) => a.name).join(', ');
      itemName = '$itemName ($addonNames)';
    }

    final cartItem = CartItem(
      menuItem: MenuItem(
        id: widget.menuItem.id,
        name: itemName,
        description: widget.menuItem.description,
        price: widget.menuItem.price + _addonTotal,
        imageUrl: widget.menuItem.imageUrl,
        category: widget.menuItem.category,
      ),
      quantity: _quantity,
      addons: selectedAddons.isNotEmpty ? List.from(selectedAddons) : null,
    );

    Navigator.of(context).pop(cartItem);
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Container(
        constraints: const BoxConstraints(maxHeight: 600),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: primaryBrown,
                borderRadius: const BorderRadius.vertical(
                  top: Radius.circular(16),
                ),
              ),
              child: Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          widget.menuItem.name,
                          style: const TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                            color: Colors.white,
                          ),
                        ),
                        Text(
                          '\$${widget.menuItem.price.toStringAsFixed(2)}',
                          style: const TextStyle(
                            fontSize: 16,
                            color: Colors.white70,
                          ),
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    onPressed: () => Navigator.of(context).pop(),
                    icon: const Icon(Icons.close, color: Colors.white),
                  ),
                ],
              ),
            ),

            // Content
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Quantity
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'Quantity',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: darkBrown,
                          ),
                        ),
                        Row(
                          children: [
                            IconButton(
                              onPressed: _quantity > 1
                                  ? () => setState(() => _quantity--)
                                  : null,
                              icon: const Icon(Icons.remove_circle_outline),
                              color: primaryBrown,
                            ),
                            Text(
                              '$_quantity',
                              style: const TextStyle(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            IconButton(
                              onPressed: () => setState(() => _quantity++),
                              icon: const Icon(Icons.add_circle_outline),
                              color: primaryBrown,
                            ),
                          ],
                        ),
                      ],
                    ),

                    const Divider(height: 24),

                    // Addons
                    const Text(
                      'Customize your order',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                        color: darkBrown,
                      ),
                    ),
                    const SizedBox(height: 12),

                    ...(_addons.map((addon) {
                      return CheckboxListTile(
                        title: Text(addon.name),
                        subtitle: addon.price > 0
                            ? Text(
                                '+\$${addon.price.toStringAsFixed(2)}',
                                style: TextStyle(
                                  color: Colors.grey[600],
                                  fontSize: 12,
                                ),
                              )
                            : null,
                        value: addon.isSelected,
                        activeColor: primaryBrown,
                        contentPadding: EdgeInsets.zero,
                        onChanged: (value) {
                          setState(() {
                            addon.isSelected = value ?? false;
                          });
                        },
                      );
                    }).toList()),

                    const SizedBox(height: 16),

                    // Total Summary
                    if (_addonTotal > 0)
                      Container(
                        padding: const EdgeInsets.all(12),
                        decoration: BoxDecoration(
                          color: Colors.grey[100],
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            const Text(
                              'Addons Total',
                              style: TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                            Text(
                              '+\$${_addonTotal.toStringAsFixed(2)}',
                              style: const TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.bold,
                                color: primaryBrown,
                              ),
                            ),
                          ],
                        ),
                      ),
                  ],
                ),
              ),
            ),

            // Footer
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.white,
                border: Border(top: BorderSide(color: Colors.grey[300]!)),
              ),
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const Text(
                        'Total',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        '\$${_totalPrice.toStringAsFixed(2)}',
                        style: const TextStyle(
                          fontSize: 24,
                          fontWeight: FontWeight.bold,
                          color: primaryBrown,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    height: 50,
                    child: ElevatedButton(
                      onPressed: _addToCart,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: primaryBrown,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const Icon(Icons.shopping_cart, color: Colors.white),
                          const SizedBox(width: 8),
                          Text(
                            'Add to Cart${_quantity > 1 ? ' ($_quantity)' : ''}',
                            style: const TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
