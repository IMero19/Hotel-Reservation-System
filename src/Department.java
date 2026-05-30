import java.util.ArrayList;

public class Department {
    private int departmentId;
    private String name;
    private ArrayList<Employee> employees;
    private Manager manager;

    public Department(int departmentId, String name) {
        this.departmentId = departmentId;
        this.name = name;
        this.employees = new ArrayList<>();
        this.manager = null;
    }

    public int getDepartmentId() { return departmentId; }
    public String getName() { return name; }
    public ArrayList<Employee> getEmployees() { return employees; }
    public Manager getManager() { return manager; }

    public void setManager(Manager manager) {
        this.manager = manager;
        if (manager != null) {
            manager.setDepartment(this);
            if (!employees.contains(manager)) employees.add(manager);
        }
    }

    public void addEmployee(Employee employee) {
        if (employee == null) return;
        if (employees.contains(employee)) {
            System.out.println(employee.getFullName() + " is already in this department.");
            return;
        }
        employees.add(employee);
        employee.setDepartment(this);
        System.out.println(employee.getFullName() + " added to " + name + " department.");
    }

    public void removeEmployee(Employee employee) {
        if (employee == null) return;
        if (employee.equals(manager)) {
            System.out.println("Cannot remove the department manager directly.");
            return;
        }
        if (employees.remove(employee)) {
            employee.setDepartment(null);
            System.out.println(employee.getFullName() + " removed from " + name + " department.");
        } else {
            System.out.println(employee.getFullName() + " not found in " + name + " department.");
        }
    }

    public void showEmployees() {
        System.out.println("Department: " + name);
        System.out.println("Manager: " + (manager != null ? manager.getFullName() : "None"));
        System.out.println("Employees:");
        for (Employee emp : employees) {
            System.out.println("  - " + emp.getDetails());
        }
    }

    public void displayDepartmentDetails() {
        System.out.println("Department ID: " + departmentId + " | Name: " + name
                + " | Manager: " + (manager != null ? manager.getFullName() : "None")
                + " | Employees: " + employees.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Department)) return false;
        Department d = (Department) obj;
        return this.departmentId == d.departmentId;
    }

    @Override
    public int hashCode() { return Integer.hashCode(departmentId); }
}
