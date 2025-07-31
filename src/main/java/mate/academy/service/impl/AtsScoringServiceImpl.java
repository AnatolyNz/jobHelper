package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.service.AtsScoringService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AtsScoringServiceImpl implements AtsScoringService {

    private static final List<String> SECTIONS = List.of("experience",
            "education", "skills", "contact", "summary");
    private static final List<String> KEYWORDS = List.of("developed",
            "led", "managed", "achieved", "designed", "built");

    @Override
    public double score(String text) {
        String lower = text.toLowerCase();
        long sections = SECTIONS.stream().filter(lower::contains).count();
        long keywords = KEYWORDS.stream().filter(lower::contains).count();
        double sectionScore = (sections / (double) SECTIONS.size()) * 50;
        double keywordScore = Math.min(keywords * 5, 50);
        return sectionScore + keywordScore;
    }
}
