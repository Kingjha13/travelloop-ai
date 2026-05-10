# TravelLoop 🌍✈️

AI-powered travel planning application built with **Jetpack Compose**, **Ktor**, and **PostgreSQL**.

TravelLoop helps users create trips, build itineraries, manage budgets, organize packing lists, save notes, and explore public travel plans in one modern mobile application.

---

## 🚀 Features

### 🔐 Authentication
- User Registration
- Secure Login
- JWT Authentication

### 🧳 Trip Management
- Create Trips
- Public / Private Trips
- Trip Dashboard
- Trip Details

### 📍 Itinerary Builder
- Add Stops
- Add Activities
- Organize Travel Schedule

### 💰 Budget Tracking
- Add Expenses
- Budget Summary
- Expense Categories

### 🎒 Packing Lists
- Add Packing Items
- Mark Packed / Unpacked

### 📝 Travel Notes
- Save Important Notes
- Trip-specific Notes

### 🌐 Public Exploration
- Explore Public Trips
- View Shared Travel Plans

---

## 🛠 Tech Stack

### Frontend
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Retrofit
- ViewModel
- StateFlow

### Backend
- Kotlin
- Ktor Server
- PostgreSQL
- Exposed ORM
- JWT Authentication
- HikariCP

---

## 📱 Android Screens

- Splash Screen
- Login Screen
- Register Screen
- Dashboard
- My Trips
- Create Trip
- Trip Details
- Itinerary Builder
- Budget Manager
- Packing List
- Notes Screen
- Public Trips
- Profile Screen

---

## 🗂 Project Structure

```text
TravelLoop/
│
├── app/                     # Android frontend
├── travelloop-backend/      # Ktor backend
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🔥 API Modules

### Authentication
```text
/api/auth
```

### Trips
```text
/api/trips
```

### Stops
```text
/api/trips/{tripId}/stops
```

### Activities
```text
/api/stops/{stopId}/activities
```

### Budget
```text
/api/trips/{tripId}/budget
```

### Packing
```text
/api/trips/{tripId}/packing
```

### Notes
```text
/api/trips/{tripId}/notes
```

---

## 🧠 Key Highlights

- Modern Material 3 UI
- Clean Architecture
- REST API Integration
- Real-time State Management
- Modular Backend Structure
- Responsive Compose Design
- Secure JWT Authentication
- PostgreSQL Database Integration

---

## 📸 Future Enhancements

- Google Maps Integration
- AI Trip Suggestions
- Offline Support
- Firebase Notifications
- Image Uploads
- Multi-language Support
- Collaborative Trip Planning

---

## 👨‍💻 Developer

**Avanish Jha**

GitHub: [Kingjha13](https://github.com/Kingjha13)

---

# ⭐ TravelLoop

**Plan smarter. Travel better.**