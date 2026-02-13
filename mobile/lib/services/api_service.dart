import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';

class ApiService {
  String? _token;

  void setToken(String? token) {
    _token = token;
  }

  Map<String, String> get _headers {
    final headers = <String, String>{
      'Content-Type': 'application/json',
    };
    if (_token != null) {
      headers['Authorization'] = 'Bearer $_token';
    }
    return headers;
  }

  Future<dynamic> get(String path) async {
    final response = await http
        .get(
          Uri.parse('${ApiConfig.baseUrl}$path'),
          headers: _headers,
        )
        .timeout(const Duration(seconds: ApiConfig.connectTimeout));

    return _handleResponse(response);
  }

  Future<dynamic> post(String path, Map<String, dynamic> body) async {
    final response = await http
        .post(
          Uri.parse('${ApiConfig.baseUrl}$path'),
          headers: _headers,
          body: jsonEncode(body),
        )
        .timeout(const Duration(seconds: ApiConfig.connectTimeout));

    return _handleResponse(response);
  }

  Future<dynamic> put(String path, [Map<String, dynamic>? body]) async {
    final response = await http
        .put(
          Uri.parse('${ApiConfig.baseUrl}$path'),
          headers: _headers,
          body: body != null ? jsonEncode(body) : null,
        )
        .timeout(const Duration(seconds: ApiConfig.connectTimeout));

    return _handleResponse(response);
  }

  dynamic _handleResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) return null;
      return jsonDecode(response.body);
    } else {
      final body = response.body.isNotEmpty ? jsonDecode(response.body) : {};
      throw ApiException(
        statusCode: response.statusCode,
        message: body['error'] ?? body['message'] ?? 'Request failed',
      );
    }
  }
}

class ApiException implements Exception {
  final int statusCode;
  final String message;

  ApiException({required this.statusCode, required this.message});

  @override
  String toString() => message;
}
