import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  //static const String baseURL = "http://192.168.1.212:8080/api";
  static const String baseURL = "http://192.168.1.20:8080/api";

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


  static Future<Map<String, dynamic>> multipartPost({
    required String endpoint,
    required String token,
    required String filePath,
    required Map<String, String> fields
  }) async {
    var uri = Uri.parse("$baseURL$endpoint");
    var request = http.MultipartRequest("POST",uri);

    request.headers['Authorization'] = 'Bearer $token';

    // Add file
    request.files.add(await http.MultipartFile.fromPath('file', filePath));
    
    // Add fields
    request.fields.addAll(fields);
    var response = await request.send();
    var responseBody = await response.stream.bytesToString();

    if (response.statusCode == 200) {
      return Map<String, dynamic>.from(jsonDecode(responseBody));
    } else {
      throw Exception("Failed request: ${response.statusCode} $responseBody");
    }

  }

  static Future<dynamic> get(String endpoint, String token) async {
    final response = await http.get(
      Uri.parse("$baseURL$endpoint"),
      headers: {
        "Authorization": "Bearer $token",
      },
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception(
        "Failed request: ${response.statusCode} ${response.body}",
      );
    }
  }
}