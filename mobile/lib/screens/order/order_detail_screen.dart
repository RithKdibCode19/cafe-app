import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../config/theme.dart';
import '../../models/order.dart';
import '../../services/api_service.dart';
import '../../services/order_service.dart';

class OrderDetailScreen extends StatefulWidget {
  final int orderId;

  const OrderDetailScreen({super.key, required this.orderId});

  @override
  State<OrderDetailScreen> createState() => _OrderDetailScreenState();
}

class _OrderDetailScreenState extends State<OrderDetailScreen> {
  Order? _order;
  bool _isLoading = true;
  bool _isCancelling = false;

  @override
  void initState() {
    super.initState();
    _loadOrder();
  }

  Future<void> _loadOrder() async {
    try {
      final orderService = OrderService(context.read<ApiService>());
      final order = await orderService.getOrderDetail(widget.orderId);
      if (mounted) {
        setState(() {
          _order = order;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  Future<void> _cancelOrder() async {
    setState(() => _isCancelling = true);
    try {
      final orderService = OrderService(context.read<ApiService>());
      final order = await orderService.cancelOrder(widget.orderId);
      if (mounted) {
        setState(() {
          _order = order;
          _isCancelling = false;
        });
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Order has been cancelled'),
            backgroundColor: AppTheme.success,
            behavior: SnackBarBehavior.floating,
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isCancelling = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed: $e'),
            backgroundColor: AppTheme.error,
            behavior: SnackBarBehavior.floating,
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(_order != null ? 'ORD-${_order!.orderNo}' : 'Order Details'),
        elevation: 0,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator(color: AppTheme.primary))
          : _order == null
              ? const Center(child: Text('Order not found'))
              : SingleChildScrollView(
                  padding: const EdgeInsets.fromLTRB(24, 24, 24, 120),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Status Tracking Card
                      _buildStatusCard(),
                      const SizedBox(height: 32),

                      // Delivery/Order Info
                      _buildSectionHeader('Logistics Details'),
                      const SizedBox(height: 16),
                      Container(
                        padding: const EdgeInsets.all(20),
                        decoration: BoxDecoration(
                          color: AppTheme.surface,
                          borderRadius: BorderRadius.circular(24),
                        ),
                        child: Column(
                          children: [
                            _infoRow(Icons.access_time_filled_rounded, 'Order Placed', _formatDate(_order!.createdAt!)),
                            if (_order!.branchName != null) ...[
                              const Divider(color: AppTheme.surfaceLight, height: 24),
                              _infoRow(Icons.storefront_rounded, 'Source Branch', _order!.branchName!),
                            ],
                            if (_order!.deliveryAddress != null) ...[
                              const Divider(color: AppTheme.surfaceLight, height: 24),
                              _infoRow(Icons.location_on_rounded, 'Delivery Address', _order!.deliveryAddress!),
                            ],
                            if (_order!.note != null && _order!.note!.isNotEmpty) ...[
                              const Divider(color: AppTheme.surfaceLight, height: 24),
                              _infoRow(Icons.description_rounded, 'Customer Note', _order!.note!),
                            ],
                          ],
                        ),
                      ),

                      const SizedBox(height: 32),
                      _buildSectionHeader('Bill of Items'),
                      const SizedBox(height: 16),
                      Container(
                        decoration: BoxDecoration(
                          color: AppTheme.surface,
                          borderRadius: BorderRadius.circular(24),
                        ),
                        padding: const EdgeInsets.all(20),
                        child: Column(
                          children: [
                            ..._order!.items.asMap().entries.map((entry) => Padding(
                                  padding: const EdgeInsets.only(bottom: 16),
                                  child: Row(
                                    crossAxisAlignment: CrossAxisAlignment.start,
                                    children: [
                                      Container(
                                        width: 32,
                                        height: 32,
                                        decoration: BoxDecoration(
                                          color: AppTheme.primary.withValues(alpha: 0.1),
                                          borderRadius: BorderRadius.circular(10),
                                        ),
                                        alignment: Alignment.center,
                                        child: Text('${entry.value.qty}',
                                            style: const TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w900, fontSize: 13)),
                                      ),
                                      const SizedBox(width: 16),
                                      Expanded(
                                        child: Column(
                                          crossAxisAlignment: CrossAxisAlignment.start,
                                          children: [
                                            Text(
                                              entry.value.menuItemName,
                                              style: const TextStyle(fontWeight: FontWeight.w800, fontSize: 15),
                                            ),
                                            if (entry.value.variantSize != null || entry.value.addonNames.isNotEmpty)
                                              Padding(
                                                padding: const EdgeInsets.only(top: 4),
                                                child: Text(
                                                  [
                                                    if (entry.value.variantSize != null) entry.value.variantSize,
                                                    ...entry.value.addonNames,
                                                  ].join(" • "),
                                                  style: TextStyle(color: AppTheme.textSecondary, fontSize: 12, fontWeight: FontWeight.w600),
                                                ),
                                              ),
                                          ],
                                        ),
                                      ),
                                      Text(
                                        '\$${(entry.value.unitPrice * entry.value.qty).toStringAsFixed(2)}',
                                        style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 15),
                                      ),
                                    ],
                                  ),
                                )),
                            const Padding(
                              padding: EdgeInsets.symmetric(vertical: 8),
                              child: Divider(color: AppTheme.surfaceLight),
                            ),
                            _totalRow('Subtotal', '\$${_order!.subTotal.toStringAsFixed(2)}'),
                            if (_order!.deliveryFee != null && _order!.deliveryFee! > 0)
                              _totalRow('Delivery Surcharge', '\$${_order!.deliveryFee!.toStringAsFixed(2)}'),
                            if (_order!.discountAmount > 0)
                              _totalRow('Loyalty Reward Applied', '-\$${_order!.discountAmount.toStringAsFixed(2)}', isNegative: true),
                            const SizedBox(height: 12),
                            _totalRow('Grand Total', '\$${_order!.totalAmount.toStringAsFixed(2)}', isBold: true),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
      bottomNavigationBar: _order != null && _order!.status == 'PENDING'
          ? SafeArea(
              child: Padding(
                padding: const EdgeInsets.all(24),
                child: SizedBox(
                  width: double.infinity,
                  height: 56,
                  child: OutlinedButton(
                    onPressed: _isCancelling ? null : _cancelOrder,
                    style: OutlinedButton.styleFrom(
                      foregroundColor: AppTheme.error,
                      side: BorderSide(color: AppTheme.error.withValues(alpha: 0.5), width: 1.5),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                    ),
                    child: _isCancelling
                        ? const SizedBox(height: 20, width: 20, child: CircularProgressIndicator(strokeWidth: 2, color: AppTheme.error))
                        : const Text('CANCEL ORDER', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5)),
                  ),
                ),
              ),
            )
          : null,
    );
  }

  Widget _buildStatusCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(24),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(32),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.2),
            blurRadius: 20,
            offset: const Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: _statusColor.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: Icon(_statusIcon, color: _statusColor, size: 40),
          ),
          const SizedBox(height: 20),
          Text(
            _order!.status,
            style: TextStyle(
              color: _statusColor,
              fontWeight: FontWeight.w900,
              fontSize: 24,
              letterSpacing: 1,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Order Method: ${_order!.orderType}',
            style: TextStyle(color: AppTheme.textSecondary, fontWeight: FontWeight.w700, fontSize: 13),
          ),
        ],
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

  Color get _statusColor {
    switch (_order?.status) {
      case 'PENDING':
        return AppTheme.warning;
      case 'CONFIRMED':
      case 'PREPARING':
        return Colors.blueAccent;
      case 'COMPLETED':
        return AppTheme.success;
      case 'CANCELLED':
        return AppTheme.error;
      default:
        return AppTheme.textSecondary;
    }
  }

  IconData get _statusIcon {
    switch (_order?.status) {
      case 'PENDING':
        return Icons.timer_rounded;
      case 'CONFIRMED':
      case 'PREPARING':
        return Icons.coffee_maker_rounded;
      case 'COMPLETED':
        return Icons.verified_rounded;
      case 'CANCELLED':
        return Icons.cancel_rounded;
      default:
        return Icons.info_rounded;
    }
  }

  Widget _infoRow(IconData icon, String label, String value) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(icon, size: 18, color: AppTheme.primary),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: TextStyle(color: AppTheme.textSecondary, fontWeight: FontWeight.w700, fontSize: 11, letterSpacing: 0.5),
              ),
              const SizedBox(height: 2),
              Text(
                value,
                style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 14),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _totalRow(String label, String value, {bool isBold = false, bool isNegative = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: TextStyle(
                color: isBold ? AppTheme.textPrimary : AppTheme.textSecondary,
                fontWeight: isBold ? FontWeight.w900 : FontWeight.w700,
                fontSize: 14,
              )),
          Text(value,
              style: TextStyle(
                color: isBold ? AppTheme.primary : (isNegative ? AppTheme.primary : AppTheme.textPrimary),
                fontWeight: isBold ? FontWeight.w900 : FontWeight.w800,
                fontSize: 14,
              )),
        ],
      ),
    );
  }

  String _formatDate(String dateStr) {
    try {
      final dt = DateTime.parse(dateStr);
      return DateFormat('MMM dd, yyyy • HH:mm').format(dt);
    } catch (_) {
      return dateStr;
    }
  }
}
