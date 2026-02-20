import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:firebase_core/firebase_core.dart';
import 'config/theme.dart';
import 'services/api_service.dart';
import 'services/notification_service.dart';
import 'providers/auth_provider.dart';
import 'providers/cart_provider.dart';
import 'providers/menu_provider.dart';
import 'screens/splash_screen.dart';
import 'screens/auth/login_screen.dart';
import 'screens/auth/register_screen.dart';
import 'screens/home/menu_screen.dart';
import 'screens/menu/item_detail_screen.dart';
import 'screens/menu/search_screen.dart';
import 'screens/cart/cart_screen.dart';
import 'screens/order/checkout_screen.dart';
import 'screens/order/order_list_screen.dart';
import 'screens/order/order_detail_screen.dart';
import 'screens/profile/profile_screen.dart';
import 'widgets/floating_nav_bar.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  if (NotificationService.isSupported) {
    try {
      await Firebase.initializeApp();
      await NotificationService().init();
    } catch (e) {
      debugPrint('Firebase initialization failed: $e');
    }
  } else {
    debugPrint('Firebase/FCM not supported on this platform. Skipping initialization.');
  }

  runApp(const CafeApp());
}

class CafeApp extends StatelessWidget {
  const CafeApp({super.key});

  @override
  Widget build(BuildContext context) {
    final apiService = ApiService();

    return MultiProvider(
      providers: [
        Provider<ApiService>.value(value: apiService),
        ChangeNotifierProvider(create: (_) => AuthProvider(apiService)),
        ChangeNotifierProvider(create: (_) => CartProvider()),
        ChangeNotifierProvider(create: (_) => MenuProvider(apiService)),
      ],
      child: MaterialApp(
        title: 'Cafe App',
        debugShowCheckedModeBanner: false,
        theme: AppTheme.darkTheme,
        home: const AppShell(),
      ),
    );
  }
}

class AppShell extends StatefulWidget {
  const AppShell({super.key});

  @override
  State<AppShell> createState() => _AppShellState();
}

class _AppShellState extends State<AppShell> {
  int _currentTab = 0;
  bool _showRegister = false;

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();

    // Loading state
    if (auth.isLoading && auth.token == null && auth.customer == null) {
      return const SplashScreen();
    }

    // Not logged in
    if (!auth.isLoggedIn) {
      return _showRegister
          ? RegisterScreen(onLoginTap: () => setState(() => _showRegister = false))
          : LoginScreen(onRegisterTap: () => setState(() => _showRegister = true));
    }

    // Logged in — show main app with bottom nav
    return Scaffold(
      extendBody: true,
      body: IndexedStack(
        index: _currentTab,
        children: [
          // Tab 0: Browse (Branch → Menu)
          Navigator(
            key: const ValueKey('browse'),
            onGenerateRoute: (_) => MaterialPageRoute(
              builder: (_) => _BrowseFlow(),
            ),
          ),
          // Tab 1: Orders
          Navigator(
            key: const ValueKey('orders'),
            onGenerateRoute: (_) => MaterialPageRoute(
              builder: (_) => OrderListScreen(
                onOrderTap: (orderId) {
                  Navigator.of(context).push(
                    MaterialPageRoute(
                      builder: (_) => OrderDetailScreen(orderId: orderId),
                    ),
                  );
                },
              ),
            ),
          ),
          // Tab 2: Profile
          ProfileScreen(
            onLogout: () => setState(() {
              _currentTab = 0;
              _showRegister = false;
            }),
          ),
        ],
      ),
      bottomNavigationBar: FloatingNavBar(
        currentIndex: _currentTab,
        onTap: (i) => setState(() => _currentTab = i),
      ),
    );
  }
}

class _BrowseFlow extends StatefulWidget {
  @override
  State<_BrowseFlow> createState() => _BrowseFlowState();
}

class _BrowseFlowState extends State<_BrowseFlow> {
  @override
  void initState() {
    super.initState();
    // Load branches immediately if they haven't been loaded
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<MenuProvider>().loadBranches();
    });
  }

  @override
  Widget build(BuildContext context) {
    return MenuScreen(
      onItemTap: (menuItemId) {
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (_) => ItemDetailScreen(menuItemId: menuItemId),
          ),
        );
      },
      onCartTap: () {
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (_) => CartScreen(
              onCheckout: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => CheckoutScreen(
                      onOrderPlaced: () {
                        // Pop back to menu
                        Navigator.of(context).popUntil((route) => route.isFirst);
                      },
                    ),
                  ),
                );
              },
            ),
          ),
        );
      },
      onSearchTap: () {
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (_) => SearchScreen(
              onItemTap: (menuItemId) {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (_) => ItemDetailScreen(menuItemId: menuItemId),
                  ),
                );
              },
            ),
          ),
        );
      },
    );
  }
}
