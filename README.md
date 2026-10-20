<p align="center">
  <a href="https://laravel.com" target="_blank">
    <img src="images/logo.png" width="400" alt="FitHub Logo">
  </a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-App-green?logo=android">
  <img src="https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black">
  <img src="https://img.shields.io/badge/JSON-000000?logo=json&logoColor=white">
  <img src="https://img.shields.io/badge/Google%20Drive-4285F4?logo=google-drive&logoColor=white">
  <img src="https://img.shields.io/badge/Android%20Studio-3DDC84?logo=android-studio&logoColor=white">
  <img src="https://img.shields.io/badge/Material%20Design-757575?logo=material-design&logoColor=white">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-1.0.0-blue">
  <img src="https://img.shields.io/badge/Status-Stable-success">
  <a href="https://github.com/ellerbrock/open-source-badges/">
    <img src="https://badges.frapsoft.com/os/v1/open-source.svg?v=103" alt="Open Source Love">
  </a>
</p>


## About FitHub

FitHub is a **mobile fitness community application** built with **expressive and modern Android technologies**.  
We believe that maintaining a healthy lifestyle should be **engaging, social, and accessible to everyone**.  
FitHub simplifies **fitness tracking** and **community interaction** by providing essential tools commonly needed  
in **fitness and wellness applications**, such as:

- **Secure login and registration** with **email verification** for enhanced security and user uniqueness.
- **Detailed nutritional information** for a wide variety of foods, with the ability to **select daily consumed items** and **visualize total daily calorie intake**.
- **Body Mass Index (BMI) visualization** based on the user’s **weight and height**.
- **Personalized workout selection**, including **specific and general workout programs**.
- **Community interaction** through **creating posts**, **liking**, **commenting**, and **sharing content**.
- **Real-time chatting** with members of the **fitness community**.
- **User profile management** with **editable personal information** and **application settings**.
- **Cloud-based data synchronization** using **Firebase Authentication, Database, and Storage services**.

FitHub is developed using **Java for Android** and follows the **MVVM architecture** to ensure **scalability**,  
**maintainability**, and **clean separation of concerns**, providing a **smooth and reliable user experience**.

---

## 🗂️ MVVM Architecture

<img src="images/mvvm-architecture.png" alt="MVVM Structure"/>

---

## à noter que
nous avons utilisé Google Drive au lieu de Firebase Storage pour le partage des images
car Firebase Storage nécessite une carte bancaire pour l'authentification

---

## 📱 Main Features

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
```bash
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
  File → Sync Project with Gradle Files


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

<img src="images/application.gif" alt="FitHub Logo" width="1000"/>

---

## License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

Check this out : 

https://github.com/magicuidesign/magicui/tree/main

https://github.com/achrafaitibba/PureFeed-Extension/tree/main

https://github.com/MedGm/OllamaGUI

## ⚠️ Project Status

7itach 3adna wa7d mochkil f payments dyal kda kda , dirna les images f google drive

This project is currently under development and may contain bugs or incomplete features.
Contributions and improvements are welcome.

We use Google Cloud instead of Firebase storage because we had a problem in the payment so we us it instead

---

Déployer l’application sur le Play Store :

Pour cela, vous pouvez suivre ce tutoriel (https://developer.android.com/studio/publish?hl=fr). Toutefois, sachez que pour pouvoir déployer une application sur le Play Store, 
il faut disposer d’un compte Google Play Developer, et que cela coûte 15 $.


Déployer l’application sur le Firebase Distribution:

Si vous avez simplement besoin de déployer votre application sur quelques appareils, par exemple dans votre entourage, sans passer par le Play Store, 
Firebase Distribution (https://firebase.google.com/docs/app-distribution?hl=fr) vous permet de partager gratuitement votre application.

