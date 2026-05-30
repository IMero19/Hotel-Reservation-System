import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class AdminDashboard extends JFrame {

    private static final Color NAVY = new Color(26, 60, 110);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color LIGHT_BG = new Color(245, 247, 250);

    private final UserAccount currentUser;
    private final Hotel hotel;

    private DefaultTableModel staffModel;
    private DefaultTableModel roomModel;
    private DefaultTableModel guestModel;
    private DefaultTableModel reservationModel;

    public AdminDashboard(UserAccount user) {
        this.currentUser = user;
        this.hotel = HotelApp.getHotel();
        setTitle("Hotel Management System - Admin Dashboard");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 550));
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("  Staff Management  ", buildStaffTab());
        tabs.addTab("  Room Management  ", buildRoomTab());
        tabs.addTab("  Guest Management  ", buildGuestTab());
        tabs.addTab("  Reservations  ", buildReservationTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(NAVY);
        h.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel(hotel.getHotelName() + " — Admin Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel welcome = new JLabel("Logged in as: " + currentUser.getUsername());
        welcome.setForeground(GOLD);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton logoutBtn = dangerButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        right.add(welcome);
        right.add(logoutBtn);
        h.add(title, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ======================================================
    // STAFF TAB
    // ======================================================
    private JPanel buildStaffTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"ID", "Full Name", "Position", "Employee Code", "Phone", "Email", "Salary"};
        staffModel = tableModel(cols);
        JTable table = buildTable(staffModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshStaff();

        JPanel btns = buttonPanel();
        JButton add = navyBtn("Add Staff");
        JButton update = navyBtn("Update Staff");
        JButton remove = navyBtn("Remove Staff");
        JButton refresh = greenBtn("Refresh");

        add.addActionListener(e -> addStaff());
        update.addActionListener(e -> updateStaff(table));
        remove.addActionListener(e -> removeStaff(table));
        refresh.addActionListener(e -> refreshStaff());

        btns.add(add); btns.add(update); btns.add(remove); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStaff() {
        staffModel.setRowCount(0);
        for (Employee e : hotel.getEmployees()) {
            staffModel.addRow(new Object[]{
                e.getId(), e.getFullName(), e.getPosition(),
                e.getEmployeeCode(), e.getPhoneNumber(), e.getEmail(),
                String.format("$%.2f", e.getSalary())
            });
        }
    }

    private void addStaff() {
        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField phone = new JTextField();
        JTextField age = new JTextField();
        JTextField salary = new JTextField();
        String[] posOptions = {"Receptionist", "Manager", "Housekeeping", "Security", "Maintenance", "Concierge"};
        JComboBox<String> posBox = new JComboBox<>(posOptions);
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("First Name:")); form.add(firstName);
        form.add(new JLabel("Last Name:")); form.add(lastName);
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Age:")); form.add(age);
        form.add(new JLabel("Position:")); form.add(posBox);
        form.add(new JLabel("Salary:")); form.add(salary);
        form.add(new JLabel("Login Username:")); form.add(username);
        form.add(new JLabel("Login Password:")); form.add(password);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Staff Member",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            String fn = firstName.getText().trim();
            String ln = lastName.getText().trim();
            String ph = phone.getText().trim();
            int ageVal = Integer.parseInt(age.getText().trim());
            double salVal = Double.parseDouble(salary.getText().trim());
            String pos = (String) posBox.getSelectedItem();
            String uname = username.getText().trim();
            String pwd = new String(password.getPassword());

            if (fn.isEmpty() || ln.isEmpty() || uname.isEmpty() || pwd.isEmpty())
                throw new InvalidInputException("All fields are required.");

            for (UserAccount ua : HotelApp.getUserAccounts())
                if (ua.getUsername().equalsIgnoreCase(uname))
                    throw new InvalidInputException("Username '" + uname + "' is already taken.");

            int empId = HotelApp.getNextEmployeeId();
            String code = pos.substring(0, Math.min(3, pos.length())).toUpperCase() + "-" + empId;
            String role = "Manager".equals(pos) ? "MANAGER" : "RECEPTIONIST";

            Employee emp;
            if ("Manager".equals(pos)) {
                emp = new Manager(empId, fn, ln, ph, fn.toLowerCase() + "@hotel.com", code, salVal, ageVal, 'M');
                hotel.addManager((Manager) emp);
            } else {
                emp = new Employee(empId, fn, ln, ph, fn.toLowerCase() + "@hotel.com", code, salVal, pos, 'M', ageVal);
                hotel.addEmployee(emp);
            }

            int uid = HotelApp.getNextUserId();
            HotelApp.getUserAccounts().add(new UserAccount(uid, uname, pwd, role, empId));
            HotelApp.save();
            refreshStaff();
            JOptionPane.showMessageDialog(this, "Staff member added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age and Salary must be valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaff(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a staff member first."); return; }
        int empId = (int) staffModel.getValueAt(row, 0);
        Employee emp = hotel.searchEmployee(empId);
        if (emp == null) return;

        JTextField phone = new JTextField(emp.getPhoneNumber());
        JTextField position = new JTextField(emp.getPosition());
        JTextField salary = new JTextField(String.valueOf(emp.getSalary()));
        JPasswordField newPass = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Position:")); form.add(position);
        form.add(new JLabel("Salary:")); form.add(salary);
        form.add(new JLabel("New Password (blank = no change):")); form.add(newPass);

        if (JOptionPane.showConfirmDialog(this, form, "Update: " + emp.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            double sal = Double.parseDouble(salary.getText().trim());
            emp.setPhoneNumber(phone.getText().trim());
            emp.updatePosition(position.getText().trim());
            emp.updateSalary(sal);

            String pwd = new String(newPass.getPassword());
            if (!pwd.isEmpty()) {
                for (UserAccount ua : HotelApp.getUserAccounts())
                    if (ua.getLinkedPersonId() == empId) { ua.setPassword(pwd); break; }
            }
            HotelApp.save();
            refreshStaff();
            JOptionPane.showMessageDialog(this, "Staff updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeStaff(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a staff member first."); return; }
        int empId = (int) staffModel.getValueAt(row, 0);
        String name = (String) staffModel.getValueAt(row, 1);
        if (confirm("Remove staff member: " + name + "?")) {
            Employee emp = hotel.searchEmployee(empId);
            if (emp != null) {
                hotel.removeEmployee(emp);
                if (emp instanceof Manager) hotel.removeManager((Manager) emp);
                HotelApp.getUserAccounts().removeIf(ua -> ua.getLinkedPersonId() == empId);
            }
            HotelApp.save();
            refreshStaff();
            JOptionPane.showMessageDialog(this, "Staff member removed.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ======================================================
    // ROOM TAB
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

        JPanel btns = buttonPanel();
        JButton add = navyBtn("Add Room");
        JButton remove = navyBtn("Remove Room");
        JButton reset = new JButton("Reset Availability");
        styleBtn(reset, new Color(160, 100, 0));
        JButton refresh = greenBtn("Refresh");

        add.addActionListener(e -> addRoom());
        remove.addActionListener(e -> removeRoom(table));
        reset.addActionListener(e -> resetRoomAvailability(table));
        refresh.addActionListener(e -> refreshRooms());

        btns.add(add); btns.add(remove); btns.add(reset); btns.add(refresh);
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

    private void addRoom() {
        JTextField roomNo = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe"});
        JTextField price = new JTextField();
        JComboBox<String> viewBox = new JComboBox<>(new String[]{"City", "Garden", "Ocean", "Pool", "Mountain"});
        JTextField floor = new JTextField();

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Room Number:")); form.add(roomNo);
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Price Per Night ($):")); form.add(price);
        form.add(new JLabel("View:")); form.add(viewBox);
        form.add(new JLabel("Floor:")); form.add(floor);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Room",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            int no = Integer.parseInt(roomNo.getText().trim());
            double pr = Double.parseDouble(price.getText().trim());
            int fl = Integer.parseInt(floor.getText().trim());

            if (hotel.searchRoom(no) != null)
                throw new InvalidInputException("Room " + no + " already exists.");

            hotel.addRoom(new Room(no, (String) typeBox.getSelectedItem(), pr, (String) viewBox.getSelectedItem(), fl));
            HotelApp.save();
            refreshRooms();
            JOptionPane.showMessageDialog(this, "Room added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Room number, price, and floor must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeRoom(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a room first."); return; }
        int no = (int) roomModel.getValueAt(row, 0);
        String status = (String) roomModel.getValueAt(row, 5);
        if ("Booked".equals(status)) {
            JOptionPane.showMessageDialog(this, "Cannot remove a currently booked room.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (confirm("Remove Room " + no + "?")) {
            hotel.removeRoom(hotel.searchRoom(no));
            HotelApp.save();
            refreshRooms();
        }
    }

    private void resetRoomAvailability(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a room first."); return; }
        int no = (int) roomModel.getValueAt(row, 0);
        String status = (String) roomModel.getValueAt(row, 5);
        if ("Available".equals(status)) {
            JOptionPane.showMessageDialog(this, "Room is already available.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (confirm("Reset Room " + no + " to Available?")) {
            hotel.resetRoomAvailability(no);
            HotelApp.save();
            refreshRooms();
            JOptionPane.showMessageDialog(this, "Room " + no + " is now Available.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ======================================================
    // GUEST TAB
    // ======================================================
    private JPanel buildGuestTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"Guest ID", "Full Name", "Age", "Gender", "Phone", "Email"};
        guestModel = tableModel(cols);
        JTable table = buildTable(guestModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshGuests();

        JPanel btns = buttonPanel();
        JButton update = navyBtn("Update Account");
        JButton remove = navyBtn("Remove Guest");
        JButton refresh = greenBtn("Refresh");

        update.addActionListener(e -> updateGuest(table));
        remove.addActionListener(e -> removeGuest(table));
        refresh.addActionListener(e -> refreshGuests());

        btns.add(update); btns.add(remove); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshGuests() {
        guestModel.setRowCount(0);
        for (Guest g : hotel.getGuests()) {
            guestModel.addRow(new Object[]{g.getId(), g.getFullName(), g.getAge(), g.getGender(), g.getPhoneNumber(), g.getEmail()});
        }
    }

    private void updateGuest(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a guest first."); return; }
        int gid = (int) guestModel.getValueAt(row, 0);
        Guest g = hotel.searchGuest(gid);
        if (g == null) return;

        JTextField phone = new JTextField(g.getPhoneNumber());
        JTextField email = new JTextField(g.getEmail());

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(new JLabel("Email:")); form.add(email);

        if (JOptionPane.showConfirmDialog(this, form, "Update Guest: " + g.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            hotel.updateGuest(gid, phone.getText().trim(), email.getText().trim());
            HotelApp.save();
            refreshGuests();
            JOptionPane.showMessageDialog(this, "Guest account updated.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeGuest(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a guest first."); return; }
        int gid = (int) guestModel.getValueAt(row, 0);
        String name = (String) guestModel.getValueAt(row, 1);
        if (confirm("Remove guest: " + name + "?")) {
            Guest g = hotel.searchGuest(gid);
            if (g != null) hotel.removeGuest(g);
            HotelApp.save();
            refreshGuests();
        }
    }

    // ======================================================
    // RESERVATIONS TAB
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

        JPanel btns = buttonPanel();
        JButton view = new JButton("View Details");
        styleBtn(view, new Color(0, 100, 180));
        JButton cancel = dangerButton("Cancel Reservation");
        JButton refresh = greenBtn("Refresh");

        view.addActionListener(e -> viewReservationDetails(table));
        cancel.addActionListener(e -> cancelReservation(table));
        refresh.addActionListener(e -> refreshReservations());

        btns.add(view); btns.add(cancel); btns.add(refresh);
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

    private void viewReservationDetails(JTable table) {
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
        sb.append("Duration       : ").append(res.getNumberOfDays()).append(" day(s)\n");
        sb.append("Status         : ").append(res.getStatus()).append("\n");
        sb.append("Remarks        : ").append(res.getSpecialRemarks()).append("\n");
        sb.append("Rooms:\n");
        for (Room r : res.getReservationRooms())
            sb.append("  Room ").append(r.getRoomCode()).append(" (").append(r.getRoomType()).append(") - $").append(String.format("%.2f", r.getPricePerNight())).append("/night\n");
        sb.append("Total Cost     : $").append(String.format("%.2f", res.getTotalCost())).append("\n");
        sb.append("Payment Method : ").append(res.getPayment().getPaymentMethod()).append("\n");
        sb.append("Payment Status : ").append(res.getPayment().getPaymentStatus());

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(420, 300));
        JOptionPane.showMessageDialog(this, sp, "Reservation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelReservation(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a reservation first."); return; }
        int rid = (int) reservationModel.getValueAt(row, 0);
        Reservation res = hotel.searchReservation(rid);
        if (res == null) return;

        if ("Cancelled".equals(res.getStatus())) {
            JOptionPane.showMessageDialog(this, "Reservation is already cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (confirm("Cancel reservation #" + rid + " for " + res.getGuest().getFullName() + "?")) {
            hotel.cancelReservation(res);
            HotelApp.save();
            refreshReservations();
            JOptionPane.showMessageDialog(this, "Reservation cancelled and rooms freed.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
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

    private JPanel buttonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        p.setBackground(LIGHT_BG);
        return p;
    }

    private JButton navyBtn(String text) {
        JButton b = new JButton(text);
        styleBtn(b, NAVY);
        return b;
    }

    private JButton greenBtn(String text) {
        JButton b = new JButton(text);
        styleBtn(b, new Color(40, 140, 40));
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        styleBtn(b, new Color(190, 40, 40));
        return b;
    }

    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(160, 34));
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void logout() {
        HotelApp.save();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
