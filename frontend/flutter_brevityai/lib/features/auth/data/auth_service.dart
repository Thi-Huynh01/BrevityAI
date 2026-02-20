import '../../../core/services/api_service.dart';

class AuthService {
  Future<String?> login(String username, String password) async {

    try {
      final response = await ApiService.post("/api/auth/login", {
      "username": username,
      "password": password, 
      });

      
      if (response["token"] != null) {
        return response["token"];
      } else {
        return null;
      }
    
      //return response["token"];
    } catch (e) {
      print ("Login Error: $e");
      return null;
    }
  } 
}