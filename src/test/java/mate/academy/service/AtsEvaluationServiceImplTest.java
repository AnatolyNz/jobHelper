package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.model.AtsEvaluation;
import mate.academy.model.Resume;
import mate.academy.repository.AtsEvaluationRepository;
import mate.academy.repository.ResumeRepository;
import mate.academy.service.impl.AtsEvaluationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtsEvaluationServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private AtsEvaluationRepository atsEvaluationRepository;

    @Mock
    private ResumeParserService resumeParserService;

    @InjectMocks
    private AtsEvaluationServiceImpl atsEvaluationService;

    private AtsEvaluation createMockEvaluation(double score) {
        AtsEvaluation evaluation = new AtsEvaluation();
        evaluation.setScore(score);
        return evaluation;
    }

    @Test
    void testEvaluateResume_HighScore() {
        Resume resume = new Resume();
        resume.setId(1L);

        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(resumeParserService.extractTextFromResume(resume)).thenReturn("any content");

        AtsEvaluation mockEval = createMockEvaluation(100.0);
        when(atsEvaluationRepository.save(any(AtsEvaluation.class))).thenReturn(mockEval);

        AtsEvaluation result = atsEvaluationService.evaluateResume(1L);

        assertEquals(100.0, result.getScore());
    }

    @Test
    void testEvaluateResume_MediumScore() {
        Resume resume = new Resume();
        resume.setId(2L);

        when(resumeRepository.findById(2L)).thenReturn(Optional.of(resume));
        when(resumeParserService.extractTextFromResume(resume)).thenReturn("any content");

        AtsEvaluation mockEval = createMockEvaluation(50.0);
        when(atsEvaluationRepository.save(any(AtsEvaluation.class))).thenReturn(mockEval);

        AtsEvaluation result = atsEvaluationService.evaluateResume(2L);

        assertEquals(50.0, result.getScore());
    }

    @Test
    void testEvaluateResume_LowScore() {
        Resume resume = new Resume();
        resume.setId(3L);

        when(resumeRepository.findById(3L)).thenReturn(Optional.of(resume));
        when(resumeParserService.extractTextFromResume(resume)).thenReturn("any content");

        AtsEvaluation mockEval = createMockEvaluation(0.0);
        when(atsEvaluationRepository.save(any(AtsEvaluation.class))).thenReturn(mockEval);

        AtsEvaluation result = atsEvaluationService.evaluateResume(3L);

        assertEquals(0.0, result.getScore());
    }

    @Test
    void testEvaluateResume_NotFound() {
        when(resumeRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> atsEvaluationService.evaluateResume(99L));

        assertTrue(exception.getMessage().contains("Resume not found"));
        verify(resumeRepository, times(1)).findById(99L);
    }
}
