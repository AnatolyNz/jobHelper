package mate.academy.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mate.academy.service.SuggestionService;
import org.springframework.stereotype.Service;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    private static final List<String> ACTION_VERBS = List.of(
            "developed", "managed", "led", "implemented", "built",
            "designed", "optimized", "analyzed"
    );

    private static final List<String> SOFT_SKILLS = List.of(
            "communication", "teamwork", "leadership",
            "adaptability", "problem-solving", "collaboration"
    );

    private static final Pattern QUANTIFICATION_PATTERN = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");

    @Override
    public String analyze(String resumeText) {
        StringBuilder feedback = new StringBuilder("🧠 Suggestion Summary:\n");

        // Action verbs
        long verbMatches = ACTION_VERBS.stream()
                .filter(verb -> resumeText.toLowerCase().contains(verb))
                .count();
        if (verbMatches >= 3) {
            feedback.append("✅ Good use of action verbs.\n");
        } else {
            feedback.append("📌 Add more action verbs (e.g., developed, managed, implemented).\n");
        }

        // Quantification
        Matcher matcher = QUANTIFICATION_PATTERN.matcher(resumeText);
        int numberCount = 0;
        while (matcher.find()) {
            numberCount++;
        }
        if (numberCount >= 3) {
            feedback.append("✅ Achievements are well quantified.\n");
        } else {
            feedback.append("📌 Add metrics to describe "
                    + "your impact (e.g., 'increased sales by 25%').\n");
        }

        // Soft skills
        long softSkillMentions = SOFT_SKILLS.stream()
                .filter(skill -> resumeText.toLowerCase().contains(skill))
                .count();
        if (softSkillMentions >= 2) {
            feedback.append("✅ Includes relevant soft skills.\n");
        } else {
            feedback.append("📌 Consider including soft "
                    + "skills (e.g., communication, problem-solving).\n");
        }

        return feedback.toString();
    }

    @Override
    public String suggestVerbs(String text) {
        String lowerText = text.toLowerCase();
        StringBuilder suggestions = new StringBuilder("Consider adding these action verbs:\n");
        boolean hasSuggestions = false;

        for (String verb : ACTION_VERBS) {
            if (!lowerText.contains(verb)) {
                suggestions.append("- ").append(verb).append("\n");
                hasSuggestions = true;
            }
        }

        return hasSuggestions ? suggestions.toString() : "Great! "
                + "You have a good range of action verbs.";
    }

    @Override
    public String suggestQuantification(String text) {
        Matcher matcher = QUANTIFICATION_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        if (count >= 3) {
            return "Your resume has sufficient quantification with metrics.";
        } else {
            return "Try to add more numbers and metrics "
                    + "to quantify your achievements "
                    + "(e.g., 'increased sales by 25%').";
        }
    }

    @Override
    public String inferSoftSkills(String text) {
        String lowerText = text.toLowerCase();
        StringBuilder foundSkills =
                new StringBuilder("Detected soft skills:\n");
        boolean anyFound = false;

        for (String skill : SOFT_SKILLS) {
            if (lowerText.contains(skill)) {
                foundSkills.append("- ").append(skill).append("\n");
                anyFound = true;
            }
        }

        return anyFound ? foundSkills.toString() :
                "No soft skills detected. Consider adding "
                        + "skills like communication, teamwork, etc.";
    }

    @Override
    public String estimateTone(String text) {
        String lowerText = text.toLowerCase();

        if (lowerText.contains("success") || lowerText
                .contains("achieved") || lowerText.contains("exceeded")) {
            return "Positive";
        } else if (lowerText.contains("failed")
                || lowerText.contains("problem")
                || lowerText.contains("issue")) {
            return "Negative";
        } else {
            return "Neutral";
        }
    }
}
