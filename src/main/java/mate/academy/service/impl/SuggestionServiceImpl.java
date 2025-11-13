package mate.academy.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mate.academy.service.SuggestionService;
import org.springframework.stereotype.Service;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    // English + Ukrainian action verbs
    private static final List<String> ACTION_VERBS = List.of(
            "developed", "managed", "led", "implemented", "built",
            "designed", "optimized", "analyzed",
            // Ukrainian equivalents
            "розробив", "керував", "очолював", "впровадив",
            "створив", "спроєктував", "оптимізував", "аналізував"
    );

    // English + Ukrainian soft skills
    private static final List<String> SOFT_SKILLS = List.of(
            "communication", "teamwork", "leadership",
            "adaptability", "problem-solving", "collaboration",
            // Ukrainian equivalents
            "комунікація", "робота в команді", "лідерство",
            "адаптивність", "розв'язання проблем", "співпраця"
    );

    private static final Pattern QUANTIFICATION_PATTERN = Pattern.compile("\\b\\d+(\\.\\d+)?\\b");

    @Override
    public String analyze(String resumeText) {
        StringBuilder feedback = new StringBuilder("🧠 Suggestion Summary:\n");
        String lowerText = resumeText.toLowerCase();

        // --- Action verbs ---
        long verbMatches = ACTION_VERBS.stream()
                .filter(lowerText::contains)
                .count();
        if (verbMatches >= 3) {
            feedback.append("✅ Гарне використання дієслів дії (EN/UA).\n");
        } else {
            feedback.append("📌 Додайте більше дієслів дії "
                    + "(e.g., developed, managed, implemented / розробив, впровадив).\n");
        }

        // --- Quantification (numbers) ---
        Matcher matcher = QUANTIFICATION_PATTERN.matcher(resumeText);
        int numberCount = 0;
        while (matcher.find()) {
            numberCount++;
        }
        if (numberCount >= 3) {
            feedback.append("✅ Досягнення добре кількісно виражені.\n");
        } else {
            feedback.append("📌 Додайте вимірювані результати "
                    + "(e.g., 'increased sales by 25%' / 'підвищив продажі на 25%').\n");
        }

        // --- Soft skills ---
        long softSkillMentions = SOFT_SKILLS.stream()
                .filter(lowerText::contains)
                .count();
        if (softSkillMentions >= 2) {
            feedback.append("✅ Включає відповідні м'які навички (EN/UA).\n");
        } else {
            feedback.append("📌 Подумайте про додавання м'яких навичок "
                    + "(e.g., communication, teamwork / комунікація, робота в команді).\n");
        }

        return feedback.toString();
    }

    @Override
    public String suggestVerbs(String text) {
        String lowerText = text.toLowerCase();
        StringBuilder suggestions = new StringBuilder("Розгляньте можливість "
                + "додавання цих дієслів дії:\n");
        boolean hasSuggestions = false;

        for (String verb : ACTION_VERBS) {
            if (!lowerText.contains(verb)) {
                suggestions.append("- ").append(verb).append("\n");
                hasSuggestions = true;
            }
        }

        return hasSuggestions ? suggestions.toString()
                : "Чудово! У вас є хороший вибір дієслів дії (англійською чи українською).";
    }

    @Override
    public String suggestQuantification(String text) {
        Matcher matcher = QUANTIFICATION_PATTERN.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }

        if (count >= 3) {
            return "✅ Ваше резюме має достатню кількісну оцінку з метриками.";
        } else {
            return "📌 Спробуйте додати більше цифр та вимірюваних результатів "
                    + "(e.g., 'increased efficiency by 30%' / 'зменшив витрати на 30%').";
        }
    }

    @Override
    public String inferSoftSkills(String text) {
        String lowerText = text.toLowerCase();
        StringBuilder foundSkills = new StringBuilder("Detected soft skills:\n");
        boolean anyFound = false;

        for (String skill : SOFT_SKILLS) {
            if (lowerText.contains(skill)) {
                foundSkills.append("- ").append(skill).append("\n");
                anyFound = true;
            }
        }

        return anyFound ? foundSkills.toString()
                : "📌 М’які навички не вказані. Розгляньте можливість їх додати. "
                + "skills like communication, teamwork / комунікація, робота в команді.";
    }

    @Override
    public String estimateTone(String text) {
        String lowerText = text.toLowerCase();

        if (lowerText.contains("success") || lowerText.contains("achieved")
                || lowerText.contains("exceeded") || lowerText.contains("успішно")
                || lowerText.contains("досяг") || lowerText.contains("перевищив")) {
            return "Positive";
        } else if (lowerText.contains("failed") || lowerText.contains("problem")
                || lowerText.contains("issue") || lowerText.contains("невдача")
                || lowerText.contains("проблема") || lowerText.contains("помилка")) {
            return "Negative";
        } else {
            return "Neutral";
        }
    }
}
