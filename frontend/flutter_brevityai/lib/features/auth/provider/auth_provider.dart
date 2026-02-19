import 'package:flutter/material.dart';
import '../data/auth_service.dart';
import '../../../core/services/storage_service.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService = AuthService();

  bool isLoading = false;
  String? token;

  Future<bool> login(String username, String password) async {
    isLoading = true;
    notifyListeners();

    final result = await _authService.login(username, password);

    token = result;

    await StorageService.saveToken(result);

    isLoading = false;
    notifyListeners();

    return true;
  
  }

  Future<void> logout() async {
    token = null;
    await StorageService.clearToken();
    notifyListeners();
  }
}
