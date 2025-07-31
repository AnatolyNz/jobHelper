package mate.academy.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ai_analyzed_resumes")
public class AiAnalyzedResume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double atsScore;

    @Column(columnDefinition = "LONGTEXT")
    private String grammarFeedback;

    @Column(columnDefinition = "LONGTEXT")
    private String structureFeedback;

    @Column(columnDefinition = "LONGTEXT")
    private String toneFeedback;

    @Column(columnDefinition = "LONGTEXT")
    private String actionVerbSuggestions;

    @Column(columnDefinition = "LONGTEXT")
    private String quantificationSuggestions;

    @Column(columnDefinition = "LONGTEXT")
    private String softSkillsInferred;

    @OneToOne
    @JoinColumn(name = "resume_id")
    @JsonManagedReference
    private Resume resume;
}
