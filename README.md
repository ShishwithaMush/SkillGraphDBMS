# 🎯 SkillGraph — Placement Preparation System

> A DBMS-backed Java desktop application that helps engineering students systematically prepare for campus placements.

![Java](https://img.shields.io/badge/Java-Swing-orange?style=flat-square&logo=java)
![Database](https://img.shields.io/badge/Database-Oracle%20SQL-red?style=flat-square&logo=oracle)
![Build](https://img.shields.io/badge/Build-Maven-blue?style=flat-square&logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

---

## 📌 About

**SkillGraph** is a Course Based Project for the **Database Management Systems** course at **Vasavi College of Engineering (Autonomous)**, affiliated to Osmania University.

The system addresses the fragmented nature of campus placement preparation by providing a single, data-driven platform that tracks resumes, skills, mock tests, roadmaps, and progress — all backed by a normalized Oracle relational database.

**Submitted by:**

| Student Name | Roll Number |
|---|---|
| Madhurima D | 1602-24-737-044 |
| Shishwitha Musham | 1602-24-737-305 |

**Under the guidance of:** Dr. S. Aruna, Associate Professor, Dept. of IT

---

## ✨ Features

- 🔐 **Secure Authentication** — SHA-256 password hashing via Java's `MessageDigest`
- 📄 **Resume Upload & Scoring** — Automatic readiness score based on file type, name, and size
- 🧠 **Skill Mapping** — Add and track technical skills with proficiency levels
- 🗺️ **AI Roadmap Generation** — 6-phase personalized preparation roadmaps stored in DB
- ✅ **Task Manager** — Track and complete roadmap tasks per phase
- 📝 **Mock Skill Tests** — Aptitude, Programming, and Database categories
- 📊 **Progress Dashboard** — Overall placement readiness score with upsert logic
- 💬 **Chat History** — Stores AI interaction logs per user

---

## 🗄️ Database Schema

The Oracle database uses a fully normalized (3NF) relational schema:

```
USERS ──< USER_SKILLS >── SKILLS
USERS ──< RESUMES
USERS ──< AI_ROADMAP ──< TASKS
USERS ──< TEST_RESULTS >── MOCK_TESTS
USERS ──< PROGRESS
USERS ──< CHAT_HISTORY
```

Key tables: `USERS`, `SKILLS`, `USER_SKILLS`, `RESUMES`, `AI_ROADMAP`, `TASKS`, `MOCK_TESTS`, `TEST_RESULTS`, `PROGRESS`, `CHAT_HISTORY`

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Java Swing (JFrame, JPanel, JTable, JButton) |
| Backend | Java (Service + Model architecture) |
| Database | Oracle Database |
| Connectivity | JDBC with PreparedStatements |
| Build Tool | Apache Maven |
| Security | SHA-256 password hashing |

---

## ⚙️ Hardware Requirements

| Component | Minimum |
|---|---|
| RAM | 4 GB |
| Processor | Intel i3 or above |
| Storage | 500 MB free |
| OS | Windows 10 or above |
| Display | 1280×800 or higher |

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 11+
- Oracle Database (XE or full)
- Apache Maven
- Oracle JDBC Driver (`ojdbc8.jar`)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/SkillGraph-DBMS.git
   cd SkillGraph-DBMS
   ```

2. **Set up the Oracle database**
   ```bash
   sqlplus username/password@localhost:1521/xe @oracle_schema.sql
   ```

3. **Configure database connection**
   Edit `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:oracle:thin:@localhost:1521:xe
   db.username=your_username
   db.password=your_password
   ```

4. **Build and run**
   ```bash
   mvn clean package
   java -jar target/skillgraph.jar
   ```

---

## 📁 Project Structure

```
SkillGraph-DBMS/
├── src/
│   └── main/
│       ├── java/
│       │   ├── model/          # Entity classes (User, Skill, Task, ...)
│       │   ├── service/        # Business logic (AuthService, ResumeService, ...)
│       │   └── ui/             # Java Swing screens
│       └── resources/
│           └── db.properties   # DB connection config
├── oracle_schema.sql           # Full DDL + seed data
├── pom.xml                     # Maven config
└── README.md
```

---

## 🔒 Security

- Passwords are hashed with **SHA-256** before storage — plaintext is never persisted
- All queries use **PreparedStatements** to prevent SQL injection
- Session management ensures user isolation across modules

---

## 📚 References

1. Silberschatz, Korth & Sudarshan — *Database System Concepts*, McGraw-Hill
2. Elmasri & Navathe — *Fundamentals of Database Systems*
3. [Oracle Database Docs](https://docs.oracle.com)
4. [Java JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc)
5. [Maven Docs](https://maven.apache.org/guides)

---

## 📜 License

This project is licensed under the MIT License.

---

*Vasavi College of Engineering (Autonomous) — Department of Information Technology — 2025–2026*
