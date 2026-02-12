
#  Hotel Management System (Java Console)

A **menu-driven Java console-based Hotel Management System** that allows admins and customers to manage hotel operations efficiently. The system supports **room booking, food ordering after booking, billing, and record management**, with persistent storage using CSV files.

---

##  Features

###  Admin Module

* Admin login authentication
* Manage admin accounts (Add / View / Update / Search)
* Manage customer records
* Manage room records (availability & rates)
* Manage food menu items
* Create and manage customer bills
* Mark bills as paid/unpaid

###  Customer Module

* Customer registration & login
* View available rooms
* Book rooms for multiple nights
* Order food from menu **after booking**
* View detailed bill (room + food)
* Checkout and free room automatically

###  Billing System

* Automatic bill generation
* Room cost + food cost calculation
* Paid / unpaid status tracking
* Unique bill ID generation

###  Data Persistence

* File-based storage using CSV files:

  * `admins.csv`
  * `customers.csv`
  * `rooms.csv`
  * `menu.csv`
  * `bills.csv`
  * `bill_qty.csv`
* Data is **loaded on startup** and **saved on exit**

---

##  Technologies Used

* **Java**
* Java I/O (`FileReader`, `FileWriter`)
* `Scanner` for user input
* 1D and 2D Arrays
* Exception Handling
* Console-based UI

---

##  How to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/hotel-management-system.git
   ```

2. Navigate to the project directory:

   ```bash
   cd hotel-management-system
   ```

3. Compile the program:

   ```bash
   javac ProjectFinal.java
   ```

4. Run the program:

   ```bash
   java ProjectFinal
   ```

---

##  Default Login Credentials

### Admin

* **Username:** `admin`
* **Password:** `admin123`

*(You can add more admins from the admin panel)*

---

##  Project Structure

```
ProjectFinal.java
admins.csv
customers.csv
rooms.csv
menu.csv
bills.csv
bill_qty.csv
```

---

##  Key Concepts Demonstrated

* Menu-driven programming
* Record management using arrays
* File handling in Java
* Input validation & exception handling
* Billing logic and data consistency
* Console-based application design

---

##  Academic Note

This project was developed as a **Java semester project**, following constraints such as:

* No GUI
* Single-class implementation
* Focus on core Java concepts

---

##  Author

**Muhammad Anas Khan**
BS Software Engineering
COMSATS University Islamabad
