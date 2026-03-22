<div align="center">

# 🎬 YouTube Tool

### A powerful Spring Boot application for YouTube video utilities

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-7.0-red?style=for-the-badge&logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![Maven](https://img.shields.io/badge/Maven-Build_Tool-purple?style=for-the-badge&logo=apachemaven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

[Features](#-features) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [Docker Setup](#-docker-setup) • [API](#-api-endpoints) • [Contributing](#-contributing)

</div>

---

## 📸 Preview

> YouTube Tool helps content creators extract SEO tags, fetch video details, and download thumbnails — all from one clean interface.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🎯 **SEO Tag Generator** | Search any YouTube video title and extract SEO tags from top results |
| 📊 **Video Details Retriever** | Fetch complete video info — title, description, tags, channel, and thumbnail |
| 🖼️ **Thumbnail Fetcher** | Get high resolution (1280x720) thumbnails from any YouTube URL or video ID |
| ⚡ **Redis Caching** | API responses cached for faster performance and reduced YouTube quota usage |
| 🐳 **Docker Support** | Fully containerized — runs with a single command |
| 🌙 **Dark Mode** | Clean UI with light/dark mode support |

---

## 🛠️ Tech Stack

### Backend
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)
![WebClient](https://img.shields.io/badge/WebFlux-6DB33F?style=flat&logo=spring&logoColor=white)

### Frontend
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat&logo=thymeleaf&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-38B2AC?style=flat&logo=tailwind-css&logoColor=white)

### DevOps
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

---

## 📁 Project Structure

```
YouTubeTool/
├── src/
│   └── main/
│       ├── java/com/YouTubeTool/
│       │   ├── controller/
│       │   │   ├── PageController.java
│       │   │   ├── ThumbnailController.java
│       │   │   ├── YouTubeTagsController.java
│       │   │   └── YouTubeVideoController.java
│       │   ├── model/
│       │   │   ├── SearchVideo.java
│       │   │   ├── Video.java
│       │   │   └── VideoDetails.java
│       │   ├── service/
│       │   │   ├── ThumbnailService.java
│       │   │   └── YouTubeService.java
│       │   ├── RedisConfig.java
│       │   └── YouTubeToolApplication.java
│       └── resources/
│           ├── templates/
│           │   ├── fragments/navbar.html
│           │   ├── home.html
│           │   ├── thumbnails.html
│           │   └── video-details.html
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+
- Docker & Docker Desktop
- YouTube Data API v3 Key → [Get it here](https://console.cloud.google.com/)

---

## 🐳 Docker Setup (Recommended)

The easiest way to run the project — no manual Redis installation needed!

### Step 1 — Clone the repository

```bash
git clone https://github.com/omkarhale/YouTubeTool.git
cd YouTubeTool
```

### Step 2 — Set your YouTube API Key

```bash
# Windows PowerShell
$env:YOUTUBE_API_KEY="your_api_key_here"

# Windows CMD
set YOUTUBE_API_KEY=your_api_key_here

# Mac/Linux
export YOUTUBE_API_KEY=your_api_key_here
```

### Step 3 — Run with Docker

```bash
docker-compose up --build
```

### Step 4 — Open in browser

```
http://localhost:8080
```

That's it! 🎉 Both Spring Boot app and Redis start automatically.

---

## 💻 Local Setup (Without Docker)

### Step 1 — Start only Redis via Docker

```bash
docker-compose up redis
```

### Step 2 — Create `application-local.properties`

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### Step 3 — Run Spring Boot

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Home page — SEO Tag Generator |
| `POST` | `/youtube/search` | Search videos and get SEO tags |
| `GET` | `/youtube/video-details` | Video details form |
| `POST` | `/youtube/video-details` | Fetch video details by URL or ID |
| `GET` | `/get-thumbnail` | Thumbnail fetcher form |
| `POST` | `/get-thumbnail` | Fetch thumbnail by URL or ID |

---

## ⚡ Redis Caching

YouTube API has a daily quota limit of **10,000 units**. Redis caching helps avoid hitting this limit.

| Cache | Key | TTL |
|---|---|---|
| `videoSearch` | Video title | 1 hour |
| `videoDetails` | Video ID | 6 hours |

### Verify Redis is working

```bash
# Open Redis CLI
docker exec -it youtube-tool-redis redis-cli

# Check cached keys
keys *

# Expected output after searching
# 1) "videoDetails::BIOgR38G2Zs"
# 2) "videoSearch::MrBeast"
```

---

## 🐳 Docker Services

```yaml
Services running via docker-compose:

┌─────────────────────────────────────┐
│  youtube-tool-app   → port 8080     │
│  youtube-tool-redis → port 6379     │
└─────────────────────────────────────┘
```

| Command | Description |
|---|---|
| `docker-compose up --build` | Build and start all containers |
| `docker-compose up` | Start containers (no rebuild) |
| `docker-compose down` | Stop and remove containers |
| `docker-compose up redis` | Start only Redis |

---

## 🔑 Environment Variables

| Variable | Description | Required |
|---|---|---|
| `YOUTUBE_API_KEY` | Your YouTube Data API v3 key | ✅ Yes |

> ⚠️ **Never hardcode your API key in `application.properties`**. Always use environment variables.

---

## 🤝 Contributing

Contributions are welcome! Here's how:

```bash
# 1. Fork the repository
# 2. Create your feature branch
git checkout -b feature/AmazingFeature

# 3. Commit your changes
git commit -m "feat: Add AmazingFeature"

# 4. Push to the branch
git push origin feature/AmazingFeature

# 5. Open a Pull Request
```

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 👨‍💻 Author

**Omkar Hale**

[![GitHub](https://img.shields.io/badge/GitHub-omkarhale-black?style=flat&logo=github)](https://github.com/omkarhale)

---

<div align="center">

⭐ **Star this repo if you find it helpful!** ⭐

</div>
