package nz.ac.canterbury.seng302.shared.enums;

public enum Roles {
    STUDENT("Student"),
    TEACHER("Teacher"),
    COURSE_ADMINISTRATOR("Course Administrator");

    private final String role;

    Roles(String label) {
        this.role = label;
    }

    public String getRole() {
        return role;
    }
}
