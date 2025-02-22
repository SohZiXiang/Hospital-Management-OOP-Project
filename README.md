# Hospital Management System (HMS) - OOP Implementation

This is a **Hospital Management System (HMS)** developed in **Java** using **Object-Oriented Programming (OOP)** principles. The system includes essential features for managing patients, appointments, inventory, and administrative tasks. Additionally, it integrates **SMS notifications via Twilio**, **email functionality using Mailtrap**, and an **activity log** feature to track system actions.

## Features

### 1. **Patient Management**
- Add, update, and view patient details.
- Manage patient medical records.
- Schedule, reschedule, or cancel patient appointments.

### 2. **Doctor Appointment System**
- Doctors can view and manage their schedules.
- Patients can book appointments with available doctors.

### 3. **Inventory Management**
- Track hospital inventory such as medicines, equipment, and supplies.
- Automatically update stock levels after usage or new arrivals.
- Alerts for low stock items.

### 4. **Administrator Actions**
- Admin can manage user roles (e.g., doctors, nurses, and patients).
- Admin can modify hospital settings such as working hours, room allocations, etc.

### 5. **SMS Notification (Twilio Integration)**
- SMS notifications are sent to patients for appointment reminders, cancellations, and confirmations using **Twilio API**.
- Setup includes environment variables for Twilio Account SID, Auth Token, and sender number.

### 6. **Email Notifications (Mailtrap Integration)**
- Email notifications for appointment confirmations, reminders, and system alerts are sent using **Mailtrap** for testing email functionality.
- Setup includes environment variables for Mailtrap credentials.

### 7. **Activity Log**
- Track all critical system actions (e.g., patient record creation, inventory updates).
- Logs can be accessed by the administrator to monitor system activity.

## Technologies Used

- **Java** for implementing the core system.
- **Twilio API** for sending SMS notifications.
- **Mailtrap** for email testing.
- **MySQL** or **SQLite** for database management (depending on the chosen setup).
- **OOP principles** such as inheritance, polymorphism, and encapsulation to organize the code and manage system components

## My Contributions

- **System Design**: Contributed to the overall design and architecture of the system using Object-Oriented Programming (OOP) principles.
- **Email System**: Configured and integrated **Mailtrap** to send email notifications for appointment confirmations and reminders when Inventory system detects low stock to staff email.
- **Activity Log**: Developed and implemented the activity log feature, allowing administrators to track critical system actions, such as patient record creation and inventory updates.
- **Inventory Management**: Contributed to the design and implementation of the hospital's inventory management system, including automatic stock level updates and low stock alerts.
- **Administrator Functions**: Developed and tested functionalities related to the admin's ability to manage users and hospital settings.


