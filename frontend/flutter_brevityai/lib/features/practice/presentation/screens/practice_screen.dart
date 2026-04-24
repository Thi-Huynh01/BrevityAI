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
        transcribedText = "";
        feedback = null;
        expectedSentence = result['sentence'] ?? "";
      });
    } catch(e) {
      setState(() {
        expectedSentence = e.toString();
      });
    }
  }

  Future<void> logout() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    authProvider.logout();
    Navigator.pushNamedAndRemoveUntil(context, "/login", (route) => false);
  }

  Color _getScoreColor(dynamic score) {
    final s = score is int ? score : int.tryParse(score.toString()) ?? 0;

    if (s >= 80) return Colors.green;
    if (s >= 50) return Colors.orange;
    return Colors.red;
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
      appBar: AppBar(title: const Text("Practice Mode"),
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
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Align (
              alignment: Alignment.centerRight,
              child: IconButton(
              icon: Icon(Icons.refresh),
              onPressed: fetchSentence,
            ),
          ),
            // Expected sentence input
            Card(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              elevation: 3,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Text(
                  expectedSentence.isEmpty
                    ? "Loading sentence..."
                    : expectedSentence,
                  style: const TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ),

            const SizedBox(height: 30),

            // Feedback display
            if (feedback != null)
              Padding(
                padding: const EdgeInsets.only(top: 20),
                child: Card(
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  elevation: 4,
                  child: ExpansionTile(
                    initiallyExpanded: true,
                    title: const Text(
                      "Practice Results",
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    subtitle: Text(
                      "Score: ${feedback!['score']}",
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: _getScoreColor(feedback!['score']),
                      ),
                    ),
                    children: [
                      ListTile(
                        title: const Text("What you said:"),
                        subtitle: Text(transcribedText),
                      ),
                      ListTile(
                        title: const Text("Accuracy:"),
                        subtitle: Text("${feedback!['accuracy']}"),
                      ),
                      ListTile(
                        title: const Text("Mistakes:"),
                        subtitle: Text("${feedback!['mistakes']}"),
                      ),
                      ListTile(
                        title: const Text("Suggestion:"),
                        subtitle: Text("${feedback!['suggestion']}"),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),

    // Mic Button
    floatingActionButton: FloatingActionButton(
            onPressed: (isLoading || expectedSentence.isEmpty)
              ? null
              : toggleRecording,
            child: isLoading
              ? const CircularProgressIndicator(color: Colors.white)
              : Icon(isRecording ? Icons.stop : Icons.mic),
          ),
          floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
    );
  }
}