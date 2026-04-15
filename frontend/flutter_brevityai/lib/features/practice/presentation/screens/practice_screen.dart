import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../../core/services/audio_service.dart';
import '../../data/practice_service.dart';
import '../../../auth/provider/auth_provider.dart';

class PracticeScreen extends StatefulWidget {
  const PracticeScreen({super.key});

  @override
  State<PracticeScreen> createState() => _PracticeScreenState();
}

class _PracticeScreenState extends State<PracticeScreen> {
  final AudioService audioService = AudioService();
  final PracticeService practiceService = PracticeService();

  bool isRecording = false;
  bool isLoading = false;
  String transcribedText = "";
  String expectedSentence = "";
  Map<String, dynamic>? feedback;

  Future<void> fetchSentence() async {
    final authProvider = Provider.of<AuthProvider>(context, listen:false);

    try {
      final result = await practiceService.getSentence(
        token: authProvider.token!,
      );
      setState(() {
        expectedSentence = result['sentence'] ?? "";
      });
    } catch(e) {
      setState(() {
        expectedSentence = "Error Loading Sentence";
      });
    }
  }

  Future<void> logout() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    authProvider.logout();
    Navigator.pushNamedAndRemoveUntil(context, "/login", (route) => false);
  }

  // Toggle recording and send audio to backend when stopped
  void toggleRecording() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);

    if (!isRecording) {
      // Start recording
      await audioService.startRecording();
      setState(() => isRecording = true);
    } else {
      // Stop recording
      final path = await audioService.stopRecording();
      setState(() => isRecording = false);

      if (path != null && authProvider.token != null) {
        // Show loading
        setState(() => isLoading = true);

        try {
          final result = await practiceService.sendPractice(
            filePath: path,
            expected: expectedSentence,//expectedController.text,
            token: authProvider.token!,
          );

          setState(() {
            transcribedText = result['transcript'] ?? "";
            feedback = result['evaluation'];
          });
        } catch (e) {
          setState(() {
            transcribedText = "Error: $e";
            feedback = null;
          });
        } finally {
          setState(() => isLoading = false);
        }
      }
    }
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    fetchSentence();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Practice Speaking"),
      actions: [
        IconButton(
          icon: const Icon(Icons.logout),
          onPressed: logout,
        ),
      ],
    ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            IconButton(
              icon: Icon(Icons.refresh),
              onPressed: fetchSentence,
            ),
            // Expected sentence input
            Text(
              expectedSentence.isEmpty
              ? "Loading sentence..."
              : expectedSentence,
              style: const TextStyle(fontSize:20, fontWeight: FontWeight.bold),
              textAlign: TextAlign.center,
            ),
            // Display transcript or instructions
            Text(
              transcribedText.isEmpty
                  ? (isRecording ? "Recording..." : "Press mic and speak...")
                  : transcribedText,
              style: const TextStyle(fontSize: 20),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 20),

            // Feedback display
            if (feedback != null) ...[
              Text("Score: ${feedback!['score']}", style: const TextStyle(fontSize: 18)),
              Text("Accuracy: ${feedback!['accuracy']}", style: const TextStyle(fontSize: 16)),
              Text("Mistakes: ${feedback!['mistakes']}", style: const TextStyle(fontSize: 16)),
              Text("Suggestion: ${feedback!['suggestion']}", style: const TextStyle(fontSize: 16)),
              const SizedBox(height: 20),
            ],

            // Record button
            FloatingActionButton(
              onPressed: isLoading ? null : toggleRecording,
              child: isLoading
                  ? const CircularProgressIndicator(color: Colors.white)
                  : Icon(isRecording ? Icons.stop : Icons.mic),
            ),
          ],
        ),
      ),
    );
  }
}