import 'api_service.dart';

class PaymentService {
  final ApiService _api;

  PaymentService(this._api);

  /// Generates a KHQR code for the given order.
  /// Returns a map containing 'qr', 'md5', 'type', and 'expiresAt'.
  Future<Map<String, dynamic>> generateKhqr(int orderId) async {
    final response = await _api.post('/payments/khqr/$orderId', {});
    if (response['success'] == true && response['data'] != null) {
      return response['data'];
    }
    throw Exception('Failed to generate KHQR: ${response['error'] ?? 'Unknown error'}');
  }

  /// Checks the payment status of the given order.
  /// Optionally takes an MD5 hash to perform a real-time check with Bakong.
  /// Returns a map containing 'orderId', 'status', and 'isPaid'.
  Future<Map<String, dynamic>> checkStatus(int orderId, {String? md5}) async {
    final path = md5 != null ? '/payments/status/$orderId?md5=$md5' : '/payments/status/$orderId';
    return await _api.get(path);
  }
}
