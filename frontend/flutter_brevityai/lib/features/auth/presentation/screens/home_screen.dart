import 'package:flutter/material.dart';

class HomeScreen extends StatelessWidget{ 
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(title: const Text("Home")),
      body: Center(
        child: ElevatedButton(
          onPressed: () async {
          Navigator.pushReplacementNamed(context, '/practice');
        },
          child: const Text("practice"),
        ),
      )
    );
  }
}