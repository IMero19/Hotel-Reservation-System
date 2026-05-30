# Hotel Management System Phase 2

A comprehensive Java-based hotel management application featuring role-based dashboards for administrators, managers, and receptionists. Manages guest reservations, room availability, employee administration, and hotel services including spa, laundry, dining, and transportation. Built with Java Swing GUI interface and file-based data storage.

## Overview

Hotel Management System Phase 2 is a fully-featured desktop application designed to streamline hotel operations and management. Built with Java, it provides an intuitive graphical interface for managing all aspects of a hotel business.

## Key Features

### 1. **User Authentication & Role-Based Access**
   - Secure login system with user account management
   - Three distinct user roles: Administrator, Manager, and Receptionist
   - Role-specific dashboards and permissions

### 2. **Guest Management**
   - Add, update, and manage guest profiles
   - Track guest information and history
   - Guest-specific preferences and records

### 3. **Room Management**
   - Comprehensive room inventory system
   - Track room availability and status
   - Manage room types, pricing, and capacity
   - Real-time room booking status

### 4. **Reservation Management**
   - Create and manage guest reservations
   - Check room availability
   - Handle reservation modifications and cancellations
   - Automated reservation tracking

### 5. **Employee & Department Management**
   - Manage employee records and information
   - Organize staff by departments
   - Track manager and receptionist roles
   - Employee performance and scheduling

### 6. **Hotel Services**
   - Spa services
   - Laundry service
   - Breakfast offerings
   - Room cleaning service
   - Airport transfer arrangements

### 7. **Additional Facilities**
   - **Restaurant Management**: Dining reservations and menu management
   - **Parking Management**: Track parking spaces and assignments
   - **Transportation Coordination**: Manage vehicle services and transfers

### 8. **Payment Processing**
   - Record and process guest payments
   - Track financial transactions
   - Payment history and invoicing

### 9. **Building Management**
   - Manage hotel buildings and facilities
   - Facility organization and maintenance

### 10. **Data Persistence**
   - File-based data storage
   - Automatic data loading and saving
   - Support for multiple data files:
     - `employees.txt`
     - `guests.txt`
     - `reservations.txt`
     - `rooms.txt`
     - `users.txt`

## Technology Stack

- **Language**: Java
- **UI Framework**: Java Swing (Nimbus Look & Feel)
- **IDE**: NetBeans
- **Data Storage**: File-based persistence
- **Build Tool**: Apache Ant (build.xml)

## System Requirements

- Java Development Kit (JDK) 8 or higher
- NetBeans IDE (recommended for development)
- Windows, macOS, or Linux

## Installation & Setup

### Using NetBeans

1. Open NetBeans IDE
2. Go to **File** → **Open Project**
3. Navigate to the project directory and select it
4. The project will be automatically configured
5. Right-click on the project and select **Build**
6. To run: Right-click on the project and select **Run**

## Project Structure
HotelManagementSystemPhase2/
├── src/                          # Source code
│   ├── Main.java                # Application entry point
│   ├── HotelApp.java            # Main application controller
│   ├── LoginForm.java           # Login interface
│   ├── Hotel.java               # Hotel class
│   ├── AdminDashboard.java      # Admin interface
│   ├── ManagerDashboard.java    # Manager interface
│   ├── ReceptionistDashboard.java # Receptionist interface
│   ├── Room.java                # Room management
│   ├── Guest.java               # Guest information
│   ├── Employee.java            # Employee records
│   ├── Reservation.java         # Reservation handling
│   ├── Manager.java             # Manager role
│   ├── Receptionist.java        # Receptionist role
│   ├── UserAccount.java         # User accounts
│   ├── Payment.java             # Payment processing
│   ├── Service.java             # Hotel services
│   ├── RoomService.java         # Room service management
│   ├── Restaurant.java          # Restaurant management
│   ├── Transportation.java      # Transportation services
│   ├── Parking.java             # Parking facilities
│   ├── Department.java          # Department management
│   ├── Building.java            # Building management
│   ├── FileManager.java         # Data persistence
│   ├── RoomManagement.java      # Room interface
│   ├── ReservationManagement.java # Reservation interface
│   └── Custom Exceptions        # InvalidLoginException, InvalidInputException, etc.
├── data/                        # Data storage files
│   ├── employees.txt
│   ├── guests.txt
│   ├── reservations.txt
│   ├── rooms.txt
│   └── users.txt
├── build/                       # Compiled files
├── build.xml                    # Ant build configuration
└── manifest.mf                  # Manifest file

## Usage
Launch the Application: Run the application through NetBeans or command line
Login: Use your credentials to access the system
Select Role: Access the appropriate dashboard based on your role
Manage Operations:
Admins: Full system control and configuration
Managers: Staff and operations management
Receptionists: Guest services and reservations
