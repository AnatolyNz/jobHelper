package mate.academy.service.impl;

import java.util.List;
import mate.academy.service.GrammarCheckService;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;
import org.springframework.stereotype.Service;

@Service
public class GrammarCheckServiceImpl implements GrammarCheckService {

    private final JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());

    @Override
    public String checkGrammar(String text) {
        try {
            List<RuleMatch> matches = langTool.check(text);

            if (matches.isEmpty()) {
                return "No major grammar or spelling issues found.";
            }

            StringBuilder feedback = new StringBuilder("Grammar and spelling issues found:\n");
            for (RuleMatch match : matches) {
                feedback.append("- At position ")
                        .append(match.getFromPos())
                        .append(": ")
                        .append(match.getMessage())
                        .append(" (Suggestion: ")
                        .append(String.join(", ", match.getSuggestedReplacements()))
                        .append(")\n");
            }
            return feedback.toString();

        } catch (Exception e) {
            return "Error running grammar analysis: " + e.getMessage();
        }
    }
}
