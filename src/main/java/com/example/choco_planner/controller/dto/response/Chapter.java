package com.example.choco_planner.controller.dto.response;

import java.util.List;

// 대제목 클래스
public class Chapter {
    private String title;
    private List<Section> sections;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "title='" + title + '\'' +
                ", sections=" + sections +
                '}';
    }
}
