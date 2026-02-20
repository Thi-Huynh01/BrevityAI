import 'package:flutter/material.dart';
import '../data/auth_service.dart';
import '../../../core/services/storage_service.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService = AuthService();

  bool isLoading = false;
  bool isAuthenticated = false;
  String? token;

  Future<void> checkAuth() async {
    final storedToken = await StorageService.getToken();

    if (storedToken != null) {
      token = storedToken;
      isAuthenticated = true;
    } else {
      isAuthenticated = false;
    }

    notifyListeners();
  }

  Future<bool> login(String username, String password) async {
    isLoading = true;
    notifyListeners();

    final result = await _authService.login(username, password);

    if (result != null && result.isNotEmpty) {
      token = result;
      isAuthenticated = true;
      await StorageService.saveToken(result);
      isLoading = false;
      notifyListeners();
      return true;
    }
    
    isLoading = false;
    notifyListeners();
    return false;
  }

  Future<void> logout() async {
    token = null;
    isAuthenticated = false;
    await StorageService.clearToken();
    notifyListeners();
  }
}
