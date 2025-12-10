![JobHelperImage.jpg](JobHelperImage.jpg)

## Introduction

**JobHelper** is a Java-based web application built using the Spring Boot framework. It provides a comprehensive platform for organizing and managing the job search process. The project was created to offer an efficient, scalable solution that helps users streamline and optimize their job-searching workflow.

## Technologies Used

* **Spring Boot**: Provides a powerful and flexible framework for building Java-based applications.
* **Spring Security**: Ensures secure user authentication and authorization.
* **Spring Data JPA**: Simplifies the implementation of data access layers by providing a repository abstraction.
* **Swagger**: Enables API documentation and testing.
* **MapStruct**: Facilitates the mapping between DTOs and entity models.
* **Hibernate**: An ORM tool for Java applications.
* **Liquibase**: An open-source database schema migration tool.
* **MySQL**: A relational database management system.
* **Docker**: A platform for developing, shipping, and running applications in containers.
* **Lombok**: A library to reduce boilerplate code in Java.

## Functionalities

### User Management

* User registration.
* Secure user login with JWT-based authentication.
* Password recovery & reset via email verification code.

**Endpoints:**

For non-authenticated users:
``` 
POST: /api/auth/register
``` 
Example of request body to **register**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "repeatPassword": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```
``` 
POST: /api/auth/login
```
Example of request body to **log-in**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```
``` 
POST: /api/auth/forgot-password
```
Example of request body to **forgot-password** for reset link sent to email if account exists.:

```json
{
  "email": "test444@example.com"
}
```
``` 
POST: /api/auth/reset-password
```
Example of request body to **reset-password**:

```json
{
  "token": "token-here",
  "newPassword": "new_secure_password123"
}
```
``` 
POST: /api/auth/verify-code
```
Example of request body for **verify-code**:

```json
{
  "token": "token-here received on email"
}
```

### Job Management

* Create, retrieve, update, and delete jobs.
* Search for jobs based on various parameters.

**Available endpoints for Jobs Management**

``` 
GET: /api/jobs

GET: /api/jobs/{id} 

POST: /api/jobs

PUT: /api/jobs/{id} 

DELETE: /api/jobs/{id} 
```
``` 
Example of request body to **create new jobs**:
```json
{
  "title": "Java Developer",
  "description": "Develop backend microservices",
  "requiredSkills": ["Java", "Spring Boot", "SQL"],
  "company": "Tech Corp",
  "location": "Kyiv",
  "salary": 5000.00,
  "workFormat": "Remote"
}
```

### Resume Management

* Create, retrieve resume.

**Available endpoints for Resume Management**

``` 
GET: /api/resumes/{id}

GET: /api/resumes/{id}/file

POST: /api/resumes 

POST: /api/resumes/upload

```
Example of form-data to **create new resume**:
```
file
userId
```
### Job match Management

* Checking if the resume suitability for the job.

**Available endpoints for Job match**

```
GET: /api/job-matches/resume/{resumeId}

```
Example of answer for request **get job-match**:
```json
{
  "id": 91,
  "resumeId": 1,
  "jobId": 1,
  "jobTitle": "Java Developer",
  "matchScore": 33.33333333333333

```

### Job Application Management (Tracker)

* Manage user job applications.
* CRUD operations for job application tracking.

**Available endpoints for JobsApplication Management**

``` 
GET: /api/applications/user/{userId}

GET: /api/applications/{id} 

POST: /api/applications

PUT: /api/applications/{id} 

DELETE: /api/applications/{id} 
```
``` 
Example of request body to **create new jobsApplication**:
```json
{
  "userId": 1,
  "jobId": 35
}
```
### ATS Evaluation Management

* Evaluate resume.

**Available endpoint for JobsApplication Management**

``` 
GET: /api/ats-evaluation/resume/{resumeId}
```
``` 
Example of request body to **get evaluate**:
```json
{
    "id": 1,
    "resumeId": 1,
    "score": 85.0,
    "feedback": "Looks good for most ATS systems."
}
```

## Project Structure
The project follows a modular structure:

* **model**: Entity models representing the database schema.
* **repository**: Spring Data JPA repositories for database operations.
* **service**: Business logic implementation.
* **controller**: Contains controllers for handling HTTP requests.
* **dto**: Data Transfer Objects for communication between the client and server.
* **mapper**: Mapper interfaces for mapping between DTOs and entity models.

Link to a video demonstration of the project - https://drive.google.com/file/d/1h1ZPs8Jy5F6MNwBxG_eYCZVF-mMmglmu/view?usp=sharing

## Setup

To set up and use the jobHelper, follow these steps:


1. Clone the repository to your local machine.
2. Configure the database settings in the application properties.
3. Build and run the application using your preferred Java IDE or build tool.
4. Access the Swagger documentation: http://localhost:8080/swagger-ui.html
   The API uses JWT (JSON Web Tokens) for authentication.
5. For protected endpoints, authenticate first and pass the JWT token via Authorization: Bearer <token>.

   To access protected endpoints first login to api, then include the generated JWT token in the Authorization header of your requests.


## Challenges and Solutions
**Challenge**: Implementing secure user authentication.

**Solution**: Utilized Spring Security and JWT for a robust authentication mechanism.

**Challenge**: Efficiently managing shopping carts and order processing.

**Solution**: Designed a ShoppingCartService and OrderService to handle cart operations and order management.

## Postman
For detailed API usage, you can use provided requests samples.
Examples answers for difference request body:

``` 
POST: /api/auth/register
``` 
Example of request body to **register**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "repeatPassword": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "shippingAddress": "123 Main St, City, Country"
}
```
``` 
Answer from request body to **register**:
{
  "id": 1,
  "password": "securePassword123",
  "repeatPassword": "securePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "shippingAddress": "123 Main St, City, Country"
}
```
```
POST: /api/auth/login
```
Example of request body to **log-in**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```
```json
Answer from request body to **log-in**:
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImlhdCI6MTcxNzYwMDI4MywiZXhwIjoxNzE3NjAwNTgzfQ.0V8B9GNRiZiFnaxdetrAv9RpIgvxl99q6IqSyqE2lBQ"
}
```
```
POST: /api/jobs/
```
Example of request body to **create new jobs**:
```json
{
  "title": "Java Developer",
  "description": "Develop backend microservices",
  "requiredSkills": ["Java", "Spring Boot", "SQL"],
  "company": "Tech Corp",
  "location": "Kyiv",
  "salary": 5000.00,
  "workFormat": "Remote"
}
```
Answer from request body to **create new job**:
```json
{
  "id": "1",       
  "title": "Java Developer",
  "description": "Develop backend microservices",
  "requiredSkills": ["Java", "Spring Boot", "SQL"],
  "company": "Tech Corp",
  "location": "Kyiv",
  "salary": 5000.00,
  "workFormat": "Remote"
}
```
```
DELETE: /api/jobs/{id}
Example of request body to **delete job with id**:
```
```json
{
  "id": "1",
  "title": "Java Developer",
  "description": "Develop backend microservices",
  "requiredSkills": ["Java", "Spring Boot", "SQL"],
  "company": "Tech Corp",
  "location": "Kyiv",
  "salary": 5000.00,
  "workFormat": "Remote"
}
```
```
Answer from request body to **delete job with id**:
Status 204 No Content

PUT: /api/jobs/{id}
Example of request body to **modernization job with id**:
```
```json
{
  "id": "1",
  "title": "Java Developer",
  "description": "Develop backend microservices",
  "requiredSkills": ["Java", "Spring Boot", "SQL"],
  "company": "Tech Corp",
  "location": "Kyiv",
  "salary": 5000.00,
  "workFormat": "Remote"
}
```
```
Answer from request body to **put job with id**:
Status 200 Ok

GET: /api/jobs
Answer from request body **get all jobs**:
```
```json
[
    {
      "id": "1",
      "title": "Java Developer",
      "description": "Develop backend microservices",
      "requiredSkills": ["Java", "Spring Boot", "SQL"],
      "company": "Tech Corp",
      "location": "Kyiv",
      "salary": 5000.00,
      "workFormat": "Remote"
    },
    {
      "id": "2",
      "title": "Java Developer",
      "description": "Develop backend",
      "requiredSkills": ["Java", "Spring Boot", "Hibernate"],
      "company": "Entri",
      "location": "Kyiv",
      "salary": 2000.00,
      "workFormat": "Remote"
    },
    {
      "id": "3",
      "title": "Java Developer",
      "description": "Develop backend",
      "requiredSkills": ["Java"],
      "company": "Uni",
      "location": "Kyiv",
      "salary": 900.00,
      "workFormat": "Remote"
    }
]
```
```
GET: /api/jobs/{id}
Answer from request body **get job with id = 2**:
```
```json
{
  "id": "2",
  "title": "Java Developer",
  "description": "Develop backend",
  "requiredSkills": ["Java", "Spring Boot", "Hibernate"],
  "company": "Entri",
  "location": "Kyiv",
  "salary": 2000.00,
  "workFormat": "Remote"
}
```
```


## Conclusion
The jobHelper is designed to manage job search operations.

Whether you're a developer looking to understand the codebase or a user interested in utilizing the features, this README provides a comprehensive guide to get started.
