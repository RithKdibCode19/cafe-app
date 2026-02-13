import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/customer.dart';
import '../services/api_service.dart';
import '../services/auth_service.dart';
import '../services/notification_service.dart';

class AuthProvider extends ChangeNotifier {
  final ApiService _apiService;
  late final AuthService _authService;

  Customer? _customer;
  String? _token;
  bool _isLoading = false;
  String? _error;

  AuthProvider(this._apiService) {
    _authService = AuthService(_apiService);
    _loadToken();
  }

  Customer? get customer => _customer;
  String? get token => _token;
  bool get isLoading => _isLoading;
  bool get isLoggedIn => _token != null;
  String? get error => _error;

  Future<void> _loadToken() async {
    final prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('auth_token');
    if (_token != null) {
      _apiService.setToken(_token);
      try {
        _customer = await _authService.getProfile();
      } catch (_) {
        // Token expired or invalid
        await logout();
      }
    }
    notifyListeners();
  }

  Future<bool> register({
    required String phone,
    required String password,
    required String name,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final fcmToken = await NotificationService().getToken();
      final result = await _authService.register(
        phone: phone,
        password: password,
        name: name,
        fcmToken: fcmToken,
      );
      await _saveAuth(result.token, result.customer);
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<bool> login({
    required String phone,
    required String password,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final fcmToken = await NotificationService().getToken();
      final result = await _authService.login(
        phone: phone,
        password: password,
        fcmToken: fcmToken,
      );
      await _saveAuth(result.token, result.customer);
      _isLoading = false;
      notifyListeners();
      return true;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<void> refreshProfile() async {
    if (_token == null) return;
    try {
      _customer = await _authService.getProfile();
      notifyListeners();
    } catch (_) {}
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('auth_token');
    _token = null;
    _customer = null;
    _apiService.setToken(null);
    notifyListeners();
  }

  Future<void> _saveAuth(String token, Customer customer) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('auth_token', token);
    _token = token;
    _customer = customer;
    _apiService.setToken(token);
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
