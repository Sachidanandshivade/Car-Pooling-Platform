# 🚗 CarPooling Platform

A full-stack car pooling application built with **Spring Boot** (backend) and **React + Vite + Tailwind CSS** (frontend).

---

## 📁 Project Structure

```
carpooling-project/
├── backend/          ← Spring Boot app
└── frontend/         ← React + Vite app
```

---

## ⚙️ Backend Setup (Spring Boot)

### Prerequisites
- Java 17+
- Maven
- MySQL running locally

### Steps

1. **Create MySQL database** (auto-created if it doesn't exist):
   ```sql
   CREATE DATABASE carpooling_db;
   ```

2. **Update credentials** in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```

3. **Run the backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   The server starts at `http://localhost:8080`

---

## 🌐 Frontend Setup (React)

### Prerequisites
- Node.js 18+

### Steps

1. **Install dependencies**:
   ```bash
   cd frontend
   npm install
   ```

2. **Start the dev server**:
   ```bash
   npm run dev
   ```
   Opens at `http://localhost:5173`

---

## 🔑 API Endpoints

### Auth
| Method | URL | Description | Auth Required |
|--------|-----|-------------|---------------|
| POST | `/auth/register` | Register user | No |
| POST | `/auth/login` | Login & get JWT | No |

### Rides
| Method | URL | Description | Role |
|--------|-----|-------------|------|
| GET | `/rides` | Get all rides | Any |
| GET | `/rides/search?source=&destination=` | Search rides | Any |
| PUT | `/rides/{id}/start` | Start a ride | DRIVER |
| PUT | `/rides/{id}/complete` | Complete a ride | DRIVER |
| PUT | `/rides/{id}/cancel` | Cancel a ride | DRIVER/PASSENGER |

### Ride Requests
| Method | URL | Description | Role |
|--------|-----|-------------|------|
| POST | `/requests` | Create ride request | PASSENGER |
| POST | `/requests/{id}/accept` | Accept a request | DRIVER |
| GET | `/requests/pending?source=` | Get pending requests by area | DRIVER |

---

## 🧭 Frontend Pages

| Route | Page | Who |
|-------|------|-----|
| `/` | Login | All |
| `/register` | Register | All |
| `/dashboard` | Dashboard + all rides | All |
| `/create-request` | Request a ride | PASSENGER |
| `/pending-requests` | View & accept requests | DRIVER |
| `/my-rides` | Manage your rides | All |

---

## 🔐 User Roles

- **PASSENGER** — Can request rides, view their rides, cancel accepted rides
- **DRIVER** — Can view pending requests by area, accept them, start/complete/cancel rides

---

## 🗒️ Notes

- **Google Maps API**: `LocationService.java` calls Google Geocoding API. Replace `YOUR_GOOGLE_API_KEY` with a real key, or the `/requests` POST endpoint will fail. You can stub out `getCoordinates()` to return `{0.0, 0.0}` for local testing.
- JWT tokens expire in **24 hours**.
- The frontend reads the `role` claim from the JWT to show role-specific UI.
