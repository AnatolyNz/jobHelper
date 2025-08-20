package mate.academy.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "resumes")
public class Resume {
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToMany
    @JoinTable(
            name = "resume_skills",
            joinColumns = @JoinColumn(name = "resume_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    @ElementCollection
    @CollectionTable(
            name = "resume_extracted_skills",
            joinColumns = @JoinColumn(name = "resume_id")
    )
    @Column(name = "skill", nullable = false)
    private Set<String> extractedSkills;

    @OneToOne(mappedBy = "resume")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AtsEvaluation atsEvaluation;

    @OneToOne(mappedBy = "resume")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private AiAnalyzedResume aiAnalyzedResume;

    @OneToOne(mappedBy = "resume")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AiCustomizedResume aiCustomizedResume;

    @OneToMany(mappedBy = "resume")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<JobMatch> jobMatches;
}
