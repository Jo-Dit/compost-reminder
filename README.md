# Compost Shift Reminder Web Application
Project Summary
- Problem: People are forgetting to put out the composting bins in the morning. While they haven’t forgotten to take it out in the afternoon yet, it would be devastating to the clubs’ future if they did. 
-  Solution: Create a web application that sends automated email and SMS reminders to high school composting club members about their upcoming shifts

Usage
- Students sign up by entering their email or phone number, selecting their shift (morning or afternoon), and choosing their day of the week. The app automatically sends them a reminder on that day — no login required.

Morning shift (put bins out) → reminder at 7:00 AM Eastern
Afternoon shift (collect bins) → reminder at 12:00 PM Eastern

Sign up page:

<img width="364.5" height="406.5" alt="image" src="https://github.com/user-attachments/assets/2a275ebf-ad31-42f1-b535-f60926a6b59f" /> <img width="209.5" height="262.5" alt="image" src="https://github.com/user-attachments/assets/c1714a05-3b5f-4fef-952a-26405c552559" />


Tech Stack
- Backend: Java 17, Spring Boot 3.2
- Database: PostgreSQL
- Email: SendGrid API
- SMS: Twilio
- Frontend: Thymeleaf, HTML/CSS
- Hosting: Railway
- CI/CD: GitHub → Railway (auto-deploy on push)
  
