public class UserAccount {
    private int accountId;
    private String username;
    private String password;
    private String role; // "ADMIN", "MANAGER", "RECEPTIONIST"
    private int linkedPersonId; // ID of the Employee/Manager linked to this account (0 for admin)

    public UserAccount(int accountId, String username, String password, String role, int linkedPersonId) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedPersonId = linkedPersonId;
    }

    public int getAccountId() { return accountId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public int getLinkedPersonId() { return linkedPersonId; }

    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }

    // Format for saving to file: id|username|password|role|linkedPersonId
    public String toFileString() {
        return accountId + "|" + username + "|" + password + "|" + role + "|" + linkedPersonId;
    }

    // Parse from file line
    public static UserAccount fromFileString(String line) throws InvalidInputException {
        String[] parts = line.split("\\|");
        if (parts.length != 5) throw new InvalidInputException("Invalid user record: " + line);
        try {
            int id = Integer.parseInt(parts[0].trim());
            String username = parts[1].trim();
            String password = parts[2].trim();
            String role = parts[3].trim();
            int linkedId = Integer.parseInt(parts[4].trim());
            return new UserAccount(id, username, password, role, linkedId);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Malformed user record: " + line);
        }
    }

    @Override
    public String toString() {
        return "Account[" + accountId + "] " + username + " (" + role + ")";
    }
}
