package com.example.eventz.add_events;

public class SkillItemAddEvent {
    private String skillName;

    public SkillItemAddEvent(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillName() {
        return skillName;
    }
}
