package mate.academy.service;

public interface SuggestionService {
    String analyze(String resumeText);

    String suggestVerbs(String text);

    String suggestQuantification(String text);

    String inferSoftSkills(String text);

    String estimateTone(String text);
}
