import '../models/customer.dart';
import 'api_service.dart';

class AuthService {
  final ApiService _api;

  AuthService(this._api);

  Future<AuthResult> register({
    required String phone,
    required String password,
    required String name,
    String? fcmToken,
  }) async {
    final data = await _api.post('/auth/register', {
      'phone': phone,
      'password': password,
      'name': name,
      'fcmToken': fcmToken,
    });
    return AuthResult.fromJson(data);
  }

  Future<AuthResult> login({
    required String phone,
    required String password,
    String? fcmToken,
  }) async {
    final data = await _api.post('/auth/login', {
      'phone': phone,
      'password': password,
      'fcmToken': fcmToken,
    });
    return AuthResult.fromJson(data);
  }

  Future<Customer> getProfile() async {
    final data = await _api.get('/auth/profile');
    return Customer.fromJson(data);
  }

  Future<Customer> updateProfile({String? name, String? email, String? fcmToken}) async {
    final body = <String, dynamic>{};
    if (name != null) body['name'] = name;
    if (email != null) body['email'] = email;
    if (fcmToken != null) body['fcmToken'] = fcmToken;
    final data = await _api.put('/auth/profile', body);
    return Customer.fromJson(data);
  }
}

class AuthResult {
  final String token;
  final Customer customer;

  AuthResult({required this.token, required this.customer});

  factory AuthResult.fromJson(Map<String, dynamic> json) {
    return AuthResult(
      token: json['token'],
      customer: Customer.fromJson(json['customer']),
    );
  }
}
