import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../config/theme.dart';
import '../../providers/cart_provider.dart';
import '../../providers/auth_provider.dart';
import '../../services/api_service.dart';
import '../../services/order_service.dart';
import 'payment_screen.dart';

class CheckoutScreen extends StatefulWidget {
  final VoidCallback onOrderPlaced;

  const CheckoutScreen({super.key, required this.onOrderPlaced});

  @override
  State<CheckoutScreen> createState() => _CheckoutScreenState();
}

class _CheckoutScreenState extends State<CheckoutScreen> {
  final _addressController = TextEditingController();
  final _phoneController = TextEditingController();
  final _noteController = TextEditingController();
  bool _isPlacing = false;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      final auth = context.read<AuthProvider>();
      if (auth.customer != null) {
        context.read<CartProvider>().setLoyaltyInfo(
          availablePoints: auth.customer!.loyaltyPoints,
          pointValue: auth.customer!.loyaltyRedeemRate,
        );
      }
    });
  }

  @override
  void dispose() {
    _addressController.dispose();
    _phoneController.dispose();
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _placeOrder() async {
    final cart = context.read<CartProvider>();

    if (cart.orderType == 'DELIVERY') {
      if (_addressController.text.trim().isEmpty || _phoneController.text.trim().isEmpty) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Please fill in delivery address and phone'),
            backgroundColor: AppTheme.error,
            behavior: SnackBarBehavior.floating,
          ),
        );
        return;
      }
      cart.setDeliveryInfo(
        address: _addressController.text.trim(),
        phone: _phoneController.text.trim(),
        note: _noteController.text.trim(),
      );
    }

    cart.setOrderNote(_noteController.text.trim().isNotEmpty ? _noteController.text.trim() : null);

    setState(() => _isPlacing = true);

    try {
      final orderService = OrderService(context.read<ApiService>());
      final order = await orderService.placeOrder(cart.toOrderJson());
      cart.clear();
      
      if (mounted) {
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(
            builder: (_) => PaymentScreen(
              orderId: order.orderId,
              totalAmount: order.totalAmount,
              onPaymentSuccess: widget.onOrderPlaced,
            ),
          ),
        );
      }
    } catch (e) {
      setState(() => _isPlacing = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed: $e'),
            backgroundColor: AppTheme.error,
            behavior: SnackBarBehavior.floating,
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final cart = context.watch<CartProvider>();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Checkout Details'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.fromLTRB(20, 20, 20, 160),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Order type
            _buildSectionHeader('Dining Option'),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: _orderTypeChip('TAKEAWAY', Icons.shopping_bag_outlined, cart),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: _orderTypeChip('DELIVERY', Icons.delivery_dining, cart),
                ),
              ],
            ),

            // Delivery fields
            if (cart.orderType == 'DELIVERY') ...[
              const SizedBox(height: 32),
              _buildSectionHeader('Delivery Destination'),
              const SizedBox(height: 16),
              Container(
                decoration: BoxDecoration(
                  color: AppTheme.surface,
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Column(
                  children: [
                    TextField(
                      controller: _addressController,
                      style: const TextStyle(color: AppTheme.textPrimary, fontSize: 14),
                      decoration: const InputDecoration(
                        hintText: 'Unit number, street, landmark...',
                        prefixIcon: Icon(Icons.location_on_outlined, color: AppTheme.primary),
                        border: InputBorder.none,
                        enabledBorder: InputBorder.none,
                        focusedBorder: InputBorder.none,
                        contentPadding: EdgeInsets.all(20),
                      ),
                      maxLines: 2,
                    ),
                    const Divider(height: 1, color: AppTheme.surfaceLight, indent: 20, endIndent: 20),
                    TextField(
                      controller: _phoneController,
                      keyboardType: TextInputType.phone,
                      style: const TextStyle(color: AppTheme.textPrimary, fontSize: 14),
                      decoration: const InputDecoration(
                        hintText: 'Recipient phone number',
                        prefixIcon: Icon(Icons.phone_outlined, color: AppTheme.primary),
                        border: InputBorder.none,
                        enabledBorder: InputBorder.none,
                        focusedBorder: InputBorder.none,
                        contentPadding: EdgeInsets.all(20),
                      ),
                    ),
                  ],
                ),
              ),
            ],

            // Loyalty Points (Glassmorphism card)
            const SizedBox(height: 32),
            _buildSectionHeader('Loyalty Rewards'),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: cart.useLoyaltyPoints ? AppTheme.primary.withValues(alpha: 0.05) : AppTheme.surface,
                borderRadius: BorderRadius.circular(24),
                border: Border.all(
                  color: cart.useLoyaltyPoints ? AppTheme.primary.withValues(alpha: 0.3) : Colors.transparent,
                ),
              ),
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: AppTheme.primary.withValues(alpha: 0.1),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(Icons.stars_rounded, color: AppTheme.primary, size: 28),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Redeem Points',
                          style: const TextStyle(
                            fontWeight: FontWeight.w800,
                            fontSize: 16,
                          ),
                        ),
                        Text(
                          'Available: ${cart.availablePoints} pts',
                          style: TextStyle(
                            color: AppTheme.textSecondary,
                            fontWeight: FontWeight.w600,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                  ),
                  if (cart.availablePoints > 0)
                    Switch(
                      value: cart.useLoyaltyPoints,
                      onChanged: (value) => cart.toggleLoyaltyPoints(value),
                      activeTrackColor: AppTheme.primary,
                      activeThumbColor: Colors.white,
                    ),
                ],
              ),
            ),
            if (cart.useLoyaltyPoints)
              Container(
                margin: const EdgeInsets.only(top: 12),
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                decoration: BoxDecoration(
                  color: AppTheme.primary.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Icon(Icons.check_circle_rounded, color: AppTheme.primary, size: 14),
                    const SizedBox(width: 8),
                    Text(
                      'Saving \$${cart.loyaltyDiscount.toStringAsFixed(2)} with loyalty rewards',
                      style: const TextStyle(
                        color: AppTheme.primary,
                        fontWeight: FontWeight.w700,
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),

            // Order summary
            const SizedBox(height: 32),
            _buildSectionHeader('Order Items'),
            const SizedBox(height: 16),
            Container(
              decoration: BoxDecoration(
                color: AppTheme.surface,
                borderRadius: BorderRadius.circular(24),
              ),
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  ...cart.items.asMap().entries.map((entry) => Padding(
                        padding: const EdgeInsets.only(bottom: 12),
                        child: Row(
                          children: [
                            Container(
                              width: 24,
                              height: 24,
                              decoration: BoxDecoration(
                                color: AppTheme.primary.withValues(alpha: 0.1),
                                borderRadius: BorderRadius.circular(6),
                              ),
                              alignment: Alignment.center,
                              child: Text('${entry.value.qty}',
                                  style: const TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w800, fontSize: 12)),
                            ),
                            const SizedBox(width: 12),
                            Expanded(
                              child: Text(
                                entry.value.name,
                                style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
                              ),
                            ),
                            Text('\$${entry.value.totalPrice.toStringAsFixed(2)}',
                                style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 14)),
                          ],
                        ),
                      )),
                  const Padding(
                    padding: EdgeInsets.symmetric(vertical: 8),
                    child: Divider(color: AppTheme.surfaceLight),
                  ),
                  _summaryRow('Subtotal', '\$${cart.subtotal.toStringAsFixed(2)}'),
                  if (cart.orderType == 'DELIVERY')
                    _summaryRow('Delivery Fee', '\$${cart.deliveryFee.toStringAsFixed(2)}'),
                  if (cart.useLoyaltyPoints)
                    _summaryRow('Loyalty Discount', '-\$${cart.loyaltyDiscount.toStringAsFixed(2)}', 
                        isNegative: true),
                ],
              ),
            ),
            
            // Notes
            const SizedBox(height: 24),
            Text('ADDITIONAL NOTES',
                style: TextStyle(
                  color: AppTheme.textSecondary,
                  fontWeight: FontWeight.w800,
                  fontSize: 10,
                  letterSpacing: 1.5,
                )),
            const SizedBox(height: 12),
            TextField(
              controller: _noteController,
              style: const TextStyle(color: AppTheme.textPrimary, fontSize: 14),
              decoration: InputDecoration(
                hintText: 'Anything else we should know?',
                fillColor: AppTheme.surface,
                filled: true,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: BorderSide.none),
                contentPadding: const EdgeInsets.all(16),
              ),
              maxLines: 2,
            ),

          ],
        ),
      ),
      bottomNavigationBar: ClipRRect(
        child: BackdropFilter(
          filter: ImageFilter.blur(sigmaX: 16, sigmaY: 16),
          child: Container(
            padding: const EdgeInsets.fromLTRB(24, 24, 24, 40),
            decoration: BoxDecoration(
              color: AppTheme.background.withValues(alpha: 0.8),
              border: Border(top: BorderSide(color: AppTheme.surfaceLight.withValues(alpha: 0.5))),
            ),
            child: SafeArea(
              child: Row(
                children: [
                  Expanded(
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'TOTAL TO PAY',
                          style: TextStyle(
                            color: AppTheme.textSecondary,
                            fontWeight: FontWeight.w800,
                            fontSize: 10,
                            letterSpacing: 1.5,
                          ),
                        ),
                        Text(
                          '\$${cart.totalAmount.toStringAsFixed(2)}',
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
                        onPressed: _isPlacing ? null : _placeOrder,
                        style: ElevatedButton.styleFrom(
                          backgroundColor: AppTheme.primary,
                          foregroundColor: AppTheme.background,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                        ),
                        child: _isPlacing
                            ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2, color: AppTheme.background))
                            : const Text('CONFIRM ORDER', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1)),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Text(
      title.toUpperCase(),
      style: const TextStyle(
        color: AppTheme.primary,
        fontWeight: FontWeight.w800,
        fontSize: 11,
        letterSpacing: 2,
      ),
    );
  }

  Widget _orderTypeChip(String type, IconData icon, CartProvider cart) {
    final selected = cart.orderType == type;
    return InkWell(
      onTap: () => cart.setOrderType(type),
      borderRadius: BorderRadius.circular(20),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 250),
        padding: const EdgeInsets.symmetric(vertical: 20),
        decoration: BoxDecoration(
          color: selected ? AppTheme.primary : AppTheme.surface,
          borderRadius: BorderRadius.circular(20),
          boxShadow: selected ? [
            BoxShadow(
              color: AppTheme.primary.withValues(alpha: 0.3),
              blurRadius: 15,
              offset: const Offset(0, 8),
            )
          ] : [],
        ),
        child: Column(
          children: [
            Icon(icon, color: selected ? AppTheme.background : AppTheme.textSecondary, size: 28),
            const SizedBox(height: 8),
            Text(
              type,
              style: TextStyle(
                color: selected ? AppTheme.background : AppTheme.textSecondary,
                fontWeight: FontWeight.w900,
                fontSize: 12,
                letterSpacing: 1,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _summaryRow(String label, String value, {bool isBold = false, bool isNegative = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: TextStyle(
                color: isBold ? AppTheme.textPrimary : AppTheme.textSecondary,
                fontWeight: isBold ? FontWeight.w800 : FontWeight.w600,
                fontSize: 14,
              )),
          Text(value,
              style: TextStyle(
                color: isBold ? AppTheme.primary : (isNegative ? AppTheme.primary : AppTheme.textPrimary),
                fontWeight: isBold ? FontWeight.w800 : FontWeight.w700,
                fontSize: 14,
              )),
        ],
      ),
    );
  }
}
