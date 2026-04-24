import 'package:flutter_brevityai/core/services/api_service.dart';

class PracticeService {

  Future<Map<String, dynamic>> sendPractice({
    required String filePath,
    required String expected,
    required String token,
  }) async {

    return await ApiService.multipartPost(
      endpoint: "/practice",
      token: token,
      filePath: filePath,
      fields: {
        "expected": expected
      }
    );
  }

  Future<dynamic> getSentence({required String token}) async {
    return await ApiService.get("/practice/generate?difficulty=easy",token);
  }
}
