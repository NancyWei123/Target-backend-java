package com.example.target.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserSettingsDTO {
    @JsonProperty("dark_mode")
    private Boolean darkMode;

    @JsonProperty("email_notifications")
    private Boolean emailNotifications;

    @JsonProperty("task_reminders")
    private Boolean taskReminders;
}