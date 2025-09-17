<p align="center">
  <img <img width="114" height="876" alt="Image" src="https://github.com/user-attachments/assets/55143a93-2078-4766-a02f-64569d9982f4" />
</p>

<h1 align="center">Project Raahi (Version 1.0)</h1>

<p align="center">
  <strong>A Smart Tourist Safety & Convenience Ecosystem</strong>
  <br />
</p>

<p align="center">
  <a href="https://raahi1.vercel.app/"><img src="https://img.shields.io/badge/Live-Dashboard-brightgreen?style=for-the-badge&logo=firebase" alt="Live Dashboard"></a>
  <a href="https://github.com/Anonymous-7777/Raahi/blob/main/Raahi.apk"><img src="https://img.shields.io/badge/Download-Android_APK-blue?style=for-the-badge&logo=android" alt="Download APK"></a>
</p>

---

## 1. Overview

Project Raahi is built on a foundation of trust and security. It's a digital companion designed to give tourists the freedom to explore with confidence, knowing that a proactive safety net is always watching over them.

For authorities, it's a modern command center, providing the real-time situational awareness needed to protect visitors and manage the region's vibrant tourism economy effectively.
## 2. The Problem

In key tourism regions like Northeast India, ensuring visitor safety is paramount for economic growth. Traditional policing and manual tracking methods are often insufficient in remote areas, leading to slow emergency response times. There is a pressing need for a smart, technology-driven solution that provides real-time monitoring, rapid response, and secure identity verification, while respecting user privacy.

## 3. The Solution

Raahi is a robust digital ecosystem that addresses this challenge through four key pillars:

* **Blockchain-Verified Digital ID:** A secure, tamper-proof, and time-limited digital ID is issued on the Polygon network. This is linked to a physical **NFC wristband** for instant, on-the-ground verification by authorities, ensuring identity integrity.
* **Smart Mobile App:** A native Android application serves as the tourist's digital guardian. It features a one-touch **Panic Button**, proactive **Geo-fencing alerts** for high-risk zones, and a clear interface for accessing personal and emergency information.
* **Real-time Authorities' Dashboard:** A sophisticated web-based command center for police and tourism departments. It provides a live map with real-time tourist locations, an integrated alert management system, and data visualization tools for heatmaps and cluster analysis.
* **Proactive Alerting (Planned):** A planned AI "Watchdog" will provide anomaly detection to flag distress signals (e.g., signal loss, route deviation). The current version utilizes a robust rule-based engine.

## 4. Demo Video

* [**Watch the 90-Second Demo Video on YouTube**](https://link-to-your-video.com)

## Demo Credentials

To test the mobile app and web dashboard, please use the following pre-configured credentials for our test user
### 📱 For the Tourist Mobile App (Raahi)

* **Username:** `king@gmail.com`
* **Password:** `321654987`

### For the Authorities' Web Dashboard

* **Username:** `aadya@p.com`
* **Password:** `12345678`

  ### For the Issuer' Web Dashboard

* **Username:** `mussa@p.com`
* **Password:** `12345678`


## 5. Architecture Highlights

The platform is built on a modern, serverless architecture designed for massive scalability, real-time performance, and resilience.

* **Serverless-First Architecture:** The entire backend is built on Google's serverless platform, Firebase. This includes **Cloud Functions** for custom logic, **Firestore** for data, and **Authentication**, completely eliminating the need to manage traditional server infrastructure.
* **Real-time Data Core:** The platform leverages both **Cloud Firestore** and the **Firebase Realtime Database** to provide instantaneous data synchronization for the live map and emergency alerts, ensuring a highly responsive system for safety-critical events.
* **Geospatial Querying via Geohashing:** This architecture uses **Leaflet** with the Firebase Realtime Database. This allows for efficient, scalable radius-based queries (e.g., "find all users within 5km"), which is a core requirement for the dashboard.
* **Privacy by Design:** Enforces a strict separation of on-chain vs. off-chain data. Only an anonymous, non-personal hash is ever stored on the public blockchain, while all sensitive user data resides in the secure, off-chain Firestore database.

## 6. Technology Stack

* **Frontend (Mobile):** Native Android (**Kotlin**/Jetpack Compose). *An iOS version is on the roadmap.*
* **Frontend (Web):** **React.js** with TypeScript, Mapbox, Firebase Client SDK.
* **Backend (Serverless):** **Firebase Cloud Functions** (written in Node.js/TypeScript).
* **Core BaaS:** The full **Firebase Suite**, including **Firebase Authentication** for user management and **Cloud Storage for Firebase** for files.
* **Database (Primary):** **Cloud Firestore** (NoSQL) for all primary user data, profiles, and itineraries.
* **Database (Real-time & Geospatial):** **Firebase Realtime Database** with the **Leaflet** library for live location tracking and proximity queries.
* **Blockchain:** **Solidity** Smart Contract on **Polygon** (Layer 2).
* **DevOps:** **Git & GitHub** for version control and **Firebase Hosting** for the web dashboard.

## 7. Project Roadmap

- [x] **Core Safety Platform (v1.0 - Current)**
  - [x] Blockchain ID on Polygon Testnet
  - [x] Native Android App with Panic Button & Live Tracking
  - [x] Real-time Authorities' Dashboard
  - [x] NFC Wristband Verification
- [ ] **AI "Watchdog" Integration (v1.5)**
- [ ] **iOS Application (v2.0)**
- [ ] **Commerce & Convenience Features (v2.5)**


## 8. Acknowledgments

* The logo and visual assets used in this project were generated using AI image generation tools.
