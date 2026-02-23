import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../provider/auth_provider.dart';

class HomeScreen extends StatelessWidget{ 
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);

    return Scaffold(
      appBar: AppBar(title: const Text("Home")),
      body: Center(
        child: ElevatedButton(
          onPressed: () async {
          //await authProvider.logout();
          Navigator.pushReplacementNamed(context, '/practice');
        },
          child: const Text("practice"),
        ),
      )
    );
  }
}