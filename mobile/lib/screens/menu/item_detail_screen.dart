import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:cached_network_image/cached_network_image.dart';
import '../../config/theme.dart';
import '../../models/menu_item.dart';
import '../../models/cart_item.dart';
import '../../providers/menu_provider.dart';
import '../../providers/cart_provider.dart';

class ItemDetailScreen extends StatefulWidget {
  final int menuItemId;

  const ItemDetailScreen({super.key, required this.menuItemId});

  @override
  State<ItemDetailScreen> createState() => _ItemDetailScreenState();
}

class _ItemDetailScreenState extends State<ItemDetailScreen> {
  MenuItemDetail? _detail;
  bool _isLoading = true;
  Variant? _selectedVariant;
  final Set<AddOn> _selectedAddOns = {};
  int _qty = 1;

  @override
  void initState() {
    super.initState();
    _loadDetail();
  }

  Future<void> _loadDetail() async {
    try {
      final detail = await context.read<MenuProvider>().getItemDetail(widget.menuItemId);
      setState(() {
        _detail = detail;
        _selectedVariant = detail.variants.isNotEmpty ? detail.variants.first : null;
        _isLoading = false;
      });
    } catch (e) {
      debugPrint('Error loading item detail: $e');
      setState(() {
        _isLoading = false;
      });
    }
  }

  double get _totalPrice {
    double price = _selectedVariant?.price ?? _detail?.basePrice ?? 0;
    for (final addon in _selectedAddOns) {
      price += addon.price;
    }
    return price * _qty;
  }

  void _addToCart() {
    if (_detail == null) return;
    final cartItem = CartItem(
      menuItemId: _detail!.menuItemId,
      name: _detail!.name,
      imageUrl: _detail!.imageUrl,
      selectedVariant: _selectedVariant,
      selectedAddOns: _selectedAddOns.toList(),
      qty: _qty,
    );
    context.read<CartProvider>().addItem(cartItem);
    Navigator.of(context).pop();
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${_detail!.name} added to cart'),
        backgroundColor: AppTheme.success,
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 1),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBodyBehindAppBar: true,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: Container(
          margin: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: Colors.black.withValues(alpha: 0.3),
            shape: BoxShape.circle,
          ),
          child: IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white, size: 20),
            onPressed: () => Navigator.of(context).pop(),
          ),
        ),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator(color: AppTheme.primary))
          : _detail == null
              ? const Center(child: Text('Item not found'))
              : Stack(
                  children: [
                    SingleChildScrollView(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          // Image with gradient overlay
                          Stack(
                            children: [
                              SizedBox(
                                height: 350,
                                width: double.infinity,
                                child: Hero(
                                  tag: 'item_image_${_detail!.menuItemId}',
                                  child: _detail!.imageUrl != null
                                      ? CachedNetworkImage(
                                          imageUrl: _detail!.imageUrl!,
                                          fit: BoxFit.cover,
                                          errorWidget: (_, __, ___) => _placeholderImage(),
                                        )
                                      : _placeholderImage(),
                                ),
                              ),
                              Positioned.fill(
                                child: Container(
                                  decoration: BoxDecoration(
                                    gradient: LinearGradient(
                                      colors: [
                                        Colors.transparent,
                                        AppTheme.background.withValues(alpha: 0.5),
                                        AppTheme.background,
                                      ],
                                      begin: Alignment.topCenter,
                                      end: Alignment.bottomCenter,
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          
                          Padding(
                            padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                // Name & Category
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Expanded(
                                      child: Column(
                                        crossAxisAlignment: CrossAxisAlignment.start,
                                        children: [
                                          Text(
                                            _detail!.name,
                                            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                                                  fontWeight: FontWeight.w900,
                                                  letterSpacing: -0.5,
                                                ),
                                          ),
                                          if (_detail!.categoryName != null)
                                            Text(
                                              _detail!.categoryName!.toUpperCase(),
                                              style: TextStyle(
                                                color: AppTheme.primary,
                                                fontWeight: FontWeight.w800,
                                                fontSize: 12,
                                                letterSpacing: 1.2,
                                              ),
                                            ),
                                        ],
                                      ),
                                    ),
                                    Text(
                                      '\$${(_selectedVariant?.price ?? _detail!.basePrice).toStringAsFixed(2)}',
                                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                                            color: AppTheme.primary,
                                            fontWeight: FontWeight.w800,
                                          ),
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 24),
                                
                                // Description (if added later, dummy for now)
                                Text(
                                  'Freshly brewed with premium beans. Perfectly roasted to bring out the rich, complex flavors and a smooth, velvety finish.',
                                  style: TextStyle(color: AppTheme.textSecondary, height: 1.5, fontSize: 13),
                                ),
                                
                                // Configuration Section
                                const SizedBox(height: 32),
                                _buildSectionHeader('Customize your coffee'),
                                const SizedBox(height: 16),
                                
                                // Variants
                                if (_detail!.variants.isNotEmpty) ...[
                                  Text('Select Size', style: _subHeaderStyle),
                                  const SizedBox(height: 12),
                                  SizedBox(
                                    height: 48,
                                    child: ListView.separated(
                                      scrollDirection: Axis.horizontal,
                                      itemCount: _detail!.variants.length,
                                      separatorBuilder: (_, __) => const SizedBox(width: 12),
                                      itemBuilder: (context, index) {
                                        final v = _detail!.variants[index];
                                        final selected = _selectedVariant?.variantId == v.variantId;
                                        return InkWell(
                                          onTap: () => setState(() => _selectedVariant = v),
                                          borderRadius: BorderRadius.circular(12),
                                          child: AnimatedContainer(
                                            duration: const Duration(milliseconds: 200),
                                            padding: const EdgeInsets.symmetric(horizontal: 24),
                                            decoration: BoxDecoration(
                                              color: selected ? AppTheme.primary : AppTheme.surface,
                                              borderRadius: BorderRadius.circular(12),
                                              border: Border.all(
                                                color: selected ? AppTheme.primary : AppTheme.surfaceLight.withValues(alpha: 0.5),
                                              ),
                                            ),
                                            alignment: Alignment.center,
                                            child: Text(
                                              v.size,
                                              style: TextStyle(
                                                color: selected ? AppTheme.background : AppTheme.textPrimary,
                                                fontWeight: FontWeight.w800,
                                                fontSize: 13,
                                              ),
                                            ),
                                          ),
                                        );
                                      },
                                    ),
                                  ),
                                  const SizedBox(height: 24),
                                ],

                                // Add-ons
                                if (_detail!.addOns.isNotEmpty) ...[
                                  Text('Extra Add-ons', style: _subHeaderStyle),
                                  const SizedBox(height: 12),
                                  ...(_detail!.addOns.map((addon) {
                                    final selected = _selectedAddOns.contains(addon);
                                    return Container(
                                      margin: const EdgeInsets.only(bottom: 8),
                                      decoration: BoxDecoration(
                                        color: AppTheme.surface,
                                        borderRadius: BorderRadius.circular(16),
                                        border: Border.all(
                                          color: selected ? AppTheme.primary.withValues(alpha: 0.3) : Colors.transparent,
                                        ),
                                      ),
                                      child: CheckboxListTile(
                                        value: selected,
                                        onChanged: (_) {
                                          setState(() {
                                            if (selected) {
                                              _selectedAddOns.remove(addon);
                                            } else {
                                              _selectedAddOns.add(addon);
                                            }
                                          });
                                        },
                                        title: Text(addon.name, style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14)),
                                        subtitle: Text('+\$${addon.price.toStringAsFixed(2)}',
                                            style: const TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w700)),
                                        activeColor: AppTheme.primary,
                                        checkColor: AppTheme.background,
                                        contentPadding: const EdgeInsets.symmetric(horizontal: 16),
                                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                                      ),
                                    );
                                  })),
                                ],

                                // Quantity
                                const SizedBox(height: 24),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                  children: [
                                    Text('Quantity', style: _subHeaderStyle),
                                    Container(
                                      decoration: BoxDecoration(
                                        color: AppTheme.surface,
                                        borderRadius: BorderRadius.circular(12),
                                      ),
                                      padding: const EdgeInsets.all(4),
                                      child: Row(
                                        children: [
                                          _qtyButton(Icons.remove, () {
                                            if (_qty > 1) setState(() => _qty--);
                                          }),
                                          Padding(
                                            padding: const EdgeInsets.symmetric(horizontal: 16),
                                            child: Text(
                                              '$_qty',
                                              style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 16),
                                            ),
                                          ),
                                          _qtyButton(Icons.add, () {
                                            setState(() => _qty++);
                                          }),
                                        ],
                                      ),
                                    ),
                                  ],
                                ),
                                const SizedBox(height: 140),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                    
                    // Bottom Sticky Footer with Blur
                    Positioned(
                      bottom: 0,
                      left: 0,
                      right: 0,
                      child: ClipRRect(
                        child: BackdropFilter(
                          filter: ImageFilter.blur(sigmaX: 12, sigmaY: 12),
                          child: Container(
                            padding: const EdgeInsets.fromLTRB(24, 24, 24, 40),
                            decoration: BoxDecoration(
                              color: AppTheme.background.withValues(alpha: 0.8),
                              border: Border(top: BorderSide(color: AppTheme.surfaceLight.withValues(alpha: 0.5))),
                            ),
                            child: Row(
                              children: [
                                Expanded(
                                  child: Column(
                                    mainAxisSize: MainAxisSize.min,
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Text(
                                        'TOTAL PRICE',
                                        style: TextStyle(
                                          color: AppTheme.textSecondary,
                                          fontWeight: FontWeight.w800,
                                          fontSize: 10,
                                          letterSpacing: 1.5,
                                        ),
                                      ),
                                      Text(
                                        '\$${_totalPrice.toStringAsFixed(2)}',
                                        style: const TextStyle(
                                          color: Colors.white,
                                          fontWeight: FontWeight.w900,
                                          fontSize: 24,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                const SizedBox(width: 16),
                                Expanded(
                                  flex: 2,
                                  child: SizedBox(
                                    height: 56,
                                    child: ElevatedButton(
                                      onPressed: _addToCart,
                                      style: ElevatedButton.styleFrom(
                                        backgroundColor: AppTheme.primary,
                                        foregroundColor: AppTheme.background,
                                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                                        elevation: 8,
                                        shadowColor: AppTheme.primary.withValues(alpha: 0.4),
                                      ),
                                      child: const Text('ADD TO CART', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1)),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
    );
  }

  TextStyle get _subHeaderStyle => const TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.w800,
    letterSpacing: -0.2,
  );

  Widget _buildSectionHeader(String title) {
    return Row(
      children: [
        Container(
          height: 1,
          width: 32,
          color: AppTheme.primary,
        ),
        const SizedBox(width: 8),
        Text(
          title.toUpperCase(),
          style: const TextStyle(
            color: AppTheme.primary,
            fontWeight: FontWeight.w800,
            fontSize: 11,
            letterSpacing: 2,
          ),
        ),
      ],
    );
  }

  Widget _qtyButton(IconData icon, VoidCallback onTap) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(8),
      child: Container(
        width: 36,
        height: 36,
        decoration: BoxDecoration(
          color: AppTheme.surfaceLight.withValues(alpha: 0.3),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Icon(icon, color: AppTheme.textPrimary, size: 18),
      ),
    );
  }

  Widget _placeholderImage() {
    return Container(
      color: AppTheme.surfaceLight,
      child: const Center(
        child: Icon(Icons.coffee, size: 60, color: AppTheme.textSecondary),
      ),
    );
  }
}
