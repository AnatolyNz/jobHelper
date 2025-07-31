package mate.academy.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import mate.academy.model.Resume;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service
public class ResumeParserService {

    private final Tika tika;

    public ResumeParserService() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("tika-config.xml")) {
            if (is == null) {
                throw new RuntimeException("tika-config.xml not found in resources");
            }
            TikaConfig config = new TikaConfig(is);
            this.tika = new Tika(config);
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException("Failed to initialize Tika with custom config", e);
        }
    }

    public String extractTextFromFileData(byte[] fileData) {
        try (InputStream input = new ByteArrayInputStream(fileData)) {
            return tika.parseToString(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract resume text from bytes", e);
        }
    }

    public String extractTextFromResume(Resume resume) {
        try (InputStream input = new ByteArrayInputStream(resume.getFileData())) {
            return tika.parseToString(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract resume text", e);
        }
    }
}
