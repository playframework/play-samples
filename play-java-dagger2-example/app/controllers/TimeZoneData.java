package controllers;

import play.data.validation.ValidationError;

import java.util.TimeZone;

import static play.data.validation.Constraints.*;

@Validate
public class TimeZoneData implements Validatable<ValidationError> {

    @Required
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

    @Override
    public ValidationError validate() {
        if (TimeZone.getTimeZone(timeZone) == null) {
            return new ValidationError("timeZone", "Invalid time zone");
        }
        return null;
    }

}
