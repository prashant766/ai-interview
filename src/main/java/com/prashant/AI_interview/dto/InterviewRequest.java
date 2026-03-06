package com.prashant.AI_interview.dto;

public class InterviewRequest {

    private String technology;
    private String answer;

    public InterviewRequest() {
    }

    public InterviewRequest(String technology, String answer) {
        this.technology = technology;
        this.answer = answer;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}