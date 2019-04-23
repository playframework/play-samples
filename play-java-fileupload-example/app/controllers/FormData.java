package controllers;

public class FormData {
    private String name;

    public FormData() {}

    public FormData(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
