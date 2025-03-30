<!-- TOC -->
  * [# Community Neighborhood Software Application](#-community-neighborhood-software-application)
  * [## 📌 Project Overview](#--project-overview)
  * [🎯 Key Features](#-key-features)
    * [1️⃣ Help Requests](#1-help-requests)
    * [2️⃣ Parking Rentals](#2-parking-rentals)
    * [3️⃣ Booking Public Places](#3-booking-public-places)
  * [🏛 System Roles](#-system-roles)
  * [🛠 Tech Stack](#-tech-stack)
  * [------------------------------------------------------------------------------------------------------------](#------------------------------------------------------------------------------------------------------------)
  * [## Dependencies](#-dependencies)
  * [Backend Dependencies](#backend-dependencies)
    * [Required Dependencies](#required-dependencies)
    * [Installation Command](#installation-command)
  * [Frontend Dependencies](#frontend-dependencies)
    * [Runtime Dependencies](#runtime-dependencies)
    * [Development Dependencies](#development-dependencies)
    * [Installation Command](#installation-command-1)
  * [This command installs all dependencies listed in `package.json`, including both runtime and development dependencies. The exact versions are locked in the `package-lock.json` file to ensure consistent builds.](#this-command-installs-all-dependencies-listed-in-packagejson-including-both-runtime-and-development-dependencies-the-exact-versions-are-locked-in-the-package-lockjson-file-to-ensure-consistent-builds)
  * [Notes](#notes)
    * [Backend:](#backend)
    * [Frontend:](#frontend)
  * [------------------------------------------------------------------------------------------------------------](#-------------------------------------------------------------------------------------------------------------1)
  * [Below are detailed build and deployment instructions for an application consisting of a backend built with Java and Spring Boot, and a frontend built with React. These instructions assume you have the necessary tools installed and guide you through the process of building and deploying the application from scratch.](#below-are-detailed-build-and-deployment-instructions-for-an-application-consisting-of-a-backend-built-with-java-and-spring-boot-and-a-frontend-built-with-react-these-instructions-assume-you-have-the-necessary-tools-installed-and-guide-you-through-the-process-of-building-and-deploying-the-application-from-scratch)
  * [Build and Deployment Instructions](#build-and-deployment-instructions)
    * [Prerequisites](#prerequisites)
    * [Backend: Build and Deployment](#backend-build-and-deployment)
      * [1. Clone the Repository (if applicable)](#1-clone-the-repository-if-applicable)
      * [2. Configure the Database](#2-configure-the-database)
      * [3. Build the Backend](#3-build-the-backend)
      * [4. Run the Backend](#4-run-the-backend)
    * [Frontend: Build and Deployment](#frontend-build-and-deployment)
      * [1. Navigate to the Frontend Directory](#1-navigate-to-the-frontend-directory)
      * [2. Install Dependencies](#2-install-dependencies)
      * [3. Configure Environment Variables](#3-configure-environment-variables)
      * [4. Build the Frontend](#4-build-the-frontend)
      * [5. Serve the Frontend Locally](#5-serve-the-frontend-locally)
      * [6. Deploy to a Server](#6-deploy-to-a-server)
    * [Full Deployment Workflow](#full-deployment-workflow)
    * [Additional Notes](#additional-notes)
  * [---](#---)
  * [### Features & Use Case Scenarios](#-features--use-case-scenarios)
    * [1. User Registration & Authentication](#1-user-registration--authentication)
    * [Use Case: Login & Role-Based Access](#use-case-login--role-based-access)
    * [2. Community Management](#2-community-management)
    * [Use Case: Create a New Community](#use-case-create-a-new-community)
    * [Use Case: Join an Existing Community](#use-case-join-an-existing-community)
    * [Use Case: Create a Help Request](#use-case-create-a-help-request)
    * [Use Case: Edit or Delete a Help Request](#use-case-edit-or-delete-a-help-request)
    * [4. Parking Rentals](#4-parking-rentals)
    * [Use Case: Rent Out a Parking Spot](#use-case-rent-out-a-parking-spot)
    * [Use Case: Book a Parking Spot](#use-case-book-a-parking-spot)
    * [5. Public Place Bookings](#5-public-place-bookings)
    * [Use Case: Book a Community Facility](#use-case-book-a-community-facility)
    * [6. Community Amenities](#6-community-amenities)
    * [Use Case: Manage Amenities](#use-case-manage-amenities)
    * [Use Case: Book an Amenity](#use-case-book-an-amenity)
    * [Admin Dashboard](#admin-dashboard)
    * [Community Manager Dashboard](#community-manager-dashboard)
    * [Resident Dashboard](#resident-dashboard)
<!-- TOC -->



# Community Neighborhood Software Application
------------------------------------------------------------------------------------------------------------
## 📌 Project Overview
------------------------------------------------------------------------------------------------------------
The **Community Neighborhood Software Application** is a platform designed to foster stronger neighborhood connections by providing essential community-driven features. This application enables residents to engage in local activities, request assistance, manage parking rentals, and book public spaces efficiently.

## 🎯 Key Features

### 1️⃣ Help Requests
- Community members can request and offer help for various tasks.
- Categories include emergency assistance, household help, and more.
- Users can respond to requests and track interactions.

### 2️⃣ Parking Rentals
- Residents can rent out their available parking spaces.
- A booking system ensures smooth transactions between users.
- Payment and rental history tracking features.

### 3️⃣ Booking Public Places
- Allows residents to reserve community halls, parks, or other public areas.
- Admins manage availability and approval processes.
- Supports event planning and group activities.

## 🏛 System Roles
1. **Admin**: Manages the overall platform, neighborhoods, and approvals.
2. **Community Manager**: Oversees a specific neighborhood, approves member requests, and ensures smooth operations.
3. **Community Member**: Can request help, rent parking spaces, book public areas, and interact with other members.

## 🛠 Tech Stack
- **Frontend**: React (Node.js-based)
- **Backend**: Spring Boot (Java)
- **Database**: PostgreSQL (with Hibernate for ORM)
- **Authentication**: Spring Security with JWT
- **Containerization**: Docker (for deployment and microservices management)
- **Project Management**: GitLab
- 
------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------
## Dependencies
------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------
The application consists of a backend built with **Java and Spring Boot**, and a frontend built with **React**. Below are the dependencies required for each part, along with the commands to install or configure them.

---

## Backend Dependencies
The backend dependencies are managed by **Gradle** and listed in the `build.gradle` file. Many dependencies are **Spring Boot starters**, and their versions are managed by **Spring Boot 3.4.2** unless explicitly specified.

### Required Dependencies

| Dependency | Version | Purpose |
|------------|---------|:-------:|
| `spring-boot-starter-web` | * | Web application development (REST APIs, MVC) |
| `spring-boot-starter-security` | * | Security features (authentication, authorization) |
| `spring-boot-starter-data-jpa` | * | JPA for database access and ORM |
| `mysql-connector-j` | 8.3.0 | MySQL database connector |
| `h2` | * | In-memory database for testing (runtime only) |
| `jjwt-api` | 0.11.5 | JWT token handling API |
| `jjwt-impl` | 0.11.5 | JWT implementation |
| `jjwt-jackson` | 0.11.5 | JWT Jackson support for JSON parsing |
| `lombok` | * | Boilerplate code reduction (compile-time only) |
| `hibernate-validator` | * | Bean validation for input data |
| `spring-boot-starter-mail` | * | Email sending capabilities |
| `spring-boot-starter-test` | * | Testing support (includes JUnit, Mockito, etc.) |
| `junit-jupiter-api` | 5.11.3 | JUnit 5 API for writing tests |
| `junit-jupiter-engine` | 5.11.3 | JUnit 5 engine for running tests |
| `mockito-core` | 4.0.0 | Mocking framework for unit tests |
| `mockito-junit-jupiter` | 4.0.0 | Mockito integration with JUnit 5 |
| `commons-lang3` | 3.12.0 | Apache Commons Lang utilities |
| `spring-security-test` | * | Spring Security testing support |

> *Versions managed by Spring Boot 3.4.2*

### Installation Command

To install all backend dependencies, ensure you have **Gradle** installed, then run the following command in the backend directory (where `build.gradle` is located):
```bash
./gradlew build
```
This command downloads all dependencies specified in `build.gradle` and builds the application. Gradle automatically resolves and installs the dependencies when you execute any build task.

---

## Frontend Dependencies
The frontend dependencies are managed by **npm** and listed in the `package.json` file. They are divided into **runtime dependencies** (required for building and deploying the application) and **development dependencies** (used during development and testing).

### Runtime Dependencies
| Dependency | Version | Purpose |
|------------|---------|---------|
| `@testing-library/jest-dom` | ^5.17.0 | Jest DOM testing utilities |
| `@testing-library/react` | ^13.4.0 | React testing library |
| `@testing-library/user-event` | ^13.5.0 | User event simulation for testing |
| `axios` | 1.7.8 | HTTP client for API requests |
| `cra-template` | 1.2.0 | Create React App template |
| `formik` | ^2.4.6 | Form handling library |
| `lucide-react` | ^0.474.0 | Icon library for React |
| `react` | ^18.0.0 | React library for UI components |
| `react-dom` | ^18.0.0 | React DOM rendering |
| `react-icons` | ^5.5.0 | Icon components for React |
| `react-router-dom` | ^6.30.0 | Routing for React applications |
| `react-scripts` | ^5.0.1 | Scripts and configuration for Create React App |
| `web-vitals` | ^2.1.4 | Web vitals reporting |
| `yup` | ^1.6.1 | Schema validation for forms |

### Development Dependencies
| Dependency | Version | Purpose |
|------------|---------|---------|
| `@babel/core` | ^7.26.9 | Babel core for transpiling JavaScript |
| `@babel/plugin-proposal-private-property-in-object` | ^7.21.11 | Babel plugin for private properties |
| `@babel/preset-env` | ^7.26.9 | Babel preset for environment compatibility |
| `@babel/preset-react` | ^7.14.5 | Babel preset for React |
| `@testing-library/react` | ^11.2.7 | Older version of React testing library |
| `autoprefixer` | ^10.4.20 | PostCSS plugin for vendor prefixes |
| `babel-jest` | ^27.5.1 | Babel integration for Jest |
| `jest` | ^27.0.6 | Testing framework |
| `postcss` | ^8.5.1 | CSS post-processing |
| `tailwindcss` | ^3.4.17 | Utility-first CSS framework |

### Installation Command
To install all frontend dependencies, navigate to the frontend directory (where `package.json` is located) and run one of the following commands:

```bash
npm install
```
or, if you prefer using Yarn:
```bash
yarn install
```

This command installs all dependencies listed in `package.json`, including both runtime and development dependencies. The exact versions are locked in the `package-lock.json` file to ensure consistent builds.
---

## Notes
### Backend:
- The full list of dependencies with their exact versions (where specified) can be found in `build.gradle`.
- Gradle manages transitive dependencies and version resolution based on **Spring Boot 3.4.2**.
### Frontend:
- The full list of dependencies with their specified versions is in `package.json`.
- Exact versions are locked in `package-lock.json` to ensure consistent builds.
- **Note:** `@testing-library/react` appears in both runtime and development dependencies with different versions (`^13.4.0` and `^11.2.7`). This may be intentional for different use cases or an oversight.


------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------
Below are detailed build and deployment instructions for an application consisting of a backend built with Java and Spring Boot, and a frontend built with React. These instructions assume you have the necessary tools installed and guide you through the process of building and deploying the application from scratch.
------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------
## Build and Deployment Instructions

This application has two main components: a backend (Java/Spring Boot) and a frontend (React). Follow the steps below to build and deploy each part.

### Prerequisites

Before you begin, ensure the following tools are installed on your system:

- **Java 17**: Required for the backend. Download from [Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or use a package manager like `sdkman` or `homebrew`.
- **Gradle**: Used to build the backend. Install from [Gradle](https://gradle.org/install/) or use the included Gradle wrapper.
- **Node.js and npm/Yarn**: Required for the frontend. Install Node.js (includes npm) from [Node.js](https://nodejs.org/). Optionally, install Yarn from [Yarn](https://yarnpkg.com/).
- **MySQL**: Needed for the backend database. Install from [MySQL](https://dev.mysql.com/downloads/) or use a cloud-based MySQL service.
- **Git**: Required to clone the repository (if hosted remotely).

---

### Backend: Build and Deployment

The backend is a Spring Boot application managed with Gradle. Follow these steps to set it up.

#### 1. Clone the Repository (if applicable)

If the project is in a Git repository, clone it to your local machine:

```bash
git clone <repository-url>
cd <backend-directory>
```

#### 2. Configure the Database

Ensure MySQL is running and create a database for the application:

```sql
CREATE DATABASE neighbourly;
```

Edit the `application.properties` or `application.yml` file in `src/main/resources` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/neighbourly
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.jpa.hibernate.ddl-auto=update
```

#### 3. Build the Backend

Build the application using Gradle to download dependencies, compile the code, and create a JAR file:

```bash
./gradlew build
```

The JAR file will be generated in `build/libs/app.jar`.

#### 4. Run the Backend

Run the backend locally using one of these methods:

- **Using Gradle**:

```bash
./gradlew bootRun
```

- **Using the JAR file**:

```bash
java -jar build/libs/app.jar
```

The backend will start on `http://localhost:8080` by default.

---

### Frontend: Build and Deployment
The frontend is a React application managed with npm or Yarn. Follow these steps to set it up.

#### 1. Navigate to the Frontend Directory
If the frontend is in a subdirectory (e.g., `frontend/`), move to it:

```bash
cd frontend
```

#### 2. Install Dependencies
Install the required dependencies:

```bash
npm install
```
or
```bash
yarn install
```

#### 3. Configure Environment Variables
If the frontend connects to the backend, set the API URL in a `.env` file in the frontend directory:

```env
REACT_APP_API_URL=http://localhost:8080
```

Update the URL to match your production backend address if needed.

#### 4. Build the Frontend
Build the React app for production:

```bash
npm run build
```
or
```bash
yarn build
```

This creates a `build/` directory with optimized files.

#### 5. Serve the Frontend Locally
Test the build locally using a static server:

```bash
npx serve -s build
```

The frontend will be available at `http://localhost:5000` by default.

#### 6. Deploy to a Server
To deploy the frontend to a production server:

- Copy the `build/` directory contents to the server’s web root or a static hosting service (e.g., Netlify, Vercel, AWS S3).
- Configure the server to serve the files. Example with Nginx:

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    location / {
        root /path/to/build;
        try_files $uri /index.html;
    }
}
```

- Ensure the `REACT_APP_API_URL` points to the deployed backend.

---

### Full Deployment Workflow
For a complete deployment:

1. **Backend**:
   - Build and deploy the JAR to the server.
   - Start the backend service.
   - Verify the database is configured and accessible.

2. **Frontend**:
   - Build and deploy the `build/` directory to a web server or static host.
   - Update the `REACT_APP_API_URL` to the deployed backend URL.

3. **Testing**:
   - Test API connectivity between the frontend and backend.
   - Confirm all features work in the production environment.

---

### Additional Notes
- **Environment Variables**: Store sensitive data (e.g., database credentials) in environment variables or external configuration files.
- **HTTPS**: Use HTTPS in production for secure communication.
- **CORS**: If the frontend and backend are on different domains, configure CORS in the backend to allow frontend requests.

---
------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------
### Features & Use Case Scenarios
------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------
### 1. User Registration & Authentication
 Use Case: Register a New User
📌 Actors: New User
📌 Scenario:

1. User visits the registration page and enters their name, email, password.
2. The system checks if the email is already registered.

✅ If not registered → A new user is created, and an OTP (One-Time Password) is sent to the email for verification.
❌ If registered → The system displays an error: "User already exists."

The user enters the OTP to verify their account and completes the registration.

### Use Case: Login & Role-Based Access
📌 Actors: Registered User (Admin, Community Manager, or Resident)
📌 Scenario:

1. User enters email & password.
2. The system authenticates the credentials.

✅ Valid credentials → User is logged in and redirected based on their role:

* Admin → Admin Dashboard
* Community Manager → Community Manager Dashboard
* Resident → Resident Home Page

❌ Invalid credentials → Error message: "Invalid email or password."

### 2. Community Management
### Use Case: Create a New Community
📌 Actors: Registered User
📌 Scenario:

1. The user selects "Create Community" and provides community details (name, location, purpose).
2. The request is sent to the Admin for approval.
3. The Admin approves/rejects the request.

✅ Approval → Community is created, and the user becomes the Community Manager.
❌ Rejection → The user receives a notification with a reason.

### Use Case: Join an Existing Community
📌 Actors: Registered User
📌 Scenario:

* User searches for a community by name or location.
* The user submits a join request.
* The Community Manager approves/rejects the request.

✅ Approval → User joins the community and can access community features.
❌ Rejection → User receives a notification with a reason.

🆘 3. Help Requests
### Use Case: Create a Help Request
📌 Actors: Community Resident
📌 Scenario:

Resident selects "Request Help" and enters:

* Title
* Description
* Category (e.g., Tools, Emergency, Event Support)

* The request is posted in the community.
* Other residents see the request and can respond by commenting or contacting the requester.

### Use Case: Edit or Delete a Help Request
📌 Actors: Request Creator
📌 Scenario:

* The requester can edit or delete their help request anytime.
* The system updates or removes the request accordingly.

### 4. Parking Rentals
### Use Case: Rent Out a Parking Spot
📌 Actors: Community Resident
📌 Scenario:

A resident lists their available parking spot with:

* Spot Number
* Price
* Availability Duration

Other residents can view and book the parking spot.

### Use Case: Book a Parking Spot
📌 Actors: Community Resident
📌 Scenario:

1. A resident searches for an available parking spot.
2. They select a spot and proceed with the booking.
3. The owner of the spot approves/rejects the request.

✅ Approval → The system confirms the booking.

❌ Rejection → The user is notified.

### 5. Public Place Bookings
### Use Case: Book a Community Facility
📌 Actors: Community Resident
📌 Scenario:

Resident selects a public place (e.g., park, hall, playground).

They provide event details:

* Event Name
* Description
* Date & Time Slot
* Expected Attendees

The Community Manager approves/rejects the request.

✅ Approval → The booking is confirmed.
❌ Rejection → The user is notified.

### 6. Community Amenities
### Use Case: Manage Amenities
📌 Actors: Community Manager
📌 Scenario:

* Community Manager can add, update, or remove amenities (e.g., gym, swimming pool).
* Residents can view available amenities.

### Use Case: Book an Amenity
📌 Actors: Community Resident
📌 Scenario:

Resident selects an amenity and requests a booking.
The Community Manager reviews and approves/rejects the request.

🔑 7. Role-Based Dashboards
### Admin Dashboard
* View all active communities.
* Approve/reject community creation requests.

### Community Manager Dashboard
* Manage resident join requests.
* Approve/reject public place bookings.
* Manage amenities & help requests.

### Resident Dashboard
* View community news & discussions.
* Create/view help requests.
* Book parking spots & amenities.