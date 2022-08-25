# Task Manager
<p align = "center">
<img src = "https://img.shields.io/badge/JAVA-blue">
<img src = "https://img.shields.io/badge/SPRING BOOT-green">
<img src = "https://img.shields.io/badge/MONGO DB-green">
</p>

## Task manager application made with Spring, MongoDB and Angular.
### This application can be used by individuals or by teams.


# Installation
To install the project follow this tutorial.

* Go to **Code** -> **Download ZIP** or use `git clone` from command prompt (`git clone https://github.com/giuraionut/task-manager.git`)

## MongoDB

* This project was built using [MongoDB](https://docs.mongodb.com/manual/installation/). You have to install MongoDB on your machine to be able to run the project.

## Backend

* The backend of this project is built using Java **Spring**. You need a **Java IDE** and **Maven** to run the project.
  * I recommend [IntelliJ IDEA Community edition](https://www.jetbrains.com/idea/), because it is a free friendly IDE.
* Once you have your IDE ready, open **api** folder as a project and use the following command to start the backend (`mvn spring-boot:run`).

## Frontend

* For the frontend of this project you need **Angular**.
  * First, you have to install [NodeJS](https://nodejs.org/en/download/).
  * After you installed **NodeJS** you can navigate to **frontend/src/app** folder and open a command prompt to run the command `ng serve`. This command will start the frontend.
    *  You can also download [VS Code](https://code.visualstudio.com/download), a modern text editor that supports hudrends of plugins to make your life easier.

Now you are all set and the project should run as intended.

## Docker

If you want to run the application in docker containers you have to follow those steps:
* Download [Docker](https://www.docker.com/products/docker-desktop).
* Use `docker pull mongo` to get the image for Mongo. More information here, [Mongo-Docker](https://hub.docker.com/_/mongo).
  * Run `docker run -d -p 27017:27017 --name=name-of-your-mongo-container mongo:latest`
* Create the *Dockerfile* for frontend and backend or download the files that I created.
* For frontend you have to:
  * Build the application with `ng build`.
  * Run `docker build -t name-of-your-image .`. This command will create a docker image.
  * Run `docker run -d --name=name-of-your-container -p port:port name-of-your-image`. Port can be 80:80.
* For backend you have to:
  * Build the application with `mvn install` if you don't have a **.jar** yet.
  * Run `docker build -t name-of-your-image .`
  * Run `docker run -d --name=name-of-your-container -p port:port name-of-your-image`. Port can be 808:8080.

Now you should have 3 images, for frontend, backend and for mongo and 3 containers for each of them, and the application should run.

# Implementation

## MongoDB and Spring
* MongoDB is a document-oriented database, also known as *NOSQL* database. Mongo uses **JSON** document structure with optional schemas.
* Mongo's synergy with Spring is great. Interacting with the database through *repositories* is really simple and intuitive.
  ```
  Integer countByResponsibleIdAndIsPrivateAndIsOpen(String responsibleId, Boolean isPrivate, Boolean isOpen);
  ```
  * The snippet above does exactly what you think it does: it returns the sum of all documents by *responsibleId* that have the *isPrivate* and *isOpen* fields set either true or false. It is that simple to create your own custom methods if you want to interogate the database for specific data.
  * Also you already have a lot of methods for interogation which saves so much time.

* Spring is a Java framework that can be used by any Java application. You can read more about it here: [Spring](https://spring.io/).

# Angular
* Angular is a powerful JavaScript framework. You can read more about it on the official website: [Angular](https://angular.io/).
* Angular allows you to build modern applications for any deployment target. It reaches very high speeds and has a lot of support by communities and IDEs.

# Features

## API features
* Secured using **JWT** ( json web token ) for authentication.
* Secured using **method security** with **ROLES** so only certain users can access certain resources.
* Performs all the **CRUD** operations.
* Uses **Web Sockets** to implement the chat feature and push notification feature.
  * Chat history is saved in the database.
  * Notifications are saved in the database.
* Allows file (image for avatar) upload.

## Frontend features
* Form validation for user input.
* Responsive interface suited for desktop and mobile devices.
