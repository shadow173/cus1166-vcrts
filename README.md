# VCRTS Application

This application is a basic Java implementation of the VCRTS (Vehicular Cloud Real-Time System) idea: an application that is a distributed cloud computing platform which uses the idle time of the computational resources of parked vehicles. 

This system enables Vehicle Owners to earn compensation by offering their idle compute power, and Job Owners to leverage extra compute to submit and track their jobs.

This implementation is a basic example provided for demonstration purposes only. It is not intended to be a fully functional or production-ready system.



## Installation

- First Make sure Maven and PostgresSQL are installed.

- Create a PostgreSQL database (e.g., named production).

- Run the provided ``schema.sql`` file (located in src/main/resources) to set up the required tables.

#### Set Up the .env configuration:
Setup env variables for your db connection (see **.env.example** for more info):
```env
POSTGRES_HOST= 
POSTGRES_USERNAME=
POSTGRES_PORT=
POSTGRES_PASSWORD=
BCRYPT_SALT=
```
Place the .env file in src/main/resources

#### Install and run this project with maven.

```bash
  mvn clean compile
  mvn exec:java
```
    
