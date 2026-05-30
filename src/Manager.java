public class Manager extends Employee {

    private Department managedDepartment;

    public Manager(int id, String firstName, String lastName, String phoneNumber, String email,
                   String employeeCode, double salary, int age, char gender) {
        super(id, firstName, lastName, phoneNumber, email, employeeCode, salary, "Manager", gender, age);
        this.managedDepartment = null;
    }

    public Department getManagedDepartment() { return managedDepartment; }

    public void setManagedDepartment(Department dept) {
        this.managedDepartment = dept;
        if (dept != null) {
            dept.setManager(this);
        }
    }

    public void addEmployeeToDepartment(Employee employee) {
        if (managedDepartment == null) {
            System.out.println("This manager is not assigned to any department.");
            return;
        }
        if (employee != null) managedDepartment.addEmployee(employee);
    }

    public void removeEmployeeFromDepartment(Employee employee) {
        if (managedDepartment == null) {
            System.out.println("This manager is not assigned to any department.");
            return;
        }
        if (employee != null) managedDepartment.removeEmployee(employee);
    }

    public void showDepartmentEmployees() {
        if (managedDepartment == null) {
            System.out.println("This manager has no assigned department.");
            return;
        }
        managedDepartment.showEmployees();
    }

    @Override
    public String getDetails() {
        String deptName = (managedDepartment != null) ? managedDepartment.getName() : "No Department";
        return "Manager Code: " + getEmployeeCode()
                + " | Name: " + getFirstName() + " " + getLastName()
                + " | Salary: $" + String.format("%.2f", getSalary())
                + " | Department: " + deptName;
    }
}
