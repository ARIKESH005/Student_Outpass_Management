# Setup Guide: Student Outpass Management System

Follow these steps to run the complete Full Stack application.

## 1. Database Setup

1. Make sure you have MySQL Server installed and running on port `3306`.
2. The application is configured to automatically create the `outpass_management_db` database if it does not exist, using the credentials `root` / `Admin@123`. 
3. If the automatic creation fails, please manually create the database using your MySQL Client (like MySQL Workbench):
   ```sql
   CREATE DATABASE outpass_management_db;
   ```

## 2. Backend Setup (Spring Boot)

You can run the backend using any standard Java IDE (IntelliJ IDEA, Eclipse, VS Code).

### Using IntelliJ IDEA or Eclipse:
1. Open your IDE.
2. Select **Open** or **Import Project**.
3. Navigate to `student-outpass-management` and select the `pom.xml` file.
4. Wait for the IDE to download all Maven dependencies.
5. Run the `StudentOutpassManagementApplication.java` class.
6. The server will start on port `8081`.
7. **Note:** A default admin user (`admin` / `admin123`) is automatically inserted when the app starts.

## 3. Frontend Setup

The frontend uses pure HTML, CSS, and JS and requires no build tools.

1. Navigate to the `frontend` folder.
2. You can simply open `index.html` in any modern web browser.
3. For the best experience and to avoid any potential CORS/File protocol issues, you can serve the frontend using a simple static server like VS Code Live Server, or Python:
   ```bash
   # If you have python installed:
   cd frontend
   python -m http.server 8000
   ```
4. Access the frontend at `http://localhost:8000`.

## 4. Default Credentials

- **Admin User**: `admin` / `admin123`
- **Student User**: Register a new student via the Register page on `index.html`.
