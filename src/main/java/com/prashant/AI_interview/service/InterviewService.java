package com.prashant.AI_interview.service;

import com.prashant.AI_interview.dto.InterviewRequest;
import com.prashant.AI_interview.dto.InterviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InterviewService {

    @Autowired
    private AIService aiService;

    public InterviewResponse processInterview(InterviewRequest request) {

        String technology = request.getTechnology();
        String answer = request.getAnswer();

        String prompt;

        // First question — no answer yet
        if (answer == null || answer.trim().isEmpty()) {
            prompt = """
                    You are a strict technical interviewer conducting a %s interview.
                    Ask the first interview question.
                    
                    Respond in EXACTLY this format, nothing else:
                    QUESTION: <your question here>
                    FEEDBACK: N/A
                    SCORE: 0
                    """.formatted(technology);
        } else {
            prompt = """
                    You are a strict technical interviewer conducting a %s interview.
                    
                    The candidate answered: "%s"
                    
                    1. Evaluate their answer strictly and give honest feedback.
                    2. Give a score out of 10 based on accuracy and depth.
                    3. Ask the next interview question on %s.
                    
                    Respond in EXACTLY this format, nothing else:
                    QUESTION: <next question here>
                    FEEDBACK: <your feedback here>
                    SCORE: <number between 0-10>
                    """.formatted(technology, answer, technology);
        }

        String aiResponse;

        try {
            aiResponse = aiService.askAI(prompt);
        } catch (Exception e) {
            return new InterviewResponse(
                    "AI service temporarily unavailable. Please try again in a moment.",
                    e.getMessage(),
                    0
            );
        }

        return parseResponse(aiResponse);
    }

    private InterviewResponse parseResponse(String aiResponse) {
        String question = "Could not parse question.";
        String feedback = "Could not parse feedback.";
        int score = 0;

        try {
            String[] lines = aiResponse.split("\n");

            for (String line : lines) {
                line = line.trim();

                if (line.startsWith("QUESTION:")) {
                    question = line.substring("QUESTION:".length()).trim();
                }
                else if (line.startsWith("FEEDBACK:")) {
                    feedback = line.substring("FEEDBACK:".length()).trim();
                }
                else if (line.startsWith("SCORE:")) {

                    String scoreStr = line.substring("SCORE:".length()).trim();

                    // Handles cases like "7/10" or "7 out of 10"
                    scoreStr = scoreStr.replaceAll("[^0-9].*", "").trim();

                    if (!scoreStr.isEmpty()) {
                        score = Integer.parseInt(scoreStr);
                    }
                }
            }

        } catch (Exception e) {
            feedback = "Parsing error: " + e.getMessage();
        }

        return new InterviewResponse(question, feedback, score);
    }
}