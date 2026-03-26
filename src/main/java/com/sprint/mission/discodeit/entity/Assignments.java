package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Assignments {
    private UUID id;
    private String title;
    private String content;
    private String description;
    private AssignmentCategory assignmentCategory;
    private Long dueDate;
    private Long createdAt;
    private Long updatedAt;


    private static class AssignmentCategory {
        private String CodeIt;
        private String SprintMission;
        private String Project;
    }


    public Assignments(String title, String content, String description, AssignmentCategory assignmentCategory) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.description = description;
        this.assignmentCategory = assignmentCategory;
        this.dueDate = System.currentTimeMillis();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();

    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public AssignmentCategory getAssignmentCategory() {
        return assignmentCategory;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }


    public void update(String title, String content, String description, AssignmentCategory assignmentCategory) {
        this.title = title;
        this.content = content;
        this.description = description;
        this.assignmentCategory = assignmentCategory;
        this.dueDate = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
        System.out.println(this.updatedAt);

    }


    @Override
    public String toString() {
        return "Assignments{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", assignmentCategory=" + assignmentCategory +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';

    }

}





