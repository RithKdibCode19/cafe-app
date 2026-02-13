import '../models/order.dart';
import 'api_service.dart';

class OrderService {
  final ApiService _api;

  OrderService(this._api);

  Future<Order> placeOrder(Map<String, dynamic> orderData) async {
    final data = await _api.post('/orders', orderData);
    return Order.fromJson(data);
  }

  Future<List<OrderSummary>> getOrderHistory() async {
    final data = await _api.get('/orders');
    return (data as List).map((o) => OrderSummary.fromJson(o)).toList();
  }

  Future<Order> getOrderDetail(int orderId) async {
    final data = await _api.get('/orders/$orderId');
    return Order.fromJson(data);
  }

  Future<Order> cancelOrder(int orderId) async {
    final data = await _api.put('/orders/$orderId/cancel');
    return Order.fromJson(data);
  }
}
