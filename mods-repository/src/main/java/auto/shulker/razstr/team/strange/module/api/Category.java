package auto.shulker.razstr.team.strange.module.api;

public enum Category {
    Utilities("Утилиты"),
    Theme("Темы");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
