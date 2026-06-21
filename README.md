# EventHub

## Overview

EventHub is a Spring Boot web application that allows users to organize, publish and join events around the world. The platform provides an intuitive user interface for creating event experiences, discovering upcoming events and managing agenda items associated with each event.

The project is developed as a Spring Fundamentals style application and demonstrates the use of Spring MVC, Thymeleaf, Spring Data JPA, validation, authentication and role-based authorization.

---

## Technology Stack

* Java 17
* Spring Boot 3.4.0
* Spring MVC
* Spring Data JPA
* Spring Security
* Thymeleaf
* MySQL
* Maven
* HTML5
* CSS3
* Bootstrap 5

---

## Domain Entities

### User

Represents an application user.

#### Properties

* UUID id
* username
* email
* password
* firstName
* lastName
* profilePicture
* role
* active
* createdAt

---

### Event

Represents an event created by a user.

#### Properties

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
* status
* creator

---

### Registration

Represents a registration made by an attendee.

#### Properties

* UUID id
* event
* attendee
* registrationDate
* attendeesCount
* status

---

### AgendaItem

Represents a scheduled session associated with an event.

#### Properties

* UUID id
* event
* title
* speaker
* imageUrl
* description
* startTime
* endTime
* displayOrder

---

## Functionalities

### Create Event

Authenticated users can create new events.

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

---

## Security

### Guests can

* Register
* Login
* Access public pages
* Browse available events

### Authenticated users can

* Browse events
* Register for events
* Manage their registrations
* Create new events
* Manage their profile

### Event creators can

* Create events
* Edit events
* Delete events
* Manage agenda items

### Administrators can

* Manage all users
* Manage all events
* Manage all registrations
* Manage all agenda items

Passwords are stored using BCrypt hashing.

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

Browse all available events.

### Create Event

Form for creating a new event.

### Edit Event

Form for updating an existing event.

### Event Details

Detailed information about a selected event.

### My Events

Displays events created by the currently logged-in user.

### My Registrations

Displays events the user has registered for.

### Profile

User profile management page.

### Agenda Management

Create, edit and delete agenda items for an event.

### Error Pages

Custom error pages for unauthorized and missing resources.

---

## Relationships

### Event -> User

ManyToOne

Each event is created by a single user.

### Registration -> User

ManyToOne

Each registration belongs to a single attendee.

### Registration -> Event

ManyToOne

Each registration belongs to a single event.

### AgendaItem -> Event

ManyToOne

Each agenda item belongs to a single event.

---

## Future Improvements

* Event categories
* Event search and filtering
* Image uploads
* Ratings and reviews
* Interactive maps integration
* Email notifications
* QR code event check-in
* Social media sharing
* Event analytics dashboard
* Online ticket payments
