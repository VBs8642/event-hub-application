# EventHub Notification Microservice

## Overview

The Notification Microservice is a separate Spring Boot application that handles all notification-related functionality for the EventHub platform. It operates independently with its own database and provides REST APIs for the main EventHub application via Feign client integration.

### Database Schema

#### Notifications Table
```sql
CREATE TABLE notifications (
    id BINARY(16) PRIMARY KEY,
    event_id BINARY(16) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    type ENUM('ANNOUNCEMENT', 'REMINDER', 'ALERT', 'UPDATE') DEFAULT 'ANNOUNCEMENT',
    status ENUM('PENDING', 'SENT', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    sent_at DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);
```

#### Notification Recipients Table (One-to-Many Relationship)
```sql
CREATE TABLE notification_recipients (
    id BINARY(16) PRIMARY KEY,
    notification_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    delivery_status ENUM('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'BOUNCED') DEFAULT 'PENDING',
    delivered_at DATETIME,
    read_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_notification_recipient (notification_id, user_id),
    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE
);
```

#### User Notification Preferences Table
```sql
CREATE TABLE user_notification_preferences (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) UNIQUE NOT NULL,
    email_enabled BOOLEAN DEFAULT true,
    sms_enabled BOOLEAN DEFAULT false,
    app_alerts_enabled BOOLEAN DEFAULT true,
    push_notification_enabled BOOLEAN DEFAULT true,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME
);
```

## Key Features

### 1. Broadcast Announcements
**Purpose:** Allow event organizers to send announcements to all registered attendees

**Flow:**
1. Organizer opens event dashboard
2. Clicks "📢 Announce" button on event card
3. Modal opens with title & message fields
4. Form POSTs to `POST /events/{eventId}/announcements` (main app)
5. Main app calls Feign client: `POST /api/micro/notifications/broadcast`
6. Microservice creates Notification entity
7. Creates NotificationRecipient entry for each registered user
8. Returns success with count of recipients
9. Toast notification shows: "Announcement sent to X attendees!"

**Main App Endpoint:**
```
POST /events/{eventId}/announcements
Parameters:
  - title (String)
  - content (String)
```

**Microservice Endpoint:**
```
POST /api/micro/notifications/broadcast
Body:
{
  "event_id": "uuid",
  "title": "string",
  "content": "string",
  "recipient_user_ids": ["uuid1", "uuid2", ...]
}

Response:
{
  "notification_id": "uuid",
  "recipients_count": 150,
  "message": "Announcement sent successfully",
  "status": "SENT"
}
```

### 2. Notification Preferences
**Purpose:** Let users control how they receive notifications

**Features:**
- Email notifications (enabled by default)
- SMS alerts (disabled by default)
- In-app alerts (enabled by default)
- Push notifications (enabled by default)

**Flow:**
1. User goes to profile page
2. Clicks "Manage Notification Preferences" button
3. Navigates to `/profile/notification-settings`
4. Toggles preference switches
5. Clicks "Save Preferences"
6. Form POSTs to `POST /profile/notification-settings`
7. Main app calls Feign: `PUT /api/micro/notifications/preferences/{userId}`
8. Microservice creates or updates UserNotificationPreference entity
9. Toast shows: "Your alert preferences have been updated successfully!"

**Main App Endpoints:**
```
GET /profile/notification-settings - Show preferences form
POST /profile/notification-settings - Save preferences
PUT /profile/notification-settings/{userId} - JSON update
GET /api/micro/notifications/preferences/{userId} - Fetch preferences
```

**Microservice Endpoints:**
```
PUT /api/micro/notifications/preferences
PUT /api/micro/notifications/preferences/{userId}
GET /api/micro/notifications/preferences/{userId}

Request Body:
{
  "user_id": "uuid",
  "email_enabled": true,
  "sms_enabled": false,
  "app_alerts_enabled": true,
  "push_notification_enabled": true
}

Response:
{
  "preference_id": "uuid",
  "user_id": "uuid",
  "email_enabled": true,
  "sms_enabled": false,
  "app_alerts_enabled": true,
  "push_notification_enabled": true,
  "updated_at": "2025-01-15T10:30:00"
}
```

## Entity Relationships

### Notification ↔ NotificationRecipient (One-to-Many)
- One Notification can have many NotificationRecipient entries
- Cascade delete: Deleting a notification removes all recipients
- Unique constraint: (notification_id, user_id) prevents duplicate recipients

```java
@OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, 
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<NotificationRecipient> recipients;
```

### UserNotificationPreference (User → Preferences)
- One-to-One relationship with User (represented by userId UUID)
- Unique constraint on user_id ensures one preference per user
- Contains preference flags for different notification channels

## Security Considerations

### Data Protection
- All UUIDs use `@GeneratedValue(strategy = GenerationType.UUID)` for primary keys
- No sensitive data stored in notifications (passwords, tokens, etc.)
- User IDs referenced but not sensitive user data
- Event IDs referenced for context

### Authorization
- Event organizers can only broadcast to their own events
  - Admin users can broadcast to any event
- Users can only modify their own preferences
- Feign client validates authorization before calling microservice

### CORS
- Microservice enables CORS for main app communication
```java
@CrossOrigin(origins = "*", maxAge = 3600)
```

## Configuration

### Main App (event-hub/pom.xml)
Add Feign client dependency:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>4.0.4</version>
</dependency>
```

Enable Feign in Application.java:
```java
@SpringBootApplication
@EnableFeignClients
public class Application { ... }
```

### Microservice (notification-service/application.properties)
```properties
server.port=8081
spring.application.name=notification-service

# Separate Database for Notifications
spring.datasource.url=jdbc:mysql://localhost:3306/event_hub_notifications
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

## Running the Microservices

### 1. Create Notification Database
```sql
CREATE DATABASE event_hub_notifications CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Build Notification Microservice
```bash
cd notification-service
./mvnw clean install
./mvnw spring-boot:run
```

### 3. Build Main App (requires microservice running on port 8081)
```bash
cd event-hub
./mvnw clean install
./mvnw spring-boot:run
```

## API Health Check

**Microservice Status:**
```
GET http://localhost:8081/api/micro/notifications/health
Response: "Notification service is running"
```

## Future Enhancements

1. **Email Integration**
   - Send actual emails based on preferences
   - Email templates with event details

2. **SMS Gateway**
   - Twilio/AWS SNS integration
   - SMS templates

3. **Push Notifications**
   - Firebase Cloud Messaging (FCM)
   - Web push notifications

4. **Notification History**
   - Audit log for all notifications sent
   - Read receipts tracking

5. **Scheduled Reminders**
   - Send reminders X hours before event
   - Recurring notification patterns

6. **Analytics**
   - Notification delivery rates
   - User preference statistics
   - Engagement metrics

## Testing

### Unit Tests
- Test NotificationService methods
- Validate entity creation and relationships
- Mock repository calls

### Integration Tests
- Test REST endpoints
- Database transaction handling
- Feign client communication

### E2E Tests
- Full flow from UI to microservice
- Multiple user scenarios
- Error handling

## Monitoring

- Enable actuator endpoints:
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

- Check logs for errors:
```
tail -f logs/notification-service.log
```

---

**Microservice Port:** 8081  
**Main App Port:** 8080  
**Database:** Separate MySQL instance for notifications
