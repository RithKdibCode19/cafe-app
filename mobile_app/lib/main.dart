import 'package:flutter/material.dart';
import 'package:mobile_app/screen/introduction_screen_cafe.dart';
import 'package:mobile_app/screen/auth/login_screen.dart';
import 'package:mobile_app/screen/auth/register_screen.dart';
import 'package:mobile_app/screen/dashboard_screen.dart';
import 'package:mobile_app/screen/home/home_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Cafe Manager',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        primarySwatch: Colors.brown,
        visualDensity: VisualDensity.adaptivePlatformDensity,
        fontFamily: 'Roboto',
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => const IntroductionScreenCafe(),
        '/login': (context) => const LoginScreen(),
        '/dashboard': (context) => const DashboardScreen(),
        '/home': (context) => const HomeScreen(),
        '/signup': (context) => const RegisterScreen(),
      },
    );
  }
}
