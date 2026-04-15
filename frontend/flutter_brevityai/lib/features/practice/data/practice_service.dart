import 'dart:convert';
//import 'dart:io';
//import 'package:flutter_brevityai/core/services/api_service.dart';
import 'package:http/http.dart' as http;

class PracticeService {
  final String baseUrl = "http://192.168.1.212:8080/api";
  //final String baseUrl = "http://192.168.1.20:8080/api";

  Future<Map<String, dynamic>> sendPractice({
    required String filePath,
    required String expected,
    required String token,
  }) async {
    var uri = Uri.parse("$baseUrl/practice");

    var request = http.MultipartRequest("POST", uri);

    // JWT
    request.headers['Authorization'] = 'Bearer $token';

    // File
    request.files.add(
      await http.MultipartFile.fromPath(
        'file',
        filePath,
      ),
    );

    // Expected Text
    request.fields['expected'] = expected;

    var response = await request.send();
    var responseBody = await response.stream.bytesToString();

    if (response.statusCode == 200) {
      return Map<String, dynamic>.from(jsonDecode(responseBody));
    } else {
      throw Exception("Practice upload failed: ${response.statusCode} - $responseBody");
    }
  }
  Future<Map<String,dynamic>> getSentence({required String token}) async {
    final response = await http.get(
      Uri.parse("$baseUrl/practice/generate?difficulty=easy"),
      headers: {
        "Authorization": "Bearer $token",
      },
    );
    return jsonDecode(response.body);
  }    
}
