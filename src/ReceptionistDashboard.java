import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class ReceptionistDashboard extends JFrame {

    private static final Color NAVY = new Color(26, 60, 110);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color LIGHT_BG = new Color(245, 247, 250);

    private final UserAccount currentUser;
    private final Hotel hotel;

    private DefaultTableModel guestModel;
    private DefaultTableModel reservationModel;
    private DefaultTableModel roomServiceModel;

    public ReceptionistDashboard(UserAccount user) {
        this.currentUser = user;
        this.hotel = HotelApp.getHotel();
        setTitle("Hotel Management System - Receptionist Dashboard");
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
        tabs.addTab("  Guest Management  ", buildGuestTab());
        tabs.addTab("  Reservations  ", buildReservationTab());
        tabs.addTab("  Room Services  ", buildRoomServiceTab());
        tabs.addTab("  Billing & Payment  ", buildBillingTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(NAVY);
        h.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel(hotel.getHotelName() + " — Receptionist");
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

        JPanel btns = btnPanel();
        JButton add     = navyBtn("Add Guest");
        JButton update  = navyBtn("Update Account");
        JButton refresh = greenBtn("Refresh");

        add.addActionListener(e -> addGuest());
        update.addActionListener(e -> updateGuest(table));
        refresh.addActionListener(e -> refreshGuests());

        btns.add(add); btns.add(update); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshGuests() {
        guestModel.setRowCount(0);
        for (Guest g : hotel.getGuests()) {
            guestModel.addRow(new Object[]{g.getId(), g.getFullName(), g.getAge(), g.getGender(), g.getPhoneNumber(), g.getEmail()});
        }
    }

    private void addGuest() {
        JTextField firstName = new JTextField();
        JTextField lastName  = new JTextField();
        JTextField email     = new JTextField();
        JTextField phone     = new JTextField();
        JTextField age       = new JTextField();
        JComboBox<Character> gender = new JComboBox<>(new Character[]{'M', 'F'});

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("First Name:"));  form.add(firstName);
        form.add(new JLabel("Last Name:"));   form.add(lastName);
        form.add(new JLabel("Email:"));       form.add(email);
        form.add(new JLabel("Phone:"));       form.add(phone);
        form.add(new JLabel("Age:"));         form.add(age);
        form.add(new JLabel("Gender:"));      form.add(gender);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Guest",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            String fn = firstName.getText().trim();
            String ln = lastName.getText().trim();
            String em = email.getText().trim();
            String ph = phone.getText().trim();
            int ageVal = Integer.parseInt(age.getText().trim());
            char gen = (char) gender.getSelectedItem();

            if (fn.isEmpty() || ln.isEmpty())
                throw new InvalidInputException("First name and last name are required.");

            int gid = HotelApp.getNextGuestId();
            hotel.addGuest(new Guest(fn, ln, em, gid, ageVal, gen, ph));
            HotelApp.save();
            refreshGuests();
            JOptionPane.showMessageDialog(this, "Guest added with ID: " + gid, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Account updated.", "Done", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ======================================================
    // RESERVATIONS TAB
    // ======================================================
    private JPanel buildReservationTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"Res. ID", "Guest", "Check-In", "Check-Out", "Days", "Total Cost", "Status", "Remarks"};
        reservationModel = tableModel(cols);
        JTable table = buildTable(reservationModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshReservations();

        JPanel btns = btnPanel();
        JButton newBook    = navyBtn("New Booking");
        JButton updateBook = new JButton("Update Booking");
        styleBtn(updateBook, new Color(0, 120, 180));
        JButton cancelBook = dangerBtn("Cancel Booking");
        JButton viewBtn    = new JButton("View Details");
        styleBtn(viewBtn, new Color(80, 80, 180));
        JButton refresh    = greenBtn("Refresh");

        newBook.addActionListener(e -> newBooking());
        updateBook.addActionListener(e -> updateBooking(table));
        cancelBook.addActionListener(e -> cancelBooking(table));
        viewBtn.addActionListener(e -> viewDetails(table));
        refresh.addActionListener(e -> refreshReservations());

        btns.add(newBook); btns.add(updateBook); btns.add(cancelBook); btns.add(viewBtn); btns.add(refresh);
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
                r.getSpecialRemarks()
            });
        }
    }

    private void newBooking() {
        if (hotel.getGuests().isEmpty()) {
            warn("No guests registered. Please add a guest first.");
            return;
        }

        // Step 1: guest selection
        String[] guestOptions = hotel.getGuests().stream()
                .map(g -> g.getId() + " - " + g.getFullName())
                .toArray(String[]::new);
        JComboBox<String> guestBox = new JComboBox<>(guestOptions);

        JTextField checkIn  = new JTextField("YYYY-MM-DD");
        JTextField checkOut = new JTextField("YYYY-MM-DD");
        JTextField days     = new JTextField("1");
        JTextArea  remarks  = new JTextArea(3, 20);
        remarks.setLineWrap(true);
        remarks.setWrapStyleWord(true);
        JComboBox<String> payMethod = new JComboBox<>(new String[]{"Card", "Cash", "Bank Transfer"});

        JPanel step1 = new JPanel(new GridLayout(6, 2, 8, 6));
        step1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        step1.add(new JLabel("Guest:"));        step1.add(guestBox);
        step1.add(new JLabel("Check-In Date:")); step1.add(checkIn);
        step1.add(new JLabel("Check-Out Date:")); step1.add(checkOut);
        step1.add(new JLabel("Number of Days:")); step1.add(days);
        step1.add(new JLabel("Payment Method:")); step1.add(payMethod);
        step1.add(new JLabel("Special Remarks:"));
        step1.add(new JScrollPane(remarks));

        if (JOptionPane.showConfirmDialog(this, step1, "New Booking — Step 1: Details",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            String guestStr = (String) guestBox.getSelectedItem();
            int guestId = Integer.parseInt(guestStr.split(" - ")[0].trim());
            Guest guest = hotel.searchGuest(guestId);
            if (guest == null) { warn("Guest not found."); return; }

            String ci = checkIn.getText().trim();
            String co = checkOut.getText().trim();
            int numDays = Integer.parseInt(days.getText().trim());
            String pm = (String) payMethod.getSelectedItem();
            String rem = remarks.getText().trim();

            if (numDays <= 0) throw new InvalidInputException("Number of days must be greater than 0.");
            if (ci.isEmpty() || co.isEmpty()) throw new InvalidInputException("Check-in and check-out dates are required.");

            // Step 2: room selection
            ArrayList<Room> available = new ArrayList<>();
            for (Room r : hotel.getRooms()) if (r.isAvailable()) available.add(r);

            if (available.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available rooms at the moment.", "No Rooms", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] roomOptions = available.stream()
                    .map(r -> r.getRoomCode() + " | " + r.getRoomType() + " | $" + String.format("%.2f", r.getPricePerNight()) + "/night | " + r.getRoomView() + " view")
                    .toArray(String[]::new);

            JList<String> roomList = new JList<>(roomOptions);
            roomList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            roomList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            JScrollPane roomScroll = new JScrollPane(roomList);
            roomScroll.setPreferredSize(new Dimension(450, 200));

            JPanel step2 = new JPanel(new BorderLayout(0, 8));
            step2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            step2.add(new JLabel("Select room(s) — hold Ctrl for multiple:"), BorderLayout.NORTH);
            step2.add(roomScroll, BorderLayout.CENTER);

            if (JOptionPane.showConfirmDialog(this, step2, "New Booking — Step 2: Select Rooms",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

            int[] selectedIndices = roomList.getSelectedIndices();
            if (selectedIndices.length == 0) { warn("Please select at least one room."); return; }

            int resId = HotelApp.getNextReservationId();
            Reservation res = new Reservation(resId, guest, numDays, ci, co, selectedIndices.length, pm, rem);

            for (int idx : selectedIndices) {
                Room room = available.get(idx);
                try {
                    if (!room.isAvailable()) throw new RoomNotAvailableException(room.getRoomCode());
                    res.addRoom(room);
                } catch (RoomNotAvailableException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Room Error", JOptionPane.WARNING_MESSAGE);
                }
            }

            res.confirmReservation();
            if ("Confirmed".equals(res.getStatus())) {
                hotel.addReservation(res);
                hotel.addPayment(res.getPayment());
                HotelApp.save();
                refreshReservations();
                JOptionPane.showMessageDialog(this,
                        "Booking confirmed!\nReservation ID: " + resId
                        + "\nTotal Cost: $" + String.format("%.2f", res.getTotalCost()),
                        "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } else {
                warn("Could not confirm booking. Check room availability.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for Guest ID and Days.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBooking(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a reservation to update."); return; }
        int rid = (int) reservationModel.getValueAt(row, 0);
        Reservation res = hotel.searchReservation(rid);
        if (res == null) return;

        if ("Cancelled".equals(res.getStatus())) {
            warn("Cannot update a cancelled reservation.");
            return;
        }

        // Show current booking info and let user change dates/days/remarks
        JTextField checkIn  = new JTextField(res.getCheckInDate());
        JTextField checkOut = new JTextField(res.getCheckOutDate());
        JTextField days     = new JTextField(String.valueOf(res.getNumberOfDays()));
        JTextArea  remarks  = new JTextArea(res.getSpecialRemarks(), 3, 20);
        remarks.setLineWrap(true);
        remarks.setWrapStyleWord(true);
        JComboBox<String> payMethod = new JComboBox<>(new String[]{"Card", "Cash", "Bank Transfer"});
        payMethod.setSelectedItem(res.getPayment().getPaymentMethod());

        JLabel infoLabel = new JLabel("Current rooms will be kept. Only dates/remarks are changed.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(100, 100, 120));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("New Check-In Date:")); form.add(checkIn);
        form.add(new JLabel("New Check-Out Date:")); form.add(checkOut);
        form.add(new JLabel("Number of Days:")); form.add(days);
        form.add(new JLabel("Payment Method:")); form.add(payMethod);
        form.add(new JLabel("Special Remarks:"));
        form.add(new JScrollPane(remarks));
        form.add(infoLabel); form.add(new JLabel(""));

        if (JOptionPane.showConfirmDialog(this, form, "Update Booking #" + rid,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        try {
            int numDays = Integer.parseInt(days.getText().trim());
            if (numDays <= 0) throw new InvalidInputException("Days must be greater than 0.");

            // Save old rooms before cancelling
            ArrayList<Room> oldRooms = new ArrayList<>(res.getReservationRooms());
            Guest guest = res.getGuest();
            int roomCount = res.getRoomCount();
            String pm = (String) payMethod.getSelectedItem();
            String rem = remarks.getText().trim();

            // Cancel and delete old reservation
            hotel.deleteReservation(res);

            // Create new reservation with same rooms and new dates
            int newId = HotelApp.getNextReservationId();
            Reservation newRes = new Reservation(newId, guest, numDays,
                    checkIn.getText().trim(), checkOut.getText().trim(), roomCount, pm, rem);

            for (Room r : oldRooms) {
                if (r.isAvailable()) {
                    newRes.addRoom(r);
                } else {
                    warn("Room " + r.getRoomCode() + " is no longer available. It was skipped.");
                }
            }

            if (newRes.getReservationRooms().isEmpty()) {
                warn("No rooms could be transferred to the updated booking. Please create a new booking.");
                HotelApp.save();
                refreshReservations();
                return;
            }

            newRes.confirmReservation();
            if ("Confirmed".equals(newRes.getStatus())) {
                hotel.addReservation(newRes);
                hotel.addPayment(newRes.getPayment());
                HotelApp.save();
                refreshReservations();
                JOptionPane.showMessageDialog(this,
                        "Booking updated!\nNew Reservation ID: " + newId
                        + "\nNew Total: $" + String.format("%.2f", newRes.getTotalCost()),
                        "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                warn("Could not confirm updated booking. Rooms may not be available.");
                HotelApp.save();
                refreshReservations();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Days must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Select a reservation first."); return; }
        int rid = (int) reservationModel.getValueAt(row, 0);
        Reservation res = hotel.searchReservation(rid);
        if (res == null) return;

        if ("Cancelled".equals(res.getStatus())) {
            JOptionPane.showMessageDialog(this, "Reservation is already cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (confirm("Cancel booking #" + rid + " for " + res.getGuest().getFullName() + "?\nRooms will be freed and payment refunded.")) {
            hotel.cancelReservation(res);
            HotelApp.save();
            refreshReservations();
            JOptionPane.showMessageDialog(this, "Booking cancelled. Rooms are now available.", "Done", JOptionPane.INFORMATION_MESSAGE);
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
        sb.append("\nRooms:\n");
        for (Room r : res.getReservationRooms())
            sb.append("  Room ").append(r.getRoomCode()).append(" (").append(r.getRoomType())
              .append(") - $").append(String.format("%.2f", r.getPricePerNight())).append("/night\n");
        sb.append("\nTotal Cost     : $").append(String.format("%.2f", res.getTotalCost())).append("\n");
        sb.append("Payment Method : ").append(res.getPayment().getPaymentMethod()).append("\n");
        sb.append("Payment Status : ").append(res.getPayment().getPaymentStatus());

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(430, 320));
        JOptionPane.showMessageDialog(this, sp, "Reservation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ======================================================
    // ROOM SERVICES TAB
    // ======================================================
    private JPanel buildRoomServiceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Current service orders table
        String[] cols = {"Room No", "Type", "Service Name", "Service Cost"};
        roomServiceModel = tableModel(cols);
        JTable table = buildTable(roomServiceModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        refreshRoomServices();

        JPanel btns = btnPanel();
        JButton order   = navyBtn("Order Service");
        JButton refresh = greenBtn("Refresh");

        order.addActionListener(e -> orderService());
        refresh.addActionListener(e -> refreshRoomServices());

        btns.add(order); btns.add(refresh);
        panel.add(btns, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshRoomServices() {
        roomServiceModel.setRowCount(0);
        for (RoomService rs : hotel.getRoomServices()) {
            for (Service s : rs.getServiceOrders()) {
                roomServiceModel.addRow(new Object[]{
                    rs.getRoom().getRoomCode(), rs.getRoom().getRoomType(),
                    s.getServiceName(), String.format("$%.2f", s.getServiceCost())
                });
            }
        }
    }

    private void orderService() {
        // Get booked rooms
        ArrayList<Room> bookedRooms = new ArrayList<>();
        for (Room r : hotel.getRooms()) if (!r.isAvailable()) bookedRooms.add(r);

        if (bookedRooms.isEmpty()) { warn("No rooms are currently booked."); return; }
        if (hotel.getServices().isEmpty()) { warn("No services configured."); return; }

        String[] roomOptions = bookedRooms.stream()
                .map(r -> r.getRoomCode() + " (" + r.getRoomType() + ")")
                .toArray(String[]::new);
        JComboBox<String> roomBox = new JComboBox<>(roomOptions);

        String[] svcOptions = hotel.getServices().stream()
                .map(s -> s.getServiceCode() + " - " + s.getServiceName() + " ($" + String.format("%.2f", s.getServiceCost()) + ")")
                .toArray(String[]::new);
        JComboBox<String> svcBox = new JComboBox<>(svcOptions);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        form.add(new JLabel("Room:")); form.add(roomBox);
        form.add(new JLabel("Service:")); form.add(svcBox);

        if (JOptionPane.showConfirmDialog(this, form, "Order Room Service",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        Room selectedRoom = bookedRooms.get(roomBox.getSelectedIndex());
        Service selectedSvc = hotel.getServices().get(svcBox.getSelectedIndex());

        RoomService rs = hotel.getRoomServiceForRoom(selectedRoom);
        if (rs == null) {
            rs = new RoomService(selectedRoom);
            hotel.addRoomService(rs);
        }
        rs.orderService(selectedSvc);
        refreshRoomServices();
        JOptionPane.showMessageDialog(this,
                selectedSvc.getServiceName() + " ordered for Room " + selectedRoom.getRoomCode() + ".",
                "Service Ordered", JOptionPane.INFORMATION_MESSAGE);
    }

    // ======================================================
    // BILLING TAB
    // ======================================================
    private JPanel buildBillingTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel hint = new JLabel("Select a guest to view their full bill:", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        hint.setForeground(new Color(80, 80, 100));

        JTextArea billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billArea.setBackground(Color.WHITE);
        JScrollPane billScroll = new JScrollPane(billArea);

        JPanel btnRow = btnPanel();
        JButton viewBill = navyBtn("View Bill");
        JButton pay      = greenBtn("Mark as Paid");

        viewBill.addActionListener(e -> showBill(billArea));
        pay.addActionListener(e -> makePaid(billArea));

        btnRow.add(viewBill); btnRow.add(pay);

        panel.add(hint, BorderLayout.NORTH);
        panel.add(billScroll, BorderLayout.CENTER);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    private Guest lastBilledGuest = null;

    private void showBill(JTextArea billArea) {
        if (hotel.getGuests().isEmpty()) { warn("No guests registered."); return; }

        String[] guestOptions = hotel.getGuests().stream()
                .map(g -> g.getId() + " - " + g.getFullName())
                .toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this,
                "Select guest:", "View Bill", JOptionPane.PLAIN_MESSAGE,
                null, guestOptions, guestOptions[0]);

        if (selected == null) return;
        int gid = Integer.parseInt(selected.split(" - ")[0].trim());
        Guest guest = hotel.searchGuest(gid);
        if (guest == null) return;
        lastBilledGuest = guest;

        StringBuilder sb = new StringBuilder();
        sb.append("=================================================\n");
        sb.append("           GRAND EAGLE HOTEL — BILL\n");
        sb.append("=================================================\n");
        sb.append("Guest  : ").append(guest.getFullName()).append("\n");
        sb.append("Email  : ").append(guest.getEmail()).append("\n");
        sb.append("Phone  : ").append(guest.getPhoneNumber()).append("\n");
        sb.append("-------------------------------------------------\n");
        sb.append("RESERVATIONS:\n");

        double totalRoomCost = 0;
        for (Reservation res : hotel.getReservations()) {
            if (res.getGuest().equals(guest) && !"Cancelled".equals(res.getStatus())) {
                sb.append(String.format("  Res #%d  %s to %s  (%d days)\n",
                        res.getReservationId(), res.getCheckInDate(), res.getCheckOutDate(), res.getNumberOfDays()));
                for (Room r : res.getReservationRooms())
                    sb.append(String.format("    Room %d (%s) $%.2f/night\n", r.getRoomCode(), r.getRoomType(), r.getPricePerNight()));
                sb.append(String.format("    Subtotal: $%.2f  [%s]\n", res.getTotalCost(), res.getPayment().getPaymentStatus()));
                totalRoomCost += res.getTotalCost();
            }
        }

        sb.append("-------------------------------------------------\n");
        sb.append("ROOM SERVICES:\n");
        double totalServiceCost = 0;
        for (Reservation res : hotel.getReservations()) {
            if (res.getGuest().equals(guest) && !"Cancelled".equals(res.getStatus())) {
                for (Room r : res.getReservationRooms()) {
                    RoomService rs = hotel.getRoomServiceForRoom(r);
                    if (rs != null && !rs.getServiceOrders().isEmpty()) {
                        sb.append("  Room ").append(r.getRoomCode()).append(":\n");
                        for (Service s : rs.getServiceOrders()) {
                            sb.append(String.format("    %-25s $%.2f\n", s.getServiceName(), s.getServiceCost()));
                            totalServiceCost += s.getServiceCost();
                        }
                    }
                }
            }
        }

        sb.append("=================================================\n");
        sb.append(String.format("Room Total   : $%.2f\n", totalRoomCost));
        sb.append(String.format("Service Total: $%.2f\n", totalServiceCost));
        sb.append(String.format("GRAND TOTAL  : $%.2f\n", totalRoomCost + totalServiceCost));
        sb.append("=================================================\n");

        billArea.setText(sb.toString());
    }

    private void makePaid(JTextArea billArea) {
        if (lastBilledGuest == null) { warn("Please view a bill first."); return; }

        boolean anyUpdated = false;
        for (Reservation res : hotel.getReservations()) {
            if (res.getGuest().equals(lastBilledGuest)
                    && !"Cancelled".equals(res.getStatus())
                    && "Pending".equals(res.getPayment().getPaymentStatus())) {
                res.getPayment().processPayment();
                anyUpdated = true;
            }
        }
        if (anyUpdated) {
            HotelApp.save();
            showBill(billArea);
            JOptionPane.showMessageDialog(this, "Payment processed for " + lastBilledGuest.getFullName() + ".",
                    "Payment Done", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No pending payments for this guest.", "Info", JOptionPane.INFORMATION_MESSAGE);
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
        b.setPreferredSize(new Dimension(155, 34));
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
