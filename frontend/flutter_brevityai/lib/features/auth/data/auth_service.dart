import '../../../core/services/api_service.dart';

class AuthService {
  Future<String> login(String username, String password) async {

    try {
      final response = await ApiService.post("/api/auth/login", {
      "username": username,
      "password": password, 
      });

      return response["token"];
    } catch (e) {
      print ("Login Error: $e");
      return "";
    }
  } 
}