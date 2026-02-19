import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseURL = "http://10.0.2.2:8080";

  static Future<Map<String, dynamic>> post (
    String endpoint, Map<String, dynamic> data) async {
      final response = await http.post(
        Uri.parse("$baseURL$endpoint"),
        headers: {"Content-Type":"application/json"},
        body: jsonEncode(data),
      );

      if (response.statusCode == 200) {
        return jsonDecode(response.body); // returns token
      } else {
        throw Exception("Failed request: ${response.statusCode} ${response.body}");
      }
    }
}