//import 'dart:io';
import 'package:path_provider/path_provider.dart';
import 'package:record/record.dart';

class AudioService {

  final _recorder = AudioRecorder();

  Future<String> getFilePath() async {
    final dir = await getApplicationDocumentsDirectory();
    return '${dir.path}/recording.m4a';
  }

  Future<void> startRecording() async {
    if (await _recorder.hasPermission()) {
      final path = await getFilePath();

      await _recorder.start(
        const RecordConfig(
          encoder: AudioEncoder.aacLc,
          bitRate: 128000,
          sampleRate: 44100,
        ),
        path: path,
      );
      print("Recording started at: $path");
    } else {
      print("No permission");
    }
  }

  Future<String?> stopRecording() async {
    final path = await _recorder.stop();
    print("Recording stopped. File saved at: $path");
    return path;
  }

}

