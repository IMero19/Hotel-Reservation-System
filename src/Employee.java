public class Employee extends Person {
    private String employeeCode;
    private double salary;
    private String position;
    private Department department;

    public Employee(int id, String firstName, String lastName, String phoneNumber, String email,
                    String employeeCode, double salary, String position, char gender, int age) {
        super(id, firstName, lastName, gender, age, phoneNumber, email);
        this.employeeCode = employeeCode;
        this.salary = salary;
        this.position = position;
        this.department = null;
    }

    public String getEmployeeCode() { return employeeCode; }
    public double getSalary() { return salary; }
    public String getPosition() { return position; }
    public Department getDepartment() { return department; }

    public void setDepartment(Department department) { this.department = department; }
    public void updatePhoneNumber(String phoneNumber) { super.setPhoneNumber(phoneNumber); }
    public void updateSalary(double salary) { this.salary = salary; }
    public void updatePosition(String position) { this.position = position; }

    @Override
    public String getDetails() {
        return "Code: " + employeeCode
                + " | Name: " + getFirstName() + " " + getLastName()
                + " | Position: " + position
                + " | Phone: " + getPhoneNumber()
                + " | Salary: $" + String.format("%.2f", salary);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;
        Employee e = (Employee) obj;
        return this.getId() == e.getId();
    }

    @Override
    public int hashCode() { return Integer.hashCode(getId()); }
}
