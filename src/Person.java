public abstract class Person {

    private int id;
    private String firstName;
    private String lastName;
    private char gender;
    private int age;
    private String phoneNumber;
    private String email;

    public Person(int id, String firstName, String lastName, char gender, int age, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public char getGender() { return gender; }
    public int getAge() { return age; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(int age) { this.age = age; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getDetails() {
        return "ID: " + id + ", Name: " + getFullName() + ", Phone: " + phoneNumber + ", Email: " + email;
    }
}
