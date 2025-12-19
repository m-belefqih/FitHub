# 📱 FitHub – Fitness & Community App

FitHub is an Android mobile application (Java) dedicated to **fitness tracking** and **community sharing**.  
It allows each user to create an account, track personal data (weight, height), and publish posts similar to Reddit.

---

## 🎯 Project Goals

- Build a mobile fitness application
- Manage user authentication
- Track personal data (height, weight)
- Add a post system (feed / community)
- Use Firebase as backend
- Apply **MVVM architecture**

---

## 🛠️ Technologies Used

- **Android Studio**
- **Java**
- **Kotlin (for build)**
- **Firebase Authentication**
- **Firebase Cloud Firestore**
- **Google Material 3 Components**
- **MVVM Architecture**
- **GitHub (team collaboration)**

---

## 🗂️ Project Structure (MVVM)

```
com.example.fithub
│
├── activity
│
├── adapter
│
├── firebase
│
├── model
│
├── repository
│
├── utils
│
└── viewmodel
```

---

## 🔑 Main Features

✅ Login & Register  
✅ Secure authentication with Firebase  
✅ User profile (height, weight)  
✅ Create posts (Reddit-style)  
✅ Public feed  
✅ Material Design interface  
✅ Real-time cloud database (Firestore)  

---

## 🔒 Important – Firebase Security

The `google-services.json` file is **not included** in this repository for security reasons.

Each team member must:

1. Go to the **Firebase main page**
2. Click on the **Android (🤖) icon**
3. Fill in:

| Field                | Value               |
| -------------------- | ------------------- |
| Android package name | `com.example.fithub` |
| App nickname         | FitHub               |

4. Click on **Register App**
5. Download the file:
```
google-services.json
```
6. Copy thif file into:
app/ (the main folder of your Android project)

---

## 🚀 Installation & Setup

1. Clone the repository:
```bash
   git clone https://github.com/your-username/FitHub.git
````

2. Open the project in Android Studio

3. Add your `google-services.json` file into the `/app` folder

4. Sync the project:

```
File → Sync Project with Gradle Files
```

5. Run the application on an emulator or physical device

---

## 👥 Team

Project developed for the **Mobile Programming** module
University / School: Faculty of Science and Technology in Marrakech (FSTG)

Team members:

* BELEFQIH MOHAMMED
* EL MAHDAOUI MOHAMMED
* RIZKI ABDELHADI
* OUTZMOURTE HAMZA
* ID EL KADI AMINE
* EL IDRISSI MOHAMED

---

## 📸 Screenshots

> To be added: Login, Register, Feed, Profile, Add Post…

---

## 📃 License

This project is intended for **educational and academic purposes only.**