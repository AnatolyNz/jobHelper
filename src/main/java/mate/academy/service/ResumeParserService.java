package mate.academy.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import mate.academy.model.Resume;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class ResumeParserService {
    public String extractTextFromResume(Resume resume) {
        try (InputStream input = new ByteArrayInputStream(resume.getFileData())) {
            Tika tika = new Tika();
            return tika.parseToString(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract resume text", e);
        }
    }
}
