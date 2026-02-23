import 'package:flutter/material.dart';
import 'package:flutter_brevityai/features/practice/presentation/screens/practice_screen.dart';
import 'package:provider/provider.dart';


//import 'package:flutter_brevityai/features/auth/presentation/screens/home_screen.dart';
import 'features/auth/provider/auth_provider.dart';
import 'features/auth/presentation/screens/login_screen.dart';
import 'features/auth/presentation/screens/home_screen.dart';
import 'features/auth/presentation/screens/slash_screen.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => AuthProvider(),
      child: const MyApp()
      ),
    );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'BrevityAI',
      initialRoute: '/', 
      routes: {
        '/': (context) => const SplashScreen(),
        '/login': (context) => const LoginScreen(),
        '/home':(context) => const HomeScreen(),
        '/practice':(context) => const PracticeScreen(),
      },
    );
  }
}