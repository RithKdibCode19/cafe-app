class ApiConfig {
  // Use 10.0.2.2 for Android emulator to access host localhost
  // Use your machine's local IP (e.g., 192.168.1.x) for physical devices
  // Use 127.0.0.1 or localhost for web/desktop
  
  static const String baseUrl = 'http://10.0.2.2:8081/api/mobile';
  
  // Example for real device (replace with your IP)
  // static const String baseUrl = 'http://192.168.1.5:8081/api/mobile';

  // Timeout in seconds
  static const int connectTimeout = 15;
  static const int receiveTimeout = 15;
}
