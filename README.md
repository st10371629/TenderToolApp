# Tender Tool Mobile Application
**Current Release: v1.0.0**

## Table of Contents
1. [Overview](#overview)
2. [App Information](#app-information)
3. [Features](#features)
    - [Feature Highlights](#feature-highlights)
    - [Core Functionality](#core-functionality)
4. [Implementation Highlights](#implementation-highlights)
    - [Performance](#performance)
    - [UI/UX & Stability](#uiux--stability)
5. [Installation and Setup](#installation-and-setup)
    - [Prerequisites](#prerequisites)
    - [Mobile App Setup](#mobile-app-setup)
6. [API Architecture](#api-architecture)
    - [Overview](#overview-1)
    - [Data Flow](#data-flow)
    - [Technologies](#technologies)
7. [Security and Data Handling](#security-and-data-handling)
8. [Demo Video](#demo-video)

---

## Overview
Tender Tool is a mobile application designed to simplify how businesses, contractors, and suppliers discover and manage public tenders. It provides a centralised platform where users can search, filter, view, and track tenders directly from their Android devices.

The app focuses on accessibility and data-driven insights, transitioning from an initial prototype to a fully-featured, secure, and deployment-ready application. This version introduces the complete end-to-end user experience, from secure authentication to real-time tender discovery and analytics.

---

## App Information
- **Name:** Tender Tool
- **Platform:** Android
- **Architecture:** Client–Server (Android front-end with .NET Core backend)
- **Target Audience:** Contractors, suppliers, and businesses seeking public tenders

---

## Features

### Feature Highlights

The Tender Tool experience is defined by three core features:

* **Tender Discovery & Filtering:** The app's primary innovation is its unified discovery engine. Users can now seamlessly search and apply complex filters (by category, location, or closing date) from a single overlay. This transforms a time-consuming manual search into an efficient, targeted process.

* **Tender Tracking & Watchlist:** Users can save tenders to a **Watchlist**, which is a separate screen that lists all saved items for easy reference. The app sends a push notification to confirm when a tender has been successfully added to the list.

* **User Activity Analytics:** An analytics dashboard shows the user's activity by displaying the total time spent in the app, the number of tenders viewed today, and a chart summarising the closing dates for tenders on the Watchlist (e.g., how many are closing soon versus later).

### Core Functionality

* **Full User Authentication:** A complete, secure registration and login system managed by AWS Cognito. This includes new user registration with email, a confirmation code screen, secure password login, and Google SSO.
* **Biometric Login:** For rapid, secure access, users can enable biometric authentication (fingerprint recognition) after their initial login, leveraging the AndroidX Biometric library.
* **Offline Sync for Watchlist:** The app uses a local **Room Database** to cache saved tenders, allowing users to view critical tender information even without an internet connection.
* **Real-time Push Notifications:** Integrated with **Firebase Cloud Messaging** to provide real-time alerts for updates on saved tenders.

---

## Implementation Highlights

### Performance
* **Pagination:** The Discovery Page now loads tenders in efficient batches of 10 as the user scrolls, significantly improving initial load time and reducing memory usage.

### UI/UX & Stability
* **Smooth Animations:** Jarring screen changes have been replaced with smooth, context-aware animations. Fragment transitions use bidirectional slides, and the login/register card animates its height to grow/shrink when switching forms.
* **Stable UI:** Fixed layout "jumping" by pinning key elements, ensuring a consistent UI. The tab indicator bar also dynamically animates its width and position to match its content.
* **Keyboard Handling:** Resolved all keyboard-related issues. The screen now correctly uses `adjustResize`, pushing input fields up so they are never obscured by the keyboard.
* **Robust Form Validation:** Added comprehensive validation to handle invalid inputs (e.g., empty fields, password mismatch) and display clear error messages without crashing.

---

## Installation and Setup

### Prerequisites
- Android Studio (latest version)

### Mobile App Setup
1. Clone the Android repository:
   ```bash
   git clone [https://github.com/st10371629/TenderToolApp.git](https://github.com/st10371629/TenderToolApp.git)
   ```
2. Open the project in Android Studio after cloning/downloading.
3. Update ApiService.kt with the correct API base URL.
4. Sync Gradle and run the app on an emulator or physical device.

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

---

## Security and Data Handling

- **Transport Layer Security:** All API requests are transmitted over **HTTPS** to ensure secure communication.
- **Authentication & Authorization:** Auth is managed through **AWS Cognito**, which issues **JWT tokens** for session validation and secure access control.
- **Secure Local Storage:** Tokens are securely stored within the **Android Keystore**, preventing unauthorized access.
- **Biometric Access:** User-facing biometric login (fingerprint recognition) provides rapid, secure access, leveraging the AndroidX Biometric library.
- **Data Encryption:** Sensitive information, including passwords and personal identifiers, is **encrypted both in transit and at rest**.

---

## Demo Video
The demo video provides a walkthrough of the Tender Tool mobile application, showcasing the core user flow and functionality from authentication to analytics.

### Demonstrated Screens:
- **Login and Registration:** Including biometric authentication and confirmation code verification.
- **Discovery Page:** Tender cards with search and filter overlay functionality.
- **Watchlist:** Managing and tracking saved tenders (including offline support).
- **Notifications:** Real-time tender updates.
- **Analytics:** User activity dashboard.
- **Settings:** Profile management, language and theme customisation, and logout.

🎥 **Watch the demo video here:** [Tender Tool Demo](https://youtu.be/FvvjMCP9NiY)
