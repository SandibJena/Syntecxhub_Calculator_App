# 📱 CALCY – Android Calculator App

CALCY is a modern Android calculator application built using **Kotlin and XML**, designed with a clean Material UI. It supports both **basic and scientific calculations**, making it suitable for everyday use as well as advanced mathematical operations.

---

## 🚀 Features

* ➕ Basic operations: Addition, Subtraction, Multiplication, Division
* 📐 Scientific functions:

  * Trigonometric: sin, cos, tan
  * Logarithmic: log, ln
  * Power & roots: x², √
* 🧮 Smart expression handling (supports parentheses)
* 🔢 Decimal and percentage calculations
* ⌫ Backspace functionality
* 🕘 Calculation History (stored locally using Room Database)
* 📱 Responsive UI:

  * Portrait → Basic calculator
  * Landscape → Scientific calculator

---

## 🛠️ Tech Stack

* **Language:** Kotlin
* **UI:** XML (Material Design principles)
* **Architecture:** Clean separation of UI and logic
* **Database:** Room Database (for history storage)
* **IDE:** Android Studio

---

## 📸 Screenshots

### Main Screen (Portrait Mode)

![Main Screen](main_screen.png)

### Scientific Mode (Landscape)

![Landscape Mode](landscape_mode.png)

### Calculation Example

![Calculation](calculation_example.png)

### History Feature

![History](history_screen.png)

---

## 📂 Project Structure

```
Syntecxhub_CALCY/
│
├── app/
│   ├── java/com/noguts/calculator/
│   │   ├── MainActivity.kt
│   │   ├── HistoryActivity.kt
│   │   ├── data/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── HistoryEntity.kt
│   │   ├── adapter/
│   │   │   ├── HistoryAdapter.kt
│   │
│   ├── res/
│   │   ├── layout/
│   │   ├── drawable/
│   │   ├── values/
│
├── screenshots/
├── README.md
```

---

## ⚙️ Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/Syntecxhub_CALCY.git
```

### Step 2: Open in Android Studio

* Open Android Studio
* Click **Open Project**
* Select the cloned folder

### Step 3: Build the Project

* Let Gradle sync complete
* Click **Run ▶️**

---

## 🧠 How It Works (Concept Overview)

* User input is captured through button clicks
* Expressions are dynamically constructed as strings
* Mathematical evaluation is handled using logic functions
* History is stored using **Room Database** for persistence
* UI updates are handled through **TextView bindings**

---

## 📈 Future Improvements

* Dark/Light theme toggle
* Voice input for calculations
* Graph plotting for functions
* Unit converter integration
* Cloud sync for history

---

## 🤝 Contributing

Contributions are welcome!
Feel free to fork this repository and submit pull requests.

---

## 📄 License

This project is open-source and available under the **MIT License**.

---

## 👨‍💻 Author
Z
Developed by **Sandib Jena**
🎓 B.Tech CSE Student

---

⭐ If you like this project, consider giving it a star on GitHub!
