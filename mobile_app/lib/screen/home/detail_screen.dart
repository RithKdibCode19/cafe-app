import 'package:flutter/material.dart';
import '../../model/menu_models.dart';

class DetailScreen extends StatefulWidget {
  final MenuItem menuItem;

  const DetailScreen({super.key, required this.menuItem});

  @override
  State<DetailScreen> createState() => _DetailScreenState();
}

class _DetailScreenState extends State<DetailScreen> {
  String _selectedSize = 'M';
  int _quantity = 1;

  // Addon selections
  String _sugarLevel = 'Normal';
  String _iceLevel = 'Normal';
  String _milkType = 'Regular';
  bool _extraShot = false;
  bool _whippedCream = false;

  // Define theme colors for the cafe app
  static const Color primaryBrown = Color(0xFF6F4E37);
  static const Color lightBrown = Color(0xFF8B5A3C);
  static const Color creamColor = Color(0xFFF5F5DC);
  static const Color darkBrown = Color(0xFF3E2723);

  final List<String> _sizes = ['S', 'M', 'L'];
  final Map<String, double> _sizePriceMultiplier = {
    'S': 0.8, // 20% discount for small
    'M': 1.0, // Base price for medium
    'L': 1.3, // 30% premium for large
  };

  final List<String> _sugarLevels = [
    'No Sugar',
    'Less Sugar',
    'Normal',
    'Extra Sugar',
  ];
  final List<String> _iceLevels = ['No Ice', 'Less Ice', 'Normal', 'Extra Ice'];
  final Map<String, double> _milkTypes = {
    'Regular': 0.0,
    'Soy Milk': 0.75,
    'Oat Milk': 0.75,
    'Almond Milk': 0.75,
  };

  double get _currentPrice {
    double price =
        widget.menuItem.price * (_sizePriceMultiplier[_selectedSize] ?? 1.0);
    price += _milkTypes[_milkType] ?? 0.0;
    if (_extraShot) price += 1.00;
    if (_whippedCream) price += 0.50;
    return price;
  }

  double get _totalPrice {
    return _currentPrice * _quantity;
  }

  void _addToCart() {
    // Build addon list
    List<Addon> addons = [];
    if (_sugarLevel != 'Normal') {
      addons.add(Addon(name: _sugarLevel, price: 0.0, isSelected: true));
    }
    if (_iceLevel != 'Normal') {
      addons.add(Addon(name: _iceLevel, price: 0.0, isSelected: true));
    }
    if (_milkType != 'Regular') {
      addons.add(
        Addon(name: _milkType, price: _milkTypes[_milkType]!, isSelected: true),
      );
    }
    if (_extraShot) {
      addons.add(Addon(name: 'Extra Shot', price: 1.00, isSelected: true));
    }
    if (_whippedCream) {
      addons.add(Addon(name: 'Whipped Cream', price: 0.50, isSelected: true));
    }

    // Create a modified menu item with size and customizations
    String itemName = '${widget.menuItem.name} ($_selectedSize)';
    if (addons.isNotEmpty) {
      final addonNames = addons.map((a) => a.name).join(', ');
      itemName = '${widget.menuItem.name} ($_selectedSize) - $addonNames';
    }

    final cartItem = CartItem(
      menuItem: MenuItem(
        id: widget.menuItem.id,
        name: itemName,
        description: widget.menuItem.description,
        price: _currentPrice,
        imageUrl: widget.menuItem.imageUrl,
        category: widget.menuItem.category,
      ),
      quantity: _quantity,
      size: _selectedSize,
      addons: addons.isNotEmpty ? addons : null,
    );

    // Show success message
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${cartItem.menuItem.name} x$_quantity added to cart!'),
        duration: const Duration(seconds: 2),
        backgroundColor: primaryBrown,
      ),
    );

    // Navigate back
    Navigator.of(context).pop(cartItem);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: creamColor,
      body: CustomScrollView(
        slivers: [
          // App Bar with Image
          SliverAppBar(
            expandedHeight: 300,
            pinned: true,
            backgroundColor: primaryBrown,
            flexibleSpace: FlexibleSpaceBar(
              background: Hero(
                tag: 'menu-item-${widget.menuItem.id}',
                child: Container(
                  decoration: const BoxDecoration(color: Colors.grey),
                  child: widget.menuItem.imageUrl.isNotEmpty
                      ? Image.network(
                          widget.menuItem.imageUrl,
                          fit: BoxFit.cover,
                          errorBuilder: (context, error, stackTrace) {
                            return Container(
                              color: Colors.grey[300],
                              child: const Icon(
                                Icons.local_cafe,
                                size: 80,
                                color: Colors.grey,
                              ),
                            );
                          },
                        )
                      : Container(
                          color: Colors.grey[300],
                          child: const Icon(
                            Icons.local_cafe,
                            size: 80,
                            color: Colors.grey,
                          ),
                        ),
                ),
              ),
            ),
            leading: IconButton(
              icon: const Icon(Icons.arrow_back, color: Colors.white),
              onPressed: () => Navigator.of(context).pop(),
            ),
          ),

          // Content
          SliverToBoxAdapter(
            child: Container(
              decoration: const BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
              ),
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Item Name and Category
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            widget.menuItem.name,
                            style: const TextStyle(
                              fontSize: 28,
                              fontWeight: FontWeight.bold,
                              color: darkBrown,
                            ),
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 12,
                            vertical: 6,
                          ),
                          decoration: BoxDecoration(
                            color: primaryBrown.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: Text(
                            widget.menuItem.category,
                            style: const TextStyle(
                              color: primaryBrown,
                              fontWeight: FontWeight.w600,
                              fontSize: 12,
                            ),
                          ),
                        ),
                      ],
                    ),

                    const SizedBox(height: 10),

                    // Description
                    Text(
                      widget.menuItem.description,
                      style: TextStyle(
                        fontSize: 16,
                        color: Colors.grey[600],
                        height: 1.5,
                      ),
                    ),

                    const SizedBox(height: 20),

                    // Size Selection
                    const Text(
                      'Size',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: darkBrown,
                      ),
                    ),

                    const SizedBox(height: 10),

                    Row(
                      children: _sizes.asMap().entries.map((entry) {
                        final size = entry.value;
                        final isSelected = _selectedSize == size;
                        final sizePrice =
                            widget.menuItem.price *
                            (_sizePriceMultiplier[size] ?? 1.0);

                        return Expanded(
                          child: Container(
                            margin: EdgeInsets.only(
                              right: entry.key < _sizes.length - 1 ? 8 : 0,
                            ),
                            height: 70,
                            child: GestureDetector(
                              onTap: () {
                                setState(() {
                                  _selectedSize = size;
                                });
                              },
                              child: Container(
                                padding: const EdgeInsets.symmetric(
                                  vertical: 8,
                                  horizontal: 4,
                                ),
                                decoration: BoxDecoration(
                                  color: isSelected
                                      ? primaryBrown
                                      : Colors.grey[100],
                                  borderRadius: BorderRadius.circular(12),
                                  border: Border.all(
                                    color: isSelected
                                        ? primaryBrown
                                        : Colors.grey[300]!,
                                    width: 2,
                                  ),
                                ),
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Text(
                                      size,
                                      style: TextStyle(
                                        fontSize: 16,
                                        fontWeight: FontWeight.bold,
                                        color: isSelected
                                            ? Colors.white
                                            : darkBrown,
                                      ),
                                    ),
                                    const SizedBox(height: 2),
                                    FittedBox(
                                      fit: BoxFit.scaleDown,
                                      child: Text(
                                        '\$${sizePrice.toStringAsFixed(2)}',
                                        style: TextStyle(
                                          fontSize: 12,
                                          color: isSelected
                                              ? Colors.white.withOpacity(0.9)
                                              : Colors.grey[600],
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        );
                      }).toList(),
                    ),

                    const SizedBox(height: 32),

                    // Sugar Level
                    const Text(
                      'Sugar Level',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: darkBrown,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _sugarLevels.map((level) {
                        final isSelected = _sugarLevel == level;
                        return ChoiceChip(
                          label: Text(level),
                          selected: isSelected,
                          onSelected: (selected) {
                            setState(() {
                              _sugarLevel = level;
                            });
                          },
                          backgroundColor: Colors.grey[100],
                          selectedColor: primaryBrown,
                          labelStyle: TextStyle(
                            color: isSelected ? Colors.white : darkBrown,
                            fontWeight: isSelected
                                ? FontWeight.bold
                                : FontWeight.normal,
                          ),
                        );
                      }).toList(),
                    ),

                    const SizedBox(height: 24),

                    // Ice Level
                    const Text(
                      'Ice Level',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: darkBrown,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _iceLevels.map((level) {
                        final isSelected = _iceLevel == level;
                        return ChoiceChip(
                          label: Text(level),
                          selected: isSelected,
                          onSelected: (selected) {
                            setState(() {
                              _iceLevel = level;
                            });
                          },
                          backgroundColor: Colors.grey[100],
                          selectedColor: primaryBrown,
                          labelStyle: TextStyle(
                            color: isSelected ? Colors.white : darkBrown,
                            fontWeight: isSelected
                                ? FontWeight.bold
                                : FontWeight.normal,
                          ),
                        );
                      }).toList(),
                    ),

                    const SizedBox(height: 24),

                    // Milk Type
                    const Text(
                      'Milk Type',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: darkBrown,
                      ),
                    ),
                    const SizedBox(height: 10),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _milkTypes.entries.map((entry) {
                        final isSelected = _milkType == entry.key;
                        return ChoiceChip(
                          label: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Text(entry.key),
                              if (entry.value > 0) ...[
                                const SizedBox(width: 4),
                                Text(
                                  '+\$${entry.value.toStringAsFixed(2)}',
                                  style: TextStyle(
                                    fontSize: 10,
                                    color: isSelected
                                        ? Colors.white70
                                        : Colors.grey[600],
                                  ),
                                ),
                              ],
                            ],
                          ),
                          selected: isSelected,
                          onSelected: (selected) {
                            setState(() {
                              _milkType = entry.key;
                            });
                          },
                          backgroundColor: Colors.grey[100],
                          selectedColor: primaryBrown,
                          labelStyle: TextStyle(
                            color: isSelected ? Colors.white : darkBrown,
                            fontWeight: isSelected
                                ? FontWeight.bold
                                : FontWeight.normal,
                          ),
                        );
                      }).toList(),
                    ),

                    const SizedBox(height: 24),

                    // Extra Options
                    const Text(
                      'Extra Options',
                      style: TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: darkBrown,
                      ),
                    ),
                    const SizedBox(height: 10),
                    CheckboxListTile(
                      title: const Text('Extra Shot'),
                      subtitle: const Text('+\$1.00'),
                      value: _extraShot,
                      activeColor: primaryBrown,
                      onChanged: (value) {
                        setState(() {
                          _extraShot = value ?? false;
                        });
                      },
                      contentPadding: EdgeInsets.zero,
                    ),
                    CheckboxListTile(
                      title: const Text('Whipped Cream'),
                      subtitle: const Text('+\$0.50'),
                      value: _whippedCream,
                      activeColor: primaryBrown,
                      onChanged: (value) {
                        setState(() {
                          _whippedCream = value ?? false;
                        });
                      },
                      contentPadding: EdgeInsets.zero,
                    ),

                    const SizedBox(height: 24),

                    // Quantity Selection
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'Quantity',
                          style: TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                            color: darkBrown,
                          ),
                        ),
                        Row(
                          children: [
                            Container(
                              decoration: BoxDecoration(
                                color: Colors.grey[100],
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: IconButton(
                                onPressed: _quantity > 1
                                    ? () => setState(() => _quantity--)
                                    : null,
                                icon: const Icon(Icons.remove),
                                color: primaryBrown,
                              ),
                            ),
                            Padding(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 20,
                              ),
                              child: Text(
                                '$_quantity',
                                style: const TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                  color: darkBrown,
                                ),
                              ),
                            ),
                            Container(
                              decoration: BoxDecoration(
                                color: Colors.grey[100],
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: IconButton(
                                onPressed: () => setState(() => _quantity++),
                                icon: const Icon(Icons.add),
                                color: primaryBrown,
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),

                    const SizedBox(height: 32),

                    // Additional Information
                    Container(
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Colors.orange.shade50,
                        borderRadius: BorderRadius.circular(12),
                        border: Border.all(color: Colors.orange.shade200),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Icon(
                                Icons.info_outline,
                                color: Colors.orange.shade700,
                                size: 20,
                              ),
                              const SizedBox(width: 8),
                              Text(
                                'Item Details',
                                style: TextStyle(
                                  color: Colors.orange.shade700,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: 8),
                          Text(
                            '• Freshly prepared\n'
                            '• Made with premium ingredients\n'
                            '• Available for dine-in and takeaway\n'
                            '• Customization available upon request',
                            style: TextStyle(
                              color: Colors.orange.shade700,
                              fontSize: 14,
                            ),
                          ),
                        ],
                      ),
                    ),

                    const SizedBox(height: 80), // Space for bottom button
                  ],
                ),
              ),
            ),
          ),
        ],
      ),

      // Bottom Add to Cart Button
      bottomSheet: Container(
        padding: const EdgeInsets.all(24),
        decoration: const BoxDecoration(
          color: Colors.white,
          boxShadow: [
            BoxShadow(
              color: Colors.black12,
              blurRadius: 8,
              offset: Offset(0, -4),
            ),
          ],
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Customization Summary
            if (_sugarLevel != 'Normal' ||
                _iceLevel != 'Normal' ||
                _milkType != 'Regular' ||
                _extraShot ||
                _whippedCream)
              Container(
                padding: const EdgeInsets.all(12),
                margin: const EdgeInsets.only(bottom: 12),
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(Icons.tune, size: 16, color: Colors.grey[700]),
                        const SizedBox(width: 4),
                        Text(
                          'Customizations:',
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                            color: Colors.grey[700],
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 6),
                    Wrap(
                      spacing: 6,
                      runSpacing: 4,
                      children: [
                        if (_sugarLevel != 'Normal')
                          _buildCustomizationChip(_sugarLevel),
                        if (_iceLevel != 'Normal')
                          _buildCustomizationChip(_iceLevel),
                        if (_milkType != 'Regular')
                          _buildCustomizationChip(
                            '$_milkType +\$${_milkTypes[_milkType]!.toStringAsFixed(2)}',
                          ),
                        if (_extraShot)
                          _buildCustomizationChip('Extra Shot +\$1.00'),
                        if (_whippedCream)
                          _buildCustomizationChip('Whipped Cream +\$0.50'),
                      ],
                    ),
                  ],
                ),
              ),

            // Price Summary
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Total Price',
                      style: TextStyle(fontSize: 14, color: Colors.grey[600]),
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
                if (_quantity > 1)
                  Text(
                    '\$${_currentPrice.toStringAsFixed(2)} each',
                    style: TextStyle(fontSize: 14, color: Colors.grey[600]),
                  ),
              ],
            ),

            const SizedBox(height: 16),

            // Add to Cart Button
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: _addToCart,
                style: ElevatedButton.styleFrom(
                  backgroundColor: primaryBrown,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                  elevation: 2,
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.shopping_cart),
                    const SizedBox(width: 8),
                    Text(
                      'Add to Cart${_quantity > 1 ? ' ($_quantity)' : ''}',
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCustomizationChip(String label) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: primaryBrown.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: primaryBrown.withOpacity(0.3)),
      ),
      child: Text(
        label,
        style: const TextStyle(
          fontSize: 11,
          color: primaryBrown,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }
}
