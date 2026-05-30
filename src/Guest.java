public class Guest extends Person {

    public Guest(String firstName, String lastName, String email,
                 int guestId, int age, char gender, String phone) {
        super(guestId, firstName, lastName, gender, age, phone, email);
    }

    @Override
    public String getDetails() {
        return "Guest ID: " + getId()
                + " | Name: " + getFirstName() + " " + getLastName()
                + " | Gender: " + getGender()
                + " | Age: " + getAge()
                + " | Phone: " + getPhoneNumber()
                + " | Email: " + getEmail();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Guest)) return false;
        Guest g = (Guest) obj;
        return this.getId() == g.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}
