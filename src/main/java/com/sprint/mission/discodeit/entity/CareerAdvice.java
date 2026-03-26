package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class CareerAdvice {
    private UUID id;
    private String title;
    private String content;
    private AdviceCategory adviceCategory;
    private JobField jobfield;
    private Long createdAt;
    private Long updatedAt;


    private static class AdviceCategory {
        private String Resume;
        private String Portfolio;
        private String Career_Change;
        private String Salary;
        private String Job_Search;
    }

    private static class JobField {
        private String Engineering;
        private String Product;
        private String Sales;
        private String Finance;
        private String Marketing;
        private String HR;
        private String Other;
    }

    public CareerAdvice(String title, String content, AdviceCategory adviceCategory, JobField jobfield) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.adviceCategory = adviceCategory;
        this.jobfield = jobfield;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {return id; }
    public String getTitle() {return title; }
    public String getContent() {return content; }
    public AdviceCategory getAdviceCategory() {return adviceCategory; }
    public JobField getJobfield() {return jobfield; }
    public Long getCreatedAt() {return createdAt; }
    public Long getUpdatedAt() {return updatedAt; }

    public void update(String title, String content, AdviceCategory adviceCategory, JobField jobfield) {
        this.title = title;
        this.content = content;
        this.adviceCategory = adviceCategory;
        this.jobfield = jobfield;
        updatedAt = System.currentTimeMillis();
        System.out.println(this.updatedAt);
    }

    @Override
    public String toString() {
        return "CareerAdvice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", adviceCategory=" + adviceCategory +
                ", jobfield=" + jobfield +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
