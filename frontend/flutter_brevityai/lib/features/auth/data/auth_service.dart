import '../../../core/services/api_service.dart';

class AuthService {
  Future<String?> login(String username, String password) async {

    try {
      final response = await ApiService.post("/auth/login", {
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

  Future<String?> register(String username, String email, String password) async {

    try {
      final response = await ApiService.post("/auth/register", {
        "username": username,
        "email": email,
        "password":password,
      }); 

      return response["message"];

      } catch (e) {
        print("Register Error: $e");
        return "Something went wrong";
      }

  }

}