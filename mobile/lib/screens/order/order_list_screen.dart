import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../config/theme.dart';
import '../../models/order.dart';
import '../../services/api_service.dart';
import '../../services/order_service.dart';

class OrderListScreen extends StatefulWidget {
  final Function(int orderId) onOrderTap;

  const OrderListScreen({super.key, required this.onOrderTap});

  @override
  State<OrderListScreen> createState() => _OrderListScreenState();
}

class _OrderListScreenState extends State<OrderListScreen> {
  List<OrderSummary>? _orders;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadOrders();
  }

  Future<void> _loadOrders() async {
    setState(() => _isLoading = true);
    try {
      final orderService = OrderService(context.read<ApiService>());
      final orders = await orderService.getOrderHistory();
      setState(() {
        _orders = orders;
        _isLoading = false;
      });
    } catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Order History'),
        elevation: 0,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator(color: AppTheme.primary))
          : RefreshIndicator(
              onRefresh: _loadOrders,
              color: AppTheme.primary,
              backgroundColor: AppTheme.surface,
              child: _orders == null || _orders!.isEmpty
                  ? SingleChildScrollView(
                      physics: const AlwaysScrollableScrollPhysics(),
                      child: Container(
                        height: MediaQuery.of(context).size.height * 0.7,
                        alignment: Alignment.center,
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Container(
                              padding: const EdgeInsets.all(32),
                              decoration: BoxDecoration(
                                color: AppTheme.surface,
                                shape: BoxShape.circle,
                              ),
                              child: Icon(Icons.receipt_long_rounded, size: 64, color: AppTheme.primary.withValues(alpha: 0.5)),
                            ),
                            const SizedBox(height: 32),
                            const Text(
                              'No orders found',
                              style: TextStyle(fontWeight: FontWeight.w800, fontSize: 18),
                            ),
                            const SizedBox(height: 12),
                            Text(
                              'Your past coffee rituals will appear here.',
                              style: TextStyle(color: AppTheme.textSecondary, fontSize: 14),
                            ),
                          ],
                        ),
                      ),
                    )
                  : ListView.builder(
                      padding: const EdgeInsets.fromLTRB(20, 16, 20, 100),
                      itemCount: _orders!.length,
                      itemBuilder: (context, index) {
                        final order = _orders![index];
                        return _OrderCard(
                          order: order,
                          onTap: () => widget.onOrderTap(order.orderId),
                        );
                      },
                    ),
            ),
    );
  }
}

class _OrderCard extends StatelessWidget {
  final OrderSummary order;
  final VoidCallback onTap;

  const _OrderCard({required this.order, required this.onTap});

  Color get _statusColor {
    switch (order.status) {
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

  @override
  Widget build(BuildContext context) {
    String formattedDate = '';
    if (order.createdAt != null) {
      try {
        final dt = DateTime.parse(order.createdAt!);
        formattedDate = DateFormat('MMM dd, yyyy • HH:mm').format(dt);
      } catch (_) {
        formattedDate = order.createdAt ?? '';
      }
    }

    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: AppTheme.surface,
        borderRadius: BorderRadius.circular(24),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.2),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(24),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    'ORD-${order.orderNo}',
                    style: const TextStyle(fontWeight: FontWeight.w900, fontSize: 15, letterSpacing: 0.5),
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
                    decoration: BoxDecoration(
                      color: _statusColor.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(10),
                      border: Border.all(color: _statusColor.withValues(alpha: 0.2)),
                    ),
                    child: Text(
                      order.status,
                      style: TextStyle(
                        color: _statusColor,
                        fontSize: 10,
                        fontWeight: FontWeight.w900,
                        letterSpacing: 0.5,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(6),
                    decoration: BoxDecoration(color: AppTheme.background, borderRadius: BorderRadius.circular(8)),
                    child: Icon(
                      order.orderType == 'DELIVERY' ? Icons.delivery_dining_rounded : Icons.local_mall_rounded,
                      size: 16,
                      color: AppTheme.primary,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          order.orderType,
                          style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13),
                        ),
                        if (order.branchName != null)
                          Text(
                            order.branchName!,
                            style: TextStyle(color: AppTheme.textSecondary, fontSize: 11, fontWeight: FontWeight.w600),
                            overflow: TextOverflow.ellipsis,
                          ),
                      ],
                    ),
                  ),
                  Text(
                    '\$${order.totalAmount.toStringAsFixed(2)}',
                    style: const TextStyle(
                      color: AppTheme.primary,
                      fontWeight: FontWeight.w900,
                      fontSize: 18,
                    ),
                  ),
                ],
              ),
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 12),
                child: Divider(color: AppTheme.surfaceLight, height: 1),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    formattedDate,
                    style: TextStyle(color: AppTheme.textSecondary, fontSize: 11, fontWeight: FontWeight.w600),
                  ),
                  const Row(
                    children: [
                      Text('View Details', style: TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w800, fontSize: 11)),
                      SizedBox(width: 4),
                      Icon(Icons.arrow_forward_ios_rounded, size: 10, color: AppTheme.primary),
                    ],
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
