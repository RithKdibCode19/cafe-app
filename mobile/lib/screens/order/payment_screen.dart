import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:qr_flutter/qr_flutter.dart';
import '../../config/theme.dart';
import '../../services/api_service.dart';
import '../../services/payment_service.dart';

class PaymentScreen extends StatefulWidget {
  final int orderId;
  final double totalAmount;
  final VoidCallback onPaymentSuccess;

  const PaymentScreen({
    super.key,
    required this.orderId,
    required this.totalAmount,
    required this.onPaymentSuccess,
  });

  @override
  State<PaymentScreen> createState() => _PaymentScreenState();
}

class _PaymentScreenState extends State<PaymentScreen> {
  late final PaymentService _paymentService;
  String? _qrData;
  String? _md5;
  bool _isLoading = true;
  String? _error;
  Timer? _timer;
  bool _isPaid = false;

  @override
  void initState() {
    super.initState();
    _paymentService = PaymentService(context.read<ApiService>());
    _generateHandler();
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _generateHandler() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final data = await _paymentService.generateKhqr(widget.orderId);
      if (mounted) {
        setState(() {
          _qrData = data['qr'];
          _md5 = data['md5'];
          _isLoading = false;
        });
        _startPolling();
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString();
          _isLoading = false;
        });
      }
    }
  }

  void _startPolling() {
    _timer?.cancel();
    _timer = Timer.periodic(const Duration(seconds: 3), (timer) async {
      if (_isPaid) {
        timer.cancel();
        return;
      }

      try {
        final status = await _paymentService.checkStatus(widget.orderId, md5: _md5);
        if (status['isPaid'] == true) {
          if (mounted) {
            setState(() {
              _isPaid = true;
            });
            timer.cancel();
            _showSuccess();
          }
        }
      } catch (e) {
        debugPrint('Polling error: $e');
      }
    });
  }

  void _showSuccess() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        backgroundColor: AppTheme.surface,
        surfaceTintColor: Colors.transparent,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(32)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                color: AppTheme.success.withValues(alpha: 0.1),
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.verified_rounded, color: AppTheme.success, size: 64),
            ),
            const SizedBox(height: 32),
            const Text(
              'PAYMENT SECURED',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.w900, color: AppTheme.textPrimary, letterSpacing: 1),
            ),
            const SizedBox(height: 12),
            Text(
              'Your order has been confirmed and is now being handcrafted by our baristas.',
              textAlign: TextAlign.center,
              style: TextStyle(color: AppTheme.textSecondary, fontWeight: FontWeight.w600, height: 1.5),
            ),
            const SizedBox(height: 40),
            SizedBox(
              width: double.infinity,
              height: 56,
              child: ElevatedButton(
                onPressed: () {
                  Navigator.of(context).pop();
                  widget.onPaymentSuccess();
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppTheme.primary,
                  foregroundColor: AppTheme.background,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                  elevation: 8,
                ),
                child: const Text('BACK TO MENU', style: TextStyle(fontWeight: FontWeight.w900, letterSpacing: 1.5)),
              ),
            ),
            const SizedBox(height: 8),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Bakong Secure Pay'),
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.close_rounded),
          onPressed: () => Navigator.of(context).pop(),
        ),
      ),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 40),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Complete Payment',
                style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.w900,
                  color: AppTheme.textPrimary,
                  letterSpacing: -0.5,
                ),
              ),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    'AMOUNT DUE:',
                    style: TextStyle(color: AppTheme.textSecondary, fontWeight: FontWeight.w700, fontSize: 13, letterSpacing: 1),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    '\$${widget.totalAmount.toStringAsFixed(2)}',
                    style: const TextStyle(
                      fontSize: 18,
                      color: AppTheme.primary,
                      fontWeight: FontWeight.w900,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 48),
              
              Container(
                padding: const EdgeInsets.all(24),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(32),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withValues(alpha: 0.3),
                      blurRadius: 30,
                      offset: const Offset(0, 15),
                    ),
                  ],
                ),
                child: _isLoading
                    ? const SizedBox(
                        width: 240,
                        height: 240,
                        child: Center(child: CircularProgressIndicator(color: AppTheme.primary)),
                      )
                    : _error != null
                        ? SizedBox(
                            width: 240,
                            height: 240,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                const Icon(Icons.error_outline_rounded, color: AppTheme.error, size: 48),
                                const SizedBox(height: 16),
                                Text(
                                  'Generation Failed',
                                  style: TextStyle(fontWeight: FontWeight.w800, color: AppTheme.error),
                                ),
                                const SizedBox(height: 8),
                                Text(
                                  _error!,
                                  textAlign: TextAlign.center,
                                  style: const TextStyle(color: Colors.black54, fontSize: 12),
                                  maxLines: 2,
                                  overflow: TextOverflow.ellipsis,
                                ),
                                const SizedBox(height: 16),
                                TextButton.icon(
                                  onPressed: _generateHandler,
                                  icon: const Icon(Icons.refresh_rounded, size: 18),
                                  label: const Text('RETRY', style: TextStyle(fontWeight: FontWeight.w900)),
                                ),
                              ],
                            ),
                          )
                        : QrImageView(
                            data: _qrData!,
                            version: QrVersions.auto,
                            size: 240.0,
                            eyeStyle: const QrEyeStyle(eyeShape: QrEyeShape.square, color: Colors.black),
                            dataModuleStyle: const QrDataModuleStyle(dataModuleShape: QrDataModuleShape.square, color: Colors.black),
                          ),
              ),
              
              const SizedBox(height: 48),
              
              if (!_isLoading && _error == null) ...[
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  decoration: BoxDecoration(
                    color: AppTheme.surface,
                    borderRadius: BorderRadius.circular(20),
                    border: Border.all(color: AppTheme.primary.withValues(alpha: 0.2)),
                  ),
                  child: const Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      SizedBox(
                        width: 14,
                        height: 14,
                        child: CircularProgressIndicator(strokeWidth: 2, color: AppTheme.primary),
                      ),
                      const SizedBox(width: 16),
                      Text(
                        'Awaiting confirmation...',
                        style: TextStyle(color: AppTheme.primary, fontWeight: FontWeight.w800, fontSize: 13),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 32),
              ],
              
              Text(
                'Please scan the KHQR code above with your Bakong or bank app. Do not leave this page until the process is complete.',
                textAlign: TextAlign.center,
                style: TextStyle(color: AppTheme.textSecondary, height: 1.6, fontSize: 12, fontWeight: FontWeight.w600),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
