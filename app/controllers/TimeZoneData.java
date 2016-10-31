package controllers;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class TimeZoneData {

    @Constraints.Required
    private String timeZone;

    public TimeZoneData() {
        super();
    }

    public TimeZoneData(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (TimeZone.getTimeZone(timeZone) == null) {
            errors.add(new ValidationError("timeZone", "Invalid time zone"));
        }
        return errors.isEmpty() ? null : errors;
    }

}
