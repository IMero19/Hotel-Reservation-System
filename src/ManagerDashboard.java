import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class ManagerDashboard extends JFrame {

    private static final Color NAVY = new Color(26, 60, 110);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color LIGHT_BG = new Color(245, 247, 250);

    private final UserAccount currentUser;
    private final Hotel hotel;
    private Manager currentManager;
    private Department currentDept;

    private DefaultTableModel deptEmpModel;
    private DefaultTableModel allEmpModel;
    private DefaultTableModel roomModel;
    private DefaultTableModel reservationModel;

    public ManagerDashboard(UserAccount user) {
        this.currentUser = user;
        this.hotel = HotelApp.getHotel();
        resolveManager();
        setTitle("Hotel Management System - Manager Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
        buildUI();
    }

    private void resolveManager() {
        int empId = currentUser.getLinkedPersonId();
        Employee emp = hotel.searchEmployee(empId);
        if (emp instanceof Manager) {
            currentManager = (Manager) emp;
            currentDept = currentManager.getManagedDepartment();
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("  My Department  ", buildDeptTab());
        tabs.addTab("  Staff Management  ", buildStaffTab());
        tabs.addTab("  Room Management  ", buildRoomTab());
        tabs.addTab("  All Reservations  ", buildReservationTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(NAVY);
        h.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        String mgrName = currentManager != null ? currentManager.getFullName() : currentUser.getUsername();
        JLabel title = new JLabel("Manager Dashboard — " + mgrName);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel welcome = new JLabel("Logged in as: " + currentUser.getUsername());
        welcome.setForeground(GOLD);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = dangerBtn("Logout");
        logoutBtn.addActionListener(e -> logout());

        right.add(welcome);
        right.add(logoutBtn);
        h.add(title, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ======================================================
    // DEPARTMENT TAB
    // ======================================================
    private JPanel buildDeptTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Info card at top
        JPanel infoCard = new JPanel(new GridLayout(1, 3, 10, 0));
        infoCard.setBackground(NAVY);
        infoCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        infoCard.setPreferredSize(new Dimension(0, 65));

        String deptName = currentDept != null ? currentDept.getName() : "No Department Assigned";
        String empCount = currentDept != null ? String.valueOf(currentDept.getEmployees().size()) : "—";
        String mgrName  = currentManager != null ? currentManager.getFullName() : "—";

        infoCard.add(infoLabel("Department", deptName));
        infoCard.add(infoLabel("Manager", mgrName));
        infoCard.add(infoLabel("Employees", empCount));
        panel.add(infoCard, BorderLayout.NORTH);

        // Employee table
        String[] cols = {"ID", "Full Name", "Position", "Phone", "Email", "Salary"};
        deptEmpModel = tableModel(cols);
        JTable table = buildTable(deptEmpModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshDeptEmployees();

        // Buttons
        JPanel btns = btnPanel();
        JButton createDept  = navyBtn("Create / Set Department");
        JButton addEmp      = navyBtn("Add Employee to Dept");
        JButton removeEmp   = navyBtn("Remove from Dept");
        JButton refresh     = greenBtn("Refresh");

        createDept.addActionListener(e -> createDepartment());
        addEmp.addActionListener(e -> addEmpToDept());
        removeEmp.addActionListener(e -> removeEmpFromDept(table));
        refresh.addActionListener(e -> refreshDeptEmployees());

        btns.add(createDept); btns.add(addEmp); btns.add(removeEmp); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel infoLabel(String title, String value) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title.toUpperCase());
        t.setForeground(GOLD);
        t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(t); p.add(v);
        return p;
    }

    private void refreshDeptEmployees() {
        deptEmpModel.setRowCount(0);
        if (currentDept == null) return;
        for (Employee e : currentDept.getEmployees()) {
            deptEmpModel.addRow(new Object[]{
                e.getId(), e.getFullName(), e.getPosition(),
                e.getPhoneNumber(), e.getEmail(),
                String.format("$%.2f", e.getSalary())
            });
        }
    }

    private void createDepartment() {
        JTextField nameField = new JTextField(currentDept != null ? currentDept.getName() : "");
        JPanel form = new JPanel(new GridLayout(1, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Department Name:")); form.add(nameField);

        if (JOptionPane.showConfirmDialog(this, form, "Set Department Name",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        if (name.isEmpty()) { warn("Department name cannot be empty."); return; }

        if (currentDept == null) {
            int deptId = hotel.getDepartments().size() + 1;
            currentDept = new Department(deptId, name);
            hotel.addDepartment(currentDept);
        }
        if (currentManager != null) {
            currentManager.setManagedDepartment(currentDept);
            currentDept.setManager(currentManager);
        }
        refreshDeptEmployees();
        JOptionPane.showMessageDialog(this, "Department '" + name + "' is set.", "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addEmpToDept() {
        if (currentDept == null) { warn("Create a department first."); return; }
        String input = JOptionPane.showInputDialog(this, "Enter Employee ID to add to department:");
        if (input == null || input.trim().isEmpty()) return;
        try {
            int id = Integer.parseInt(input.trim());
            Employee emp = hotel.searchEmployee(id);
            if (emp == null) { warn("Employee ID " + id + " not found."); return; }
            currentDept.addEmployee(emp);
            refreshDeptEmployees();
        } catch (NumberFormatException ex) {
            warn("Please enter a valid numeric ID.");
        }
    }

    private void removeEmpFromDept(JTable table) {
        if (currentDept == null) { warn("No department assigned."); return; }
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select an employee first."); return; }
        int empId = (int) deptEmpModel.getValueAt(row, 0);
        String name = (String) deptEmpModel.getValueAt(row, 1);
        if (confirm("Remove " + name + " from " + currentDept.getName() + "?")) {
            Employee emp = hotel.searchEmployee(empId);
            if (emp != null) currentDept.removeEmployee(emp);
            refreshDeptEmployees();
        }
    }

    // ======================================================
    // STAFF MANAGEMENT TAB
    // ======================================================
    private JPanel buildStaffTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"ID", "Full Name", "Position", "Employee Code", "Phone", "Email", "Salary"};
        allEmpModel = tableModel(cols);
        JTable table = buildTable(allEmpModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshAllStaff();

        JPanel btns = btnPanel();
        JButton update  = navyBtn("Update Staff Data");
        JButton refresh = greenBtn("Refresh");

        update.addActionListener(e -> updateStaff(table));
        refresh.addActionListener(e -> refreshAllStaff());

        btns.add(update); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshAllStaff() {
        allEmpModel.setRowCount(0);
        for (Employee e : hotel.getEmployees()) {
            allEmpModel.addRow(new Object[]{
                e.getId(), e.getFullName(), e.getPosition(),
                e.getEmployeeCode(), e.getPhoneNumber(), e.getEmail(),
                String.format("$%.2f", e.getSalary())
            });
        }
    }

    private void updateStaff(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a staff member first."); return; }
        int empId = (int) allEmpModel.getValueAt(row, 0);
        Employee emp = hotel.searchEmployee(empId);
        if (emp == null) return;

        JTextField phone    = new JTextField(emp.getPhoneNumber());
        JTextField position = new JTextField(emp.getPosition());
        JTextField salary   = new JTextField(String.valueOf(emp.getSalary()));
        JPasswordField newPass = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Position:")); form.add(position);
        form.add(new JLabel("Salary:")); form.add(salary);
        form.add(new JLabel("New Password (blank = keep):")); form.add(newPass);

        if (JOptionPane.showConfirmDialog(this, form, "Update: " + emp.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            double sal = Double.parseDouble(salary.getText().trim());
            hotel.updateEmployee(empId, phone.getText().trim(), position.getText().trim(), sal);

            String pwd = new String(newPass.getPassword());
            if (!pwd.isEmpty()) {
                for (UserAccount ua : HotelApp.getUserAccounts())
                    if (ua.getLinkedPersonId() == empId) { ua.setPassword(pwd); break; }
            }
            HotelApp.save();
            refreshAllStaff();
            JOptionPane.showMessageDialog(this, "Staff data updated.", "Done", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            warn("Salary must be a valid number.");
        }
    }

    // ======================================================
    // ROOM MANAGEMENT TAB
    // ======================================================
    private JPanel buildRoomTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"Room No", "Type", "Price/Night", "View", "Floor", "Status"};
        roomModel = tableModel(cols);
        JTable table = buildTable(roomModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshRooms();

        JPanel btns = btnPanel();
        JButton reset   = new JButton("Reset Availability");
        styleBtn(reset, new Color(160, 100, 0));
        JButton refresh = greenBtn("Refresh");

        reset.addActionListener(e -> resetRoom(table));
        refresh.addActionListener(e -> refreshRooms());

        btns.add(reset); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRooms() {
        roomModel.setRowCount(0);
        for (Room r : hotel.getRooms()) {
            roomModel.addRow(new Object[]{
                r.getRoomCode(), r.getRoomType(),
                String.format("$%.2f", r.getPricePerNight()),
                r.getRoomView(), r.getFloorNo(),
                r.isAvailable() ? "Available" : "Booked"
            });
        }
    }

    private void resetRoom(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a room first."); return; }
        int no = (int) roomModel.getValueAt(row, 0);
        if ("Available".equals(roomModel.getValueAt(row, 5))) {
            JOptionPane.showMessageDialog(this, "Room is already available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (confirm("Reset Room " + no + " to Available?")) {
            hotel.resetRoomAvailability(no);
            HotelApp.save();
            refreshRooms();
        }
    }

    // ======================================================
    // RESERVATIONS TAB (read-only view)
    // ======================================================
    private JPanel buildReservationTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"Res. ID", "Guest", "Check-In", "Check-Out", "Days", "Total Cost", "Status", "Payment"};
        reservationModel = tableModel(cols);
        JTable table = buildTable(reservationModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshReservations();

        JPanel btns = btnPanel();
        JButton view    = new JButton("View Details");
        styleBtn(view, new Color(0, 100, 180));
        JButton refresh = greenBtn("Refresh");

        view.addActionListener(e -> viewDetails(table));
        refresh.addActionListener(e -> refreshReservations());

        btns.add(view); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshReservations() {
        reservationModel.setRowCount(0);
        for (Reservation r : hotel.getReservations()) {
            reservationModel.addRow(new Object[]{
                r.getReservationId(), r.getGuest().getFullName(),
                r.getCheckInDate(), r.getCheckOutDate(), r.getNumberOfDays(),
                String.format("$%.2f", r.getTotalCost()), r.getStatus(),
                r.getPayment().getPaymentStatus()
            });
        }
    }

    private void viewDetails(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a reservation first."); return; }
        int rid = (int) reservationModel.getValueAt(row, 0);
        Reservation res = hotel.searchReservation(rid);
        if (res == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Reservation ID : ").append(res.getReservationId()).append("\n");
        sb.append("Guest          : ").append(res.getGuest().getFullName()).append("\n");
        sb.append("Check-In       : ").append(res.getCheckInDate()).append("\n");
        sb.append("Check-Out      : ").append(res.getCheckOutDate()).append("\n");
        sb.append("Days           : ").append(res.getNumberOfDays()).append("\n");
        sb.append("Status         : ").append(res.getStatus()).append("\n");
        sb.append("Remarks        : ").append(res.getSpecialRemarks()).append("\n");
        sb.append("Rooms:\n");
        for (Room r : res.getReservationRooms())
            sb.append("  Room ").append(r.getRoomCode()).append(" (").append(r.getRoomType()).append(")\n");
        sb.append("Total          : $").append(String.format("%.2f", res.getTotalCost()));

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(400, 280));
        JOptionPane.showMessageDialog(this, sp, "Reservation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ======================================================
    // HELPERS
    // ======================================================
    private JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionBackground(new Color(210, 225, 255));
        t.setGridColor(new Color(220, 220, 228));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = t.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(NAVY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        return t;
    }

    private DefaultTableModel tableModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private JPanel btnPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p.setBackground(LIGHT_BG);
        return p;
    }

    private JButton navyBtn(String text) { JButton b = new JButton(text); styleBtn(b, NAVY); return b; }
    private JButton greenBtn(String text) { JButton b = new JButton(text); styleBtn(b, new Color(40, 140, 40)); return b; }
    private JButton dangerBtn(String text) { JButton b = new JButton(text); styleBtn(b, new Color(190, 40, 40)); return b; }

    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(170, 34));
    }

    private void warn(String msg) { JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.WARNING_MESSAGE); }
    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void logout() {
        HotelApp.save();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
