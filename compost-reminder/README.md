# 🌿 Compost Shift Reminder App

A simple Spring Boot application that sends email and SMS reminders to students signed up for morning or afternoon composting shifts.

---

## How It Works

1. A student visits the web page
2. They enter their **email** or **phone number** (or both)
3. They choose **Morning** (put bins out) or **Afternoon** (collect bins)
4. They pick their **day of the week**
5. On that day, they get a reminder automatically:
   - **Morning shift** → notified at **7:00 AM**
   - **Afternoon shift** → notified at **12:00 PM**

No login needed. No accounts. Just sign up and done.

---

## Project Structure

```
compost-reminder/
├── src/main/java/com/compost/
│   ├── CompostReminderApplication.java   ← Entry point
│   ├── controller/
│   │   ├── SignupController.java         ← Web form (Thymeleaf)
│   │   └── ApiController.java           ← REST API endpoints
│   ├── model/
│   │   ├── ShiftSignup.java             ← Database entity
│   │   └── SignupRequest.java           ← Form DTO
│   ├── repository/
│   │   └── ShiftSignupRepository.java   ← Data access
│   ├── service/
│   │   ├── SignupService.java           ← Signup logic & validation
│   │   └── NotificationService.java    ← Email + SMS sending
│   └── scheduler/
│       └── ReminderScheduler.java      ← Timed reminder jobs
├── src/main/resources/
│   ├── application.properties           ← All configuration
│   └── templates/
│       ├── index.html                   ← Signup form
│       └── success.html                 ← Confirmation page
└── pom.xml
```

---

## Setup Instructions

### 1. Prerequisites
- Java 17+
- Maven 3.8+

### 2. Configure Email (Gmail)

In `application.properties`:
```properties
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
```

> **Gmail setup:** Go to your Google Account → Security → 2-Step Verification → App passwords → Generate one for "Mail". Use that 16-character password here.

### 3. Configure SMS via Twilio (Optional)

Sign up at [twilio.com](https://twilio.com) (free trial available). Then:
```properties
twilio.account-sid=ACxxxxxxxxxxxxxxxxxxxxxxxxxxxx
twilio.auth-token=your_auth_token
twilio.from-number=+15551234567
```

If you skip Twilio, email-only reminders still work fine.

### 4. Run the App

```bash
mvn spring-boot:run
```

Then visit: **http://localhost:8080**

---

## Changing Reminder Times

Edit `application.properties`:
```properties
# Fires at 7:00 AM every day
notification.morning.cron=0 0 7 * * *

# Fires at 12:00 PM every day
notification.afternoon.cron=0 0 12 * * *
```

Cron format: `seconds minutes hours day-of-month month day-of-week`

---

## REST API

| Method | Endpoint           | Description              |
|--------|--------------------|--------------------------|
| POST   | `/api/signup`      | Create a new signup      |
| GET    | `/api/signups`     | List all signups (admin) |
| DELETE | `/api/signups/{id}`| Cancel a signup          |

**Example POST body:**
```json
{
  "email": "alex@school.edu",
  "phone": "5551234567",
  "shiftType": "MORNING",
  "dayOfWeek": "TUESDAY"
}
```

---

## Database

The app uses an **H2 in-memory database** by default — no setup required. Data resets when the app restarts.

To view the database while running:
- Visit: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:compostdb`
- Username: `sa` | Password: *(empty)*

### Switch to PostgreSQL (for production)

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/compostdb
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

---

## Customizing Messages

Edit these lines in `application.properties`:
```properties
notification.morning.message=Good morning! Don't forget to PUT OUT the compost bins...
notification.afternoon.message=Heads up! It's time to COLLECT the compost bins...
notification.club-name=Green Grounds Composting Club
```
