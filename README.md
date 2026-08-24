# EventHub

## Overview

EventHub is a Spring Boot web application that allows users to organize, publish and join events around the world. The platform provides an intuitive user interface for creating event experiences, discovering upcoming events and managing agenda items associated with each event.

The project is developed with a **microservices architecture**:
- **Main Application** (event-hub): Web portal with Spring MVC, user management, event management
- **Notification Microservice** (notification-service): Separate service for notifications and preferences

The project demonstrates the use of Spring MVC, Thymeleaf, Spring Data JPA, Spring Cloud Feign, validation, authentication and role-based authorization.

---

## Technology Stack

* Java 21
* Spring Boot 4.1.0
* Spring Cloud OpenFeign (Microservices)
* Spring MVC
* Spring Data JPA
* Spring Security
* Thymeleaf
* MySQL (separate databases)
* Maven
* HTML5
* CSS3
* Bootstrap 5

---

## Microservices Architecture

### Main Application (event-hub)
Port: **8080**  
Database: `event_hub`  
Responsibilities:
- User authentication & authorization
- Event creation, editing, deletion
- Event registration management
- User profile management
- Admin user & event management

### Notification Microservice (notification-service)
Port: **8081**  
Database: `event_hub_notifications`  
Responsibilities:
- Broadcast announcements to event attendees
- Store notification preferences (Email, SMS, Push, etc.)
- Track notification delivery status
- Expose REST APIs for notification operations

**See [NOTIFICATION_MICROSERVICE.md](./NOTIFICATION_MICROSERVICE.md) for complete microservice documentation.**

---

## Domain Entities

### Main Application Entities

#### User

Represents an application user with authentication and role information.

**Properties:**
* UUID id
* username
* email
* password (BCrypt hashed)
* firstName
* lastName
* profilePicture
* role (USER, ORGANIZER, ADMIN)
* active
* createdAt

---

#### Event

Represents an event created by a user.

**Properties:**
* UUID id
* title
* city
* venue
* description
* imageUrl
* capacity
* ticketPrice
* startDateTime
* endDateTime
* status (DRAFT, PUBLISHED, ONGOING, COMPLETED, CANCELLED)
* creator (User relationship)
* agendaItems (List of AgendaItem)

---

#### Registration

Represents a registration made by an attendee for an event.

**Properties:**
* UUID id
* event (Event)
* attendee (User)
* registrationDate
* attendeesCount
* status (CONFIRMED, CANCELLED)

---

#### AgendaItem

Represents a scheduled session associated with an event.

**Properties:**
* UUID id
* event (Event)
* title
* speaker
* imageUrl
* description
* startTime
* endTime
* displayOrder

---

### Notification Microservice Entities

#### Notification
Represents a broadcast notification for an event.
**Key Fields:** eventId, title, content, type, status, sentAt

#### NotificationRecipient (One-to-Many with Notification)
Links a notification to multiple user recipients for delivery tracking.
**Key Fields:** notificationId, userId, deliveryStatus, deliveredAt

#### UserNotificationPreference
Stores user's notification channel preferences.
**Key Fields:** userId, emailEnabled, smsEnabled, appAlertsEnabled, pushNotificationEnabled

---

## Functionalities

### Create Event

Authenticated users (ORGANIZER, ADMIN) can create new events.

### Edit Event

Event creators can update existing events.

### Delete Event

Event creators can remove events.

### Register for Event

Users can register for available events.

### Cancel Registration

Users can cancel their participation.

### Manage Agenda

Event creators can add, edit and remove agenda items.

### View Event Details

Users can view complete event information, agenda schedule and attendee statistics.

### Manage Profile

Authenticated users can update their personal information and profile details.

### **Broadcast Announcements**

Event organizers can send announcements to all registered attendees.
- Opens modal from event dashboard
- Calls Feign client to microservice
- Notification created for each attendee
- Success notification with recipient count

### **Manage Notification Preferences**

Users can control notification channels:
- Email notifications
- SMS alerts
- In-app alerts
- Push notifications

---

## Security

### Guests can

* Register
* Login
* Access public pages
* Browse available events
* View event details

### Authenticated users can

* Browse events
* Register for events
* Manage their registrations
* Create new events
* Manage their profile
* Update notification preferences

### Event creators (ORGANIZER) can

* Create events
* Edit own events
* Delete own events
* Manage agenda items
* Broadcast announcements to attendees
* View event registrations

### Administrators (ADMIN) can

* Manage all users (role assignment, activation)
* Manage all events (edit/delete any event)
* Manage all registrations
* Manage all agenda items
* Access admin dashboard at `/users`
* Broadcast announcements to any event

**Authentication:** Spring Security with BCrypt password hashing  
**Authorization:** URL-level and method-level @PreAuthorize annotations  
**Session:** Spring Security session management with HttpSession backup

---

## Validation

All forms include server-side validation.

### Examples

* Required fields
* Minimum and maximum length
* Email validation
* URL validation
* Date and time validation
* Capacity validation
* Business rule validation

### Business Rules

* Event end date must be after the start date
* Event capacity must be greater than zero
* A user can register only once for a specific event
* Agenda item end time must be after its start time
* Registration is not allowed when the event is full
* Event organizer can only edit/delete their own events (admins bypass this)
* Announcements can only be sent to events with registered attendees

Validation messages are displayed directly next to invalid fields.

---

## Pages

### Home

Landing page with featured and upcoming events.

### Login

User authentication page.

### Register

User registration page.

### Event Catalog

Browse all available events with search and filtering.

### Create Event

Form for creating a new event.

### Edit Event

Form for updating an existing event.

### Event Details

Detailed information about a selected event with booking/registration.

### My Events Dashboard

Displays events created by the currently logged-in user.
- Includes "📢 Announce" button for each event
- Modal for composing and sending announcements

### My Registrations (My Tickets)

Displays events the user has registered for with cancel option.

### Profile

User profile management page with link to notification preferences.

### Notification Settings

Toggle notification preferences for different channels.

### Agenda Management

Create, edit and delete agenda items for an event.

### Admin Panel

Admin-only pages for managing users, events, and registrations.

### Error Pages

Custom error pages for unauthorized and missing resources.

---

## Relationships

### Main Application

#### Event -> User
**ManyToOne**  
Each event is created by a single user (creator).

#### Registration -> User
**ManyToOne**  
Each registration belongs to a single attendee (User).

#### Registration -> Event
**ManyToOne**  
Each registration belongs to a single event.

#### AgendaItem -> Event
**ManyToOne**  
Each agenda item belongs to a single event.

#### Event -> AgendaItem
**OneToMany**  
Each event can have multiple agenda items.

---

### Notification Microservice

#### Notification -> NotificationRecipient
**OneToMany (with CascadeType.ALL)**  
Each notification can have multiple recipients for delivery tracking.
- Cascade delete: Removing notification deletes all recipient entries
- Orphan removal: Unused recipients are automatically deleted

---

## Database Setup

### 1. Create Main Application Database
```sql
CREATE DATABASE event_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Create Notification Microservice Database
```sql
CREATE DATABASE event_hub_notifications CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Run Applications
Hibernate will auto-create tables based on `spring.jpa.hibernate.ddl-auto=update`

---

## Running the Application

### Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.6+

### 1. Start Notification Microservice
```bash
cd notification-service
./mvnw spring-boot:run
# Runs on http://localhost:8081
```

### 2. Start Main Application
```bash
cd event-hub
./mvnw spring-boot:run
# Runs on http://localhost:8080
```

### 3. Access Application
Open browser: **http://localhost:8080**

### Test Credentials

**Admin User:**
- Username: `admin`
- Password: `password`
- Role: ADMIN

**Test Organizer:**
- Username: `organizer`
- Password: `password`
- Role: ORGANIZER

**Test User:**
- Username: `testuser`
- Password: `password`
- Role: USER

---

## Key Features Demonstration

### Feature 1: Broadcast Announcements
1. Login as organizer/admin
2. Go to "My Events" dashboard
3. Click "📢 Announce" on any event
4. Enter title and message
5. Click "Send Announcement"
6. Confirmation: "Announcement sent to X attendees!"

### Feature 2: Notification Preferences
1. Login as any authenticated user
2. Go to Profile
3. Click "Manage Notification Preferences"
4. Toggle preference switches
5. Click "Save Preferences"
6. Confirmation: "Your alert preferences have been updated successfully!"

---

## Future Improvements

* Event categories
* Advanced search and filtering
* Image uploads to cloud storage
* Ratings and reviews
* Interactive maps integration
* Email gateway integration (SendGrid, AWS SES)
* SMS gateway integration (Twilio)
* QR code event check-in
* Social media sharing
* Event analytics dashboard
* Online payment processing
* Notification delivery statistics
* Push notification (FCM) integration
* Event scheduling and reminders
---

## Troubleshooting

### Microservice Connection Issues
- Ensure notification-service is running on port 8081
- Check database connectivity for `event_hub_notifications`
- Verify Feign client configuration in main app

### Database Errors
- Ensure MySQL is running
- Check database credentials in application.properties
- Verify both databases exist

### Port Already in Use
- Main app uses 8080, microservice uses 8081
- Change ports in respective application.properties if needed

---

## File Structure

```
event-hub/
├── event-hub/                          # Main Application
│   ├── src/main/java/com/event_hub/event_hub/
│   │   ├── Application.java
│   │   ├── config/
│   │   ├── web/                        # Controllers
│   │   ├── service/                    # Business logic
│   │   ├── repository/                 # Data access
│   │   ├── model/                      # Entities & DTOs
│   │   ├── mapper/                     # Entity mappers
│   │   ├── security/                   # Security config
│   │   ├── exception/                  # Custom exceptions
│   │   └── client/                     # Feign clients
│   ├── src/main/resources/
│   │   ├── templates/                  # Thymeleaf views
│   │   ├── static/                     # CSS, JS, images
│   │   └── application.properties
│   └── pom.xml
│
├── notification-service/               # Notification Microservice
│   ├── src/main/java/com/event_hub/notification/
│   │   ├── NotificationServiceApplication.java
│   │   ├── model/
│   │   │   ├── entity/                 # JPA entities
│   │   │   └── dto/                    # REST DTOs
│   │   ├── repository/                 # JPA repositories
│   │   ├── service/                    # Business logic
│   │   └── web/                        # REST Controller
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── README.md                           # This file
├── NOTIFICATION_MICROSERVICE.md        # Microservice details
└── .git/
```

---

**Author:** EventHub Development Team  
**Last Updated:** 2025-01-15  
**License:** MIT

