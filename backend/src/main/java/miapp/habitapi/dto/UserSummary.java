package miapp.habitapi.dto;

public class UserSummary {
    private Long id;
    private String name;

    public UserSummary(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
}
