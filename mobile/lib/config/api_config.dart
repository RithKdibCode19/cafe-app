class ApiConfig {
  // Change this to your backend URL
  // For Android emulator: 10.0.2.2
  // For iOS simulator: localhost
  // For real device: your machine's local IP
  // For Chrome/web: use localhost
  // For Android emulator: use 10.0.2.2
  // For real device: use your machine's local IP
  static const String baseUrl = 'http://localhost:8081/api/mobile';

  // Timeout in seconds
  static const int connectTimeout = 15;
  static const int receiveTimeout = 15;
}
