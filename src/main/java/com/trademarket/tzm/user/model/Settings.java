package com.trademarket.tzm.user.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public class Settings {

    @Size(max=50, message = "Language must be at most 50 characters")
    private String language = "en";

    @Size(max=10, message = "Timezone must be at most 10 characters")
    private String timezone = "UCT";

    @Valid
    private Notifications notifications = new Notifications();

    public Settings() {}

    public Settings(String language, String timezone, Notifications notifications) {
        this.language = language;
        this.timezone = timezone;
        this.notifications = notifications;
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Notifications getNotifications() { return notifications; }

    public void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "language='" + language +
                ", timezone='" + timezone +
                ", notifications=" + notifications +
                '}';
    }

    public static class Notifications {

        private Boolean email, sms;

        public Notifications() {
            this.email = true;
            this.sms = false;
        }

        public boolean isEmail() { return email; }

        public void setEmail(boolean email) { this.email = email; }

        public boolean isSms() { return sms; }

        public void setSms(boolean sms) { this.sms = sms; }

        @Override
        public String toString() {
            return "Notifications{" +
                    "email=" + email +
                    ", sms=" + sms +
                    '}';
        }
    }
}
