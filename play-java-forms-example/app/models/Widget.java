package models;

/**
 * Presentation object used for displaying data in a template.
 *
 * Note that it's a good practice to keep the presentation DTO,
 * which are used for reads, distinct from the form processing DTO,
 * which are used for writes.
 */
public class Widget {
    public String name;
    public int price;
    public String description;

    public Widget(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
