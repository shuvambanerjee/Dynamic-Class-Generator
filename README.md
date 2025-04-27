
# 📦 Dynamic Class Generator (Spring Boot)

This project is a **Spring Boot application** that allows you to dynamically **generate Java classes at runtime** by sending a **JSON** over a **REST API**.  
It **compiles**, **registers** the classes as Spring Beans, and also **saves** the generated `.java` source files permanently.

---

## 🚀 Features
- Accepts **class definitions** via REST API.
- **Generates Java classes** dynamically with custom fields.
- **Compiles and registers** the class in the **Spring ApplicationContext**.
- **Saves** the generated class file permanently in a configured package.
- **View** all dynamically created classes through an API.

---

## 🔥 Tech Stack
- Java 21+
- Spring Boot 3.x
- Spring Web

---

## 🛠 How It Works

### 1. Create Class
Send a `POST` request to:

```
POST /generate-class
```

**Sample Request JSON:**
```json
{
  "className": "Employee",
  "fields": {
    "id": "int",
    "name": "String",
    "email": "String"
  }
}
```

✅ This will:
- Generate `Employee.java` under the configured package (e.g., `com.shuvam.generated`).
- Compile and load it into memory.
- Register it as a Spring Bean.

---

### 2. View All Generated Classes

Send a `GET` request to:

```
GET /list
```

✅ This will return a list of all generated class names.

---

## 🧩 Project Structure

```bash
src/main/java/com/shuvam/
│
├── controller/
│   └── DynamicClassController.java
│
├── service/
│   └── DynamicClassService.java
│
├── model/
│   └── ClassDefinitionRequest.java
│
└── generated/
    └── (Dynamic classes saved here)
```

---

## 🚴‍♂️ Running the Project

### Prerequisites:
- Java 21+
- Maven 3.9+
- Git

---

### Clone and Run

```bash
git clone git@github.com:your-username/dynamic-class-generator.git
cd dynamic-class-generator
mvn clean install
mvn spring-boot:run
```

Server will start at:

```
http://localhost:8080
```

---

## 📜 Sample API Requests (using `curl`)

### Create a Class

```bash
curl -X POST http://localhost:8080/dynamic/generateClass    -H 'Content-Type: application/json'    -d '{
         "className": "Product",
         "fields": {
           "id": "int",
           "name": "String",
           "price": "double"
         }
       }'
```

### Get All Generated Classes

```bash
curl http://localhost:8080/list
```

---

## 🛡️ Important Notes
- Only **basic Java types** (`int`, `double`, `String`, etc.) are supported.
- Invalid field types or invalid class names will throw errors.
- All generated `.java` files are saved **permanently** under `src/main/java/com/shuvam/generated/`.
- Application needs write permissions to `src/main/java/` for saving files.

---

---

## 🧠 Future Enhancements
- Add Swagger UI for easier API testing.
- Support validation annotations like `@NotNull`, `@Size`.
- Support relationships (e.g., OneToMany) between generated classes.

---

## 📜 License
This project is licensed under the MIT License.

---

# ✨ Happy Coding! ✨
