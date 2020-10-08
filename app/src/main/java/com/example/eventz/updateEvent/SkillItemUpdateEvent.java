package com.example.eventz.updateEvent;

public class SkillItemUpdateEvent {
    private String skillName;

    public SkillItemUpdateEvent(String skillName) {
        this.skillName = skillName;
    }

    public String getSkillName() {
        return skillName;
    }
}
