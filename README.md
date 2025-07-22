# 🛠️ Grievance Management System

Grievance Management System is a web-based application built using Java Spring Boot that streamlines the process of raising, tracking, and resolving grievances within an organization or institution. It enables users to submit complaints, view status updates, and ensures transparent resolution by administrators or assigned authorities.

## 🚀 Features

- 📝 Submit grievances with category and description
- 🔍 Track status of submitted complaints
- 👨‍💼 Admin dashboard for managing user grievances
- 📊 Priority-wise and department-wise complaint categorization
- 🔔 Email notifications for status updates (optional)
- 🗂️ Role-based access (User / Admin)
- 🧾 Complaint history and resolution logs
- 💬 Comment section for two-way communication

## 🛠️ Tech Stack

- **Backend**: Java, Spring Boot
- **Frontend**: HTML, CSS, JavaScript (Thymeleaf or JSP)
- **Database**: MySQL 
- **Libraries & Tools**:
  - Spring Web
  - Spring Data JPA
  - Spring Security (optional for role-based access)
  - Thymeleaf (for dynamic frontend rendering)
  - Lombok (for cleaner code)
  - Git, IntelliJ / VS Code

## 📦 Installation

### Option 1: Clone the Repo

```bash
git clone https://github.com/yourusername/GrievanceManagementSystem.git
cd GrievanceManagementSystem
Option 2: Download ZIP
Click "Code" → "Download ZIP"

Extract and open the project in IntelliJ IDEA or your preferred IDE

▶️ Usage
Set up the database:

MySQL: Create a schema (e.g., grievance_db)

Update application.properties with DB credentials

Build & Run the App:
./mvnw spring-boot:run
Access the application:

Open browser and navigate to: http://localhost:8080

Login or Register to submit/view grievances.

📂 Folder Structure

GrievanceManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/com/grievance/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
├── pom.xml
└── README.md
🤝 Contributing
Contributions are welcome!

Fork the repo

Create your feature branch: git checkout -b feature-name

Commit your changes: git commit -m 'Add new feature'

Push to the branch: git push origin feature-name

Create a Pull Request

🙋‍♀️ Contact
Developer: Supriya
📫 Email: supriyauchinthala@gmail.com
🔗 LinkedIn: https://www.linkedin.com/in/supriya-uchinthala
