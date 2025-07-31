package mate.academy.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private byte[] fileData;
    private Long userId;
    private List<String> extractedSkills;
}
