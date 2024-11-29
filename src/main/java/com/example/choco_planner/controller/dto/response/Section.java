package com.example.choco_planner.controller.dto.response;

import java.util.List;

// 중제목 클래스
public class Section {
    private String subtitle;
    private List<String> contents;

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "Section{" +
                "subtitle='" + subtitle + '\'' +
                ", contents=" + contents +
                '}';
    }
}
