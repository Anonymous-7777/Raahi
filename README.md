# प्रोजेक्ट राही (Project Raahi)
### A Smart Tourist Safety & Convenience Ecosystem

![Hackathon](https://img.shields.io/badge/Hackathon-Submission-blue.svg)

A comprehensive platform using AI, Blockchain, and IoT to provide a proactive safety net for visitors in regions like the Northeast. This project is a submission for the [Name of Hackathon].

---

## 🎥 Demo Video

**Our entire concept is demonstrated in this 90-second video.**

[**Watch the Demo Video on YouTube**](https://link-to-your-video.com)

---

## 🚀 Live Demo & APK

Access the live demo of the authorities' dashboard and download the Android APK using the links below.

[![Live Dashboard](https://img.shields.io/badge/Live-Dashboard-brightgreen?style=for-the-badge&logo=vercel)](https://your-deployed-link.vercel.app)
[![Download APK](https://img.shields.io/badge/Download-Android_APK-blue?style=for-the-badge&logo=android)](https://github.com/your-username/raahi-project/releases/download/v1.0/raahi-app.apk)

---

## ✨ Key Features

* **🛡️ Blockchain-Verified Digital ID:** A secure, tamper-proof ID is issued on the Polygon testnet, linked to a physical **NFC wristband** for on-ground verification.
* **📱 Smart Mobile App:** A native Android app for tourists featuring a one-touch **Panic Button** and a real-time location service.
* **🤖 AI "Watchdog" (Vision):** A proactive anomaly detection engine to flag distress signals even when the panic button isn't pressed. For the demo, this is simulated.
* **🗺️ Real-time Authorities' Dashboard:** A web-based command center for authorities to see live tourist locations and receive instant emergency alerts.

---

## 🛠️ Tech Stack

This project uses a modern, hybrid architecture to ensure performance and scalability.

* **Frontend (Mobile):** Native Android (Kotlin with Jetpack Compose, Google Maps SDK)
* **Frontend (Web):** React.js with TypeScript, Mapbox, Socket.IO Client
* **Backend (Custom API):** Node.js with Express.js & Socket.IO
* **Backend (BaaS):** Supabase (for Auth & Storage)
* **Database:** Supabase Managed PostgreSQL + PostGIS extension
* **Blockchain:** Solidity Smart Contract on Polygon (Layer 2)
* **Physical Integration:** NFC Wristbands
* **Version Control:** Git & GitHub

---

## ⚙️ Getting Started

To run this project locally, follow these steps.

### Prerequisites
* Node.js (v18 or later)
* Android Studio (latest version)
* A Supabase account (free tier)

### Backend & Dashboard Setup
1.  **Clone the repository:** `git clone https://github.com/your-username/raahi-project.git`
2.  **Set up Supabase:** Create a `.env` file in the `backend` folder with your Supabase URL and Anon Key.
3.  **Install dependencies:** `cd web-dashboard && npm install` and `cd backend && npm install`.
4.  **Run the backend:** `cd backend && npm start`.
5.  **Run the dashboard:** `cd web-dashboard && npm start`.

### Mobile App Setup
1.  Open the `/mobile-app` folder in Android Studio.
2.  Let Gradle sync the dependencies.
3.  Update the backend URL in the networking configuration file.
4.  Run the app on an emulator or a physical Android device.

---

## 👥 The Team

* **Frontend (Web) Lead:** [Name of Person 1]
* **Backend Lead:** [Name of Person 2]
* **API Lead:** [Name of Person 3]
* **Database Lead:** [Name of Person 4]
* **Presentation Lead:** [Name of Person 5]
* **App & Web3 Lead:** [Name of Person 6]

---

## 🙏 Acknowledgments & Attributions

* The logo, monument images, and other visual assets used in this project were generated using AI image generation tools.
* This project was built with the help of the Gemini agent in Android Studio for code generation and architectural guidance.
