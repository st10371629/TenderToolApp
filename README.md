# Tender Tool Mobile Application

## Table of Contents
1. [Overview](#overview)  
2. [App Information](#app-information)  
3. [Requirements](#requirements)  
   - [Functional Requirements](#functional-requirements)  
   - [Conditional Functionality](#conditional-functionality)  
4. [Installation and Setup](#installation-and-setup)  
   - [Prerequisites](#prerequisites)  
   - [Mobile App Setup](#mobile-app-setup)  
5. [API Architecture](#api-architecture)  
   - [Overview](#overview-1)  
   - [Data Flow](#data-flow)  
   - [Technologies](#technologies)  
6. [Security and Data Handling](#security-and-data-handling)  
7. [Demo Video](#demo-video)  

---

## Overview
Tender Tool is a mobile application designed to simplify how businesses, contractors, and suppliers discover and manage public tenders. It provides a centralised platform where users can search, filter, view, and track tenders directly from their Android devices. The app focuses on accessibility, usability, and data-driven insights while maintaining secure authentication and efficient data retrieval.

---

## App Information
- **Name:** Tender Tool  
- **Platform:** Android  
- **Architecture:** Client–Server (Android front-end with .NET Core backend)  
- **Target Audience:** Contractors, suppliers, and businesses seeking public tenders  
- **Core Features:** Tender discovery, filtering, analytics, saved tenders, and secure user authentication  

## Requirements

### Functional Requirements
- **Authentication:** Email and password login, registration, and Google SSO  
- **Confirmation Flow:** After registration, users verify their account with a confirmation code screen that also displays their username  
- **Biometric Login:** Fingerprint or facial recognition for faster, secure access  
- **Discovery Page:** Lists tenders in a searchable, filterable card layout  
- **Filter Overlay:** Enables filtering by category, location, or closing date  
- **Analytics:** Displays user activity metrics including daily active time  
- **Settings:** Language, notification, theme, and profile management  
- **Notifications:** Real-time updates for saved or relevant tenders  

### Conditional Functionality
To display accurate daily activity, users are asked to grant permission for usage tracking.  
If they choose not to, the daily active timer will be hidden while the rest of the analytics remain available.  
Users can later enable this feature by tapping the "Tap to grant access" message that appears on the analytics screen.

---

## Installation and Setup

### Prerequisites
- Android Studio (latest version)   

### Mobile App Setup
1. Clone the Android repository:
   
   ```bash
   git clone https://github.com/st10371629/TenderToolApp.git
3. Open the project in Android Studio.
4. Update ApiService.kt with the correct API base URL.
5. Sync Gradle and run the app on an emulator or physical device.

---

## API Architecture

### Overview
The Tender Tool backend is built using **ASP.NET Core Web API**. It handles authentication, tender retrieval, and watchlist management. Data is stored in **SQL Server** and exposed to the Android front end through RESTful endpoints.

### Data Flow
1. The Android client (Retrofit) sends HTTPS requests to the API.  
2. The API queries SQL Server and serialises data into JSON.  
3. The mobile app deserialises and displays this data in the user interface.  

### Technologies
- **Backend Language:** C# (.NET Core 8.0)  
- **Database:** SQL Server  
- **Frontend Integration:** Retrofit (Kotlin)  
- **Hosting:** AWS Lambda with API Gateway  
- **Security:** AWS Cognito and JWT Authentication

---

## Security and Data Handling

- All API requests are transmitted over **HTTPS** to ensure secure communication between the client and the server.
- Authentication and authorisation are managed through **AWS Cognito**, which issues **JWT tokens** for session validation and secure access control.
- Tokens are securely stored within the **Android Keystore**, preventing unauthorised access even if the device is compromised.
- Sensitive information, including passwords and personal identifiers, is **encrypted both in transit and at rest**, maintaining user privacy and data integrity.

---

## Demo Video
The demo video provides a walkthrough of the Tender Tool mobile application, showcasing the core user flow and functionality from authentication to analytics.  

### Demonstrated Screens:
- **Login and Registration:** Including biometric authentication and confirmation code verification.  
- **Discovery Page:** Tender cards with search and filter overlay functionality.  
- **Watchlist:** Managing and tracking saved tenders.  
- **Notifications:** Real-time tender updates and dismissible alerts.  
- **Analytics:** User activity dashboard with optional daily active timer.  
- **Settings:** Profile management, language and theme customisation, and logout.  

The video highlights how users can seamlessly navigate through the app, explore tenders, and personalise their experience while maintaining security and efficiency.  

🎥 **Watch the demo video here:** [Tender Tool Demo](https://youtu.be/FvvjMCP9NiY)
