import 'package:flutter/material.dart';
import 'package:introduction_screen/introduction_screen.dart';

class IntroductionScreenCafe extends StatelessWidget {
  const IntroductionScreenCafe({super.key});

  // Define the pages for the introduction screen
  List<PageViewModel> get listPagesViewModel => [
    PageViewModel(
      title: "Welcome to Cafe App",
      body:
          "Manage your cafe operations efficiently with our comprehensive solution.",
      image: const Center(
        child: Icon(Icons.local_cafe, size: 100.0, color: Colors.brown),
      ),
    ),
    PageViewModel(
      title: "Point of Sale",
      body:
          "Process orders quickly and efficiently with our intuitive POS system.",
      image: const Center(
        child: Icon(Icons.point_of_sale, size: 100.0, color: Colors.green),
      ),
    ),
    PageViewModel(
      title: "Inventory Management",
      body: "Keep track of your stock levels and manage inventory seamlessly.",
      image: const Center(
        child: Icon(Icons.inventory, size: 100.0, color: Colors.blue),
      ),
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IntroductionScreen(
        pages: listPagesViewModel,
        showSkipButton: true,
        showNextButton: false,
        skip: const Text("Skip"),
        done: const Text("Done"),
        onDone: () {
          // Navigate to login screen
          Navigator.of(context).pushReplacementNamed('/login');
        },
        onSkip: () {
          // Navigate to login screen when skipped
          Navigator.of(context).pushReplacementNamed('/login');
        },
      ),
    );
  }
}
