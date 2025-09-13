package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import mate.academy.model.Resume;
import mate.academy.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ResumeParserServiceTest {
    private SkillRepository skillRepository;
    private ResumeParserService resumeParserService;

    @BeforeEach
    void setUp() {
        skillRepository = mock(SkillRepository.class);
        resumeParserService = new ResumeParserService(skillRepository);
    }

    @Test
    @DisplayName("Should extract text from byte array")
    void extractTextFromFileData_ShouldReturnText() {
        String sampleText = "This is a test resume text";
        byte[] data = sampleText.getBytes();

        String extracted = resumeParserService.extractTextFromFileData(data);

        assertNotNull(extracted);
        assertTrue(extracted.contains("test") || extracted.length() > 0);
    }

    @Test
    @DisplayName("Should extract text from Resume object")
    void extractTextFromResume_ShouldReturnText() {
        Resume resume = new Resume();
        resume.setFileData("Sample resume content".getBytes());

        String extracted = resumeParserService.extractTextFromResume(resume);

        assertNotNull(extracted);
    }

    @Test
    @DisplayName("Should filter known skills from text")
    void extractSkills_ShouldReturnMatchingSkills() {
        when(skillRepository.findAllSkillsUsedInJobs()).thenReturn(List.of("Java", "Docker"));

        List<String> result = resumeParserService.extractSkills("I know Java and Spring Boot");

        assertEquals(List.of("Java"), result);
    }
}
