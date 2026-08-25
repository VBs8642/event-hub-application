# Data Validation and Error Handling Implementation Guide

## Overview

This document details the comprehensive validation and error handling implementation across both the main EventHub application and the Notification Microservice. The implementation follows Spring best practices and provides consistent, meaningful error responses across all layers.

## Architecture

### Validation Layers

#### 1. **DTO Layer Validation**
DTOs use Jakarta Validation (javax.validation) annotations to validate user input at the boundary layer.

**Main Application DTOs:**
- `UserRegisterRequest`: Email format, username length (3-50), password length (6+), required names
- `UserLoginRequest`: Email format validation, password length (6+), required fields
- `EventCreateUpdateDto`: Title length (max 100), capacity (min 1), price (0+), date validation, image URL format
- `RegistrationDto`: Event ID required, attendees count (1-5)
- `AgendaItemDto`: Title/speaker required, time validation, display order (min 1)
- `AnnouncementRequest`: Title (1-200 chars), content (1-5000 chars)

**Microservice DTOs:**
- `BroadcastAnnouncementRequest`: Event ID required, title/content length constraints, recipients required
- `UserNotificationPreferenceRequest`: User ID required, boolean preferences

#### 2. **Entity Layer Validation**
Entities use the same validation annotations for database consistency:
- `User`: Username length, email uniqueness, required fields
- `Event`: Title size, capacity constraints, price validation
- `Registration`: Event/user references, attendance count limits
- `AgendaItem`: Time fields validation
- `Notification`: Event/content validation
- `UserNotificationPreference`: User ID uniqueness

#### 3. **Service Layer Validation**
Business logic validation ensures data integrity and enforces business rules:
- User registration: Duplicate email/username detection
- Event operations: Authorization checks, status validation
- Registration booking: Capacity validation, duplicate registration checks
- Agenda management: Time logic validation (start < end)
- Notification sending: Recipient validation, content validation

### Error Response Structure

All errors return a consistent `ErrorResponse` object:

```json
{
  "status": 400,
  "message": "Validation failed",
  "error": "VALIDATION_ERROR",
  "timestamp": "2024-01-15T10:30:45",
  "path": "/api/events",
  "fieldErrors": [
    {
      "field": "title",
      "message": "Title cannot exceed 100 characters",
      "rejectedValue": "This is a very long title that exceeds the maximum..."
    }
  ]
}
```

### Custom Exceptions

#### Main Application Exceptions:
1. **ResourceNotFoundException** (HTTP 404)
   - Thrown when requested resource is not found
   - Usage: Events, users, registrations not found

2. **UnauthorizedAccessException** (HTTP 403)
   - Thrown when user lacks required permissions
   - Usage: Resource ownership violations, admin-only operations

3. **BusinessException** (HTTP 400)
   - Thrown when business rules are violated
   - Usage: Capacity exceeded, duplicate entries, invalid state transitions

4. **ValidationException** (HTTP 400)
   - Thrown for validation failures not caught by annotations
   - Usage: Complex cross-field validations

5. **ResourceOwnerException** (HTTP 403)
   - Legacy exception for resource ownership checks
   - Usage: Event deletion/editing by non-owners

#### Microservice Exceptions:
- **ResourceNotFoundException** (HTTP 404)
- **ValidationException** (HTTP 400)
- **BusinessException** (HTTP 400)

## Global Exception Handlers

### Main Application: `GlobalExceptionHandler.java`

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // Handles @Valid annotation validation failures
    
    @ExceptionHandler(ResourceNotFoundException.class)
    // Returns 404 with resource not found details
    
    @ExceptionHandler(UnauthorizedAccessException.class)
    // Returns 403 with permission details
    
    @ExceptionHandler(BusinessException.class)
    // Returns 400 with business logic error
    
    @ExceptionHandler(ValidationException.class)
    // Returns 400 with validation error
    
    @ExceptionHandler(ResourceOwnerException.class)
    // Returns 403 for resource ownership violations
    
    @ExceptionHandler(Exception.class)
    // Catches all unexpected exceptions with 500
}
```

### Microservice: `GlobalExceptionHandler.java`

Similar structure for REST API endpoints with `@ControllerAdvice(annotations = RestController.class)`.

## Validation in Action

### Example 1: Registration with Invalid Input

**Request:**
```bash
POST /register
body: {
  "username": "ab",  # Too short
  "email": "invalid-email",  # Invalid format
  "password": "123",  # Too short
  "firstName": "",  # Empty
  "lastName": "Doe"
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "error": "VALIDATION_ERROR",
  "timestamp": "2024-01-15T10:35:22",
  "path": "/register",
  "fieldErrors": [
    {
      "field": "username",
      "message": "Username must be between 3 and 50 characters",
      "rejectedValue": "ab"
    },
    {
      "field": "email",
      "message": "Please provide a valid email address",
      "rejectedValue": "invalid-email"
    },
    {
      "field": "password",
      "message": "Password must be at least 6 characters long",
      "rejectedValue": "123"
    },
    {
      "field": "firstName",
      "message": "First name is required",
      "rejectedValue": ""
    }
  ]
}
```

### Example 2: Event Not Found

**Request:**
```bash
GET /events/{invalid-uuid}
```

**Response (404 Not Found):**
```json
{
  "status": 404,
  "message": "Event not found with ID: invalid-uuid",
  "error": "RESOURCE_NOT_FOUND",
  "timestamp": "2024-01-15T10:36:15",
  "path": "/events/invalid-uuid"
}
```

### Example 3: Unauthorized Access

**Request:**
```bash
POST /events/edit/{event-id}  # Trying to edit event not owned by user
```

**Response (403 Forbidden):**
```json
{
  "status": 403,
  "message": "You are not authorized to modify this event",
  "error": "UNAUTHORIZED_ACCESS",
  "timestamp": "2024-01-15T10:37:05",
  "path": "/events/edit/123e4567-e89b-12d3-a456-426614174000"
}
```

### Example 4: Business Rule Violation

**Request:**
```bash
POST /registrations/book
params: {
  "eventId": "123e4567-e89b-12d3-a456-426614174000",
  "attendeesCount": 10  # Exceeds max of 5
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "You cannot exceed buying 5 tickets per transaction",
  "error": "BUSINESS_LOGIC_ERROR",
  "timestamp": "2024-01-15T10:38:30",
  "path": "/registrations/book"
}
```

## Controller Integration

### Example Controller: `EventController`

```java
@PostMapping("/create")
public String createEvent(
    @Valid @ModelAttribute("eventDto") EventCreateUpdateDto dto,
    BindingResult bindingResult,
    @AuthenticationPrincipal AuthenticationUserDetails principal) {
    
    // Step 1: @Valid annotation automatically validates the DTO
    if (bindingResult.hasErrors()) {
        return "events/create";  // Return form with error messages
    }
    
    // Step 2: Service layer validation
    try {
        eventService.createEvent(dto, principal.getUsername());
        log.info("✅ Event created successfully");
    } catch (BusinessException ex) {
        log.warn("⚠️ Business rule violation: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "events/create";
    } catch (UnauthorizedAccessException ex) {
        log.warn("🚫 Unauthorized: {}", ex.getMessage());
        return "redirect:/events/dashboard?error=Unauthorized";
    }
    
    return "redirect:/events/dashboard";
}
```

### REST API Example: Microservice `NotificationController`

```java
@PostMapping("/broadcast")
public ResponseEntity<BroadcastAnnouncementResponse> broadcastAnnouncement(
    @Valid @RequestBody BroadcastAnnouncementRequest request) {
    
    // Step 1: @Valid validates the request body
    // Step 2: GlobalExceptionHandler automatically catches validation errors
    
    try {
        BroadcastAnnouncementResponse response = 
            notificationService.broadcastAnnouncement(request);
        log.info("✅ Announcement broadcast completed");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (ValidationException ex) {
        // Returns 400 via GlobalExceptionHandler
        log.warn("❌ Validation error: {}", ex.getMessage());
        throw ex;
    }
}
```

## Logging Integration

All validation and error handling includes comprehensive logging with emoji markers:

- **📝**: Information message
- **✅**: Success message
- **❌**: Validation/error message
- **⚠️**: Warning message
- **🚫**: Authorization/access denied
- **🔍**: Resource not found
- **💥**: Unexpected error
- **📋**: Debug information

Example logs:
```
2024-01-15 10:30:45 INFO: ✅ User registration successful for: john@example.com
2024-01-15 10:31:10 WARN: ⚠️ Booking validation failed: Capacity exceeded
2024-01-15 10:31:45 ERROR: 💥 Unexpected error in microservice - NullPointerException
```

## Testing Validation and Error Handling

### Manual Testing Steps:

1. **Test DTO Validation:**
   - Submit registration with missing fields
   - Submit event with invalid dates
   - Verify field-level error responses

2. **Test Resource Not Found:**
   - Access non-existent event/user
   - Verify 404 response with meaningful message

3. **Test Authorization:**
   - Try to edit event as non-owner
   - Try to delete user as non-admin
   - Verify 403 response

4. **Test Business Rules:**
   - Book more than 5 tickets
   - Register twice for same event
   - Verify 400 response with rule explanation

## Summary

This validation and error handling implementation ensures:

✅ **Consistent error responses** across all endpoints
✅ **Multi-layer validation** (DTO, entity, service)
✅ **Meaningful error messages** for developers and end-users
✅ **No application crashes** or white-label error pages
✅ **Comprehensive logging** for debugging and monitoring
✅ **Security-focused** with proper authorization checks
✅ **Production-ready** with proper HTTP status codes
