import java.io.*;
import java.util.Scanner;
public class ProjectFinal {

    static final int MAX_ADMINS = 20;
    static final int MAX_CUSTOMERS = 100;
    static final int MAX_ROOMS = 120;
    static final int MAX_MENU = 120;
    static final int MAX_BILLS = 200;

    static Scanner input = new Scanner(System.in);
    // 1) ADMIN RECORD SYSTEM (1D arrays)
    static String[] adminUser = new String[MAX_ADMINS];
    static String[] adminPass = new String[MAX_ADMINS];
    static int adminSize = 0;

    // 2) CUSTOMER RECORD SYSTEM (1D arrays)
    static String[] custName = new String[MAX_CUSTOMERS];
    static int[] custAge = new int[MAX_CUSTOMERS];
    static String[] custPass = new String[MAX_CUSTOMERS];
    static int custSize = 0;

    // 3) ROOMS RECORD SYSTEM (1D arrays)
    static int[] roomNo = new int[MAX_ROOMS];
    static int[] roomRate = new int[MAX_ROOMS];
    static boolean[] roomTaken = new boolean[MAX_ROOMS];
    static int roomSize = 0;

    // 4) MENU RECORD SYSTEM (1D arrays)
    static String[] itemName = new String[MAX_MENU];
    static int[] itemPrice = new int[MAX_MENU];
    static int menuSize = 0;

    // 5) BILLING RECORD SYSTEM (1D + 2D arrays)
    static int[] billId = new int[MAX_BILLS];
    static int[] billCustomerIndex = new int[MAX_BILLS];
    static int[] billRoomIndex = new int[MAX_BILLS];
    static int[] billNights = new int[MAX_BILLS];
    static int[] billRoomCost = new int[MAX_BILLS];
    static int[] billFoodCost = new int[MAX_BILLS];
    static int[] billTotal = new int[MAX_BILLS];
    static boolean[] billPaid = new boolean[MAX_BILLS];
    static int billSize = 0;

    // 2D array: food quantity per bill (bill x menuItem)
    static int[][] billFoodQty = new int[MAX_BILLS][MAX_MENU];

    // CURRENT SESSION
    static int loggedAdmin = -1;
    static int loggedCustomer = -1;

    // FILE NAMES (simple CSV)
    static final String FILE_ADMINS = "admins.csv";
    static final String FILE_CUSTOMERS = "customers.csv";
    static final String FILE_ROOMS = "rooms.csv";
    static final String FILE_MENU = "menu.csv";
    static final String FILE_BILLS = "bills.csv";
    static final String FILE_QTY = "bill_qty.csv";

    // MAIN
    public static void main(String[] args) {

        // Load saved records from files
        loadAllFiles();

        // default data
        defaultDataIfEmpty();

        // Start menu-driven system
        mainMenu();

        // Save before exit
        saveAllFiles();

        System.out.println("\nProgram closed safely. Data saved.");
    }

    // MAIN MENU
    static void mainMenu() {
        while (true) {
            System.out.println("         HOTEL MANAGEMENT SYSTEM (PF)            ");
            System.out.println("1) Admin Login");
            System.out.println("2) Customer Login");
            System.out.println("3) Customer Registration");
            System.out.println("4) Exit");
            System.out.println("-------------------------------------------------");

            int choice = readIntInRange("Choose option: ", 1, 4);

            if (choice == 1) {
                if (adminLogin()) {
                    adminDashboard();
                }
            } else if (choice == 2) {
                if (customerLogin()) {
                    customerDashboard();
                }
            } else if (choice == 3) {
                registerCustomer();
            } else {
                return; // exit
            }
        }
    }

    // ADMIN DASHBOARD
    static void adminDashboard() {
        while (true) {
            System.out.println("\n                ADMIN DASHBOARD");
            System.out.println("Logged in as: " + adminUser[loggedAdmin]);
            System.out.println("1) Admin Records (Add/View/Update/Search)");
            System.out.println("2) Customer Records (Add/View/Update/Search)");
            System.out.println("3) Room Records (Add/View/Update/Search)");
            System.out.println("4) Menu Records (Add/View/Update/Search)");
            System.out.println("5) Billing System (Add/View/Update/Search)");
            System.out.println("6) Logout");
            System.out.println("---------------------------------------------");

            int choice = readIntInRange("Select: ", 1, 6);
            if (choice == 1) adminRecordsMenu();
            else if (choice == 2) customerRecordsMenu();
            else if (choice == 3) roomRecordsMenu();
            else if (choice == 4) menuRecordsMenu();
            else if (choice == 5) billingRecordsMenu();
            else {
                loggedAdmin = -1;
                return;
            }
            saveAllFiles();
        }
    }

    // CUSTOMER DASHBOARD
    static void customerDashboard() {
        while (true) {
            System.out.println("\n               CUSTOMER DASHBOARD");
            System.out.println("Welcome: " + custName[loggedCustomer]);
            System.out.println("1) View Available Rooms");
            System.out.println("2) Book Room (Creates Bill)");
            System.out.println("3) Order Food (Adds to Bill)");
            System.out.println("4) View My Bill");
            System.out.println("5) Checkout (Mark Paid + Free Room)");
            System.out.println("6) Logout");
            System.out.println("---------------------------------------------");

            int choice = readIntInRange("Select: ", 1, 6);

            if (choice == 1) showAvailableRooms();
            else if (choice == 2) customerBookRoom();
            else if (choice == 3) customerOrderFood();
            else if (choice == 4) customerViewOwnBill();
            else if (choice == 5) customerCheckout();
            else {
                loggedCustomer = -1;
                return;
            }
            saveAllFiles();
        }
    }

    // LOGIN SYSTEMS
    static boolean adminLogin() {
        System.out.println("\n--- Admin Login ---");
        String u = readNonEmptyLine("Username: ");
        String p = readNonEmptyLine("Password: ");

        for (int i = 0; i < adminSize; i++) {
            if (adminUser[i].equals(u) && adminPass[i].equals(p)) {
                loggedAdmin = i;
                System.out.println("Admin login successful.");
                return true;
            }
        }
        System.out.println("Invalid admin credentials.");
        return false;
    }

    static boolean customerLogin() {
        System.out.println("\n--- Customer Login ---");
        String name = readNonEmptyLine("Name: ");
        String p = readNonEmptyLine("Password: ");

        for (int i = 0; i < custSize; i++) {
            if (custName[i].equalsIgnoreCase(name) && custPass[i].equals(p)) {
                loggedCustomer = i;
                System.out.println("Customer login successful.");
                return true;
            }
        }
        System.out.println("Invalid customer credentials.");
        return false;
    }

    // CUSTOMER REGISTRATION
    static void registerCustomer() {
        System.out.println("\n--- Customer Registration ---");

        if (custSize >= MAX_CUSTOMERS) {
            System.out.println("Customer storage full. Contact admin.");
            return;
        }

        String name;
        while (true) {
            name = readNonEmptyLine("Enter full name: ");
            if (!isLettersAndSpaces(name)) {
                System.out.println("Invalid name. Use letters/spaces only.");
                continue;
            }
            if (findCustomerByName(name) != -1) {
                System.out.println("This name already exists. Try another.");
                continue;
            }
            break;
        }

        int age = readIntInRange("Enter age (18-80): ", 18, 80);

        String pass;
        while (true) {
            pass = readNonEmptyLine("Create password (min 4 chars: ");
            if (pass.length() < 4) {
                System.out.println("Password too short ");
                continue;
            }
            String confirm = readNonEmptyLine("Confirm password: ");
            if (!pass.equals(confirm)) {
                System.out.println("Passwords do not match.");
                continue;
            }
            break;
        }

        custName[custSize] = name;
        custAge[custSize] = age;
        custPass[custSize] = pass;
        custSize++;
        saveAllFiles();
        System.out.println("Registration successful. You can now login.");
    }

    // ADMIN RECORDS MENU (Add/View/Update/Search)
    static void adminRecordsMenu() {
        while (true) {
            System.out.println("\n--- Admin Records ---");
            System.out.println("1) Add Admin");
            System.out.println("2) View Admins");
            System.out.println("3) Update Admin");
            System.out.println("4) Search Admin");
            System.out.println("5) Back");

            int choice = readIntInRange("Select: ", 1, 5);

            if (choice == 1) adminAdd();
            else if (choice == 2) adminView();
            else if (choice == 3) adminUpdate();
            else if (choice == 4) adminSearch();
            else return;
        }
    }

    static void adminAdd() {
        System.out.println("\n[Add Admin]");
        if (adminSize >= MAX_ADMINS) {
            System.out.println("Admin limit reached.");
            return;
        }

        String u;
        while (true) {
            u = readNonEmptyLine("New username: ");
            if (!isUsernameSafe(u)) {
                System.out.println("Username must be letters/numbers/_ only.");
                continue;
            }
            if (findAdminByUser(u) != -1) {
                System.out.println("Username already exists.");
                continue;
            }
            break;
        }

        String p = readNonEmptyLine("New password: ");
        adminUser[adminSize] = u;
        adminPass[adminSize] = p;
        adminSize++;

        System.out.println("Admin added successfully.");
    }

    static void adminView() {
        System.out.println("\n[View Admins]");
        if (adminSize == 0) {
            System.out.println("No admin records.");
            return;
        }
        for (int i = 0; i < adminSize; i++) {
            System.out.println(i + ") " + adminUser[i]);
        }
    }

    static void adminUpdate() {
        System.out.println("\n[Update Admin]");
        adminView();
        if (adminSize == 0) return;

        int idx = readIntInRange("Enter admin index: ", 0, adminSize - 1);
        String newUser;
        while (true) {
            newUser = readNonEmptyLine("New username: ");
            if (!isUsernameSafe(newUser)) {
                System.out.println("Username must be letters/numbers/_ only.");
                continue;
            }
            int found = findAdminByUser(newUser);
            if (found != -1 && found != idx) {
                System.out.println("Username already taken by another admin.");
                continue;
            }
            break;
        }

        String newPass = readNonEmptyLine("New password: ");

        adminUser[idx] = newUser;
        adminPass[idx] = newPass;

        System.out.println("Admin updated.");
    }

    static void adminSearch() {
        System.out.println("\n[Search Admin]");
        String key = readNonEmptyLine("Enter username to search: ");
        int idx = findAdminByUser(key);
        if (idx == -1) {
            System.out.println("Admin not found.");
        } else {
            System.out.println("Found at index " + idx + ": " + adminUser[idx]);
        }
    }
    // CUSTOMER RECORDS MENU (Add/View/Update/Search)
    static void customerRecordsMenu() {
        while (true) {
            System.out.println("\n--- Customer Records ---");
            System.out.println("1) Add Customer");
            System.out.println("2) View Customers");
            System.out.println("3) Update Customer");
            System.out.println("4) Search Customer");
            System.out.println("5) Back");

            int choice = readIntInRange("Select: ", 1, 5);

            if (choice == 1) customerAddByAdmin();
            else if (choice == 2) customerView();
            else if (choice == 3) customerUpdate();
            else if (choice == 4) customerSearch();
            else return;
        }
    }

    static void customerAddByAdmin() {
        System.out.println("\n[Add Customer]");
        if (custSize >= MAX_CUSTOMERS) {
            System.out.println("Customer limit reached.");
            return;
        }

        String name;
        while (true) {
            name = readNonEmptyLine("Customer name: ");
            if (!isLettersAndSpaces(name)) {
                System.out.println("Invalid name. Use letters/spaces only.");
                continue;
            }
            if (findCustomerByName(name) != -1) {
                System.out.println("Customer name already exists.");
                continue;
            }
            break;
        }

        int age = readIntInRange("Age (18-80): ", 18, 80);
        String pass = readNonEmptyLine("Password: ");

        custName[custSize] = name;
        custAge[custSize] = age;
        custPass[custSize] = pass;
        custSize++;

        System.out.println("Customer added.");
    }

    static void customerView() {
        System.out.println("\n[View Customers]");
        if (custSize == 0) {
            System.out.println("No customers found.");
            return;
        }
        for (int i = 0; i < custSize; i++) {
            System.out.println(i + ") " + custName[i] + " | Age: " + custAge[i]);
        }
    }

    static void customerUpdate() {
        System.out.println("\n[Update Customer]");
        customerView();
        if (custSize == 0) return;

        int idx = readIntInRange("Enter customer index: ", 0, custSize - 1);

        String newName;
        while (true) {
            newName = readNonEmptyLine("New name: ");
            if (!isLettersAndSpaces(newName)) {
                System.out.println("Invalid name.");
                continue;
            }
            int found = findCustomerByName(newName);
            if (found != -1 && found != idx) {
                System.out.println("Name already used by another customer.");
                continue;
            }
            break;
        }

        int newAge = readIntInRange("New age (18-80): ", 18, 80);
        String newPass = readNonEmptyLine("New password: ");

        custName[idx] = newName;
        custAge[idx] = newAge;
        custPass[idx] = newPass;

        System.out.println("Customer updated.");
    }

    static void customerSearch() {
        System.out.println("\n[Search Customer]");
        String name = readNonEmptyLine("Enter name to search: ");
        int idx = findCustomerByName(name);
        if (idx == -1) {
            System.out.println("Customer not found.");
        } else {
            System.out.println("Found at index " + idx + ": " + custName[idx] + " | Age: " + custAge[idx]);
        }
    }

    // ROOM RECORDS MENU (Add/View/Update/Search)
    static void roomRecordsMenu() {
        while (true) {
            System.out.println("\n--- Room Records ---");
            System.out.println("1) Add Room");
            System.out.println("2) View Rooms");
            System.out.println("3) Update Room");
            System.out.println("4) Search Room");
            System.out.println("5) Back");

            int choice = readIntInRange("Select: ", 1, 5);

            if (choice == 1) roomAdd();
            else if (choice == 2) roomView();
            else if (choice == 3) roomUpdate();
            else if (choice == 4) roomSearch();
            else return;
        }
    }

    static void roomAdd() {
        System.out.println("\n[Add Room]");
        if (roomSize >= MAX_ROOMS) {
            System.out.println("Room storage full.");
            return;
        }

        int rn;
        while (true) {
            rn = readInt("Room number (positive): ");
            if (rn <= 0) {
                System.out.println("Room number must be positive.");
                continue;
            }
            if (findRoomByNumber(rn) != -1) {
                System.out.println("Room number already exists.");
                continue;
            }
            break;
        }

        int rate = readNonNegativeInt("Room rate (>=0): ");

        roomNo[roomSize] = rn;
        roomRate[roomSize] = rate;
        roomTaken[roomSize] = false;
        roomSize++;

        System.out.println("Room added.");
    }

    static void roomView() {
        System.out.println("\n[View Rooms]");
        if (roomSize == 0) {
            System.out.println("No rooms available in system.");
            return;
        }
        for (int i = 0; i < roomSize; i++) {
            System.out.println(i + ") Room " + roomNo[i] +
                    " | Rate: " + roomRate[i] +
                    " | Status: " + (roomTaken[i] ? "Occupied" : "Available"));
        }
    }

    static void roomUpdate() {
        System.out.println("\n[Update Room]");
        roomView();
        if (roomSize == 0) return;

        int idx = readIntInRange("Enter room index: ", 0, roomSize - 1);

        int newRate = readNonNegativeInt("New rate (>=0): ");
        int status = readIntInRange("Status (1=Available, 2=Occupied): ", 1, 2);

        roomRate[idx] = newRate;
        roomTaken[idx] = (status == 2);

        System.out.println("Room updated.");
    }

    static void roomSearch() {
        System.out.println("\n[Search Room]");
        int rn = readInt("Enter room number: ");
        int idx = findRoomByNumber(rn);
        if (idx == -1) {
            System.out.println("Room not found.");
        } else {
            System.out.println("Found: Room " + roomNo[idx] + " | Rate: " + roomRate[idx] +
                    " | " + (roomTaken[idx] ? "Occupied" : "Available") + " | Index: " + idx);
        }
    }

    static void showAvailableRooms() {
        System.out.println("\n[Available Rooms]");
        boolean any = false;
        for (int i = 0; i < roomSize; i++) {
            if (!roomTaken[i]) {
                any = true;
                System.out.println("Room " + roomNo[i] + " | Rate: " + roomRate[i]);
            }
        }
        if (!any) System.out.println("No available rooms right now.");
    }

    // MENU RECORDS MENU (Add/View/Update/Search)
    static void menuRecordsMenu() {
        while (true) {
            System.out.println("\n--- Food Menu Records ---");
            System.out.println("1) Add Item");
            System.out.println("2) View Items");
            System.out.println("3) Update Item");
            System.out.println("4) Search Item");
            System.out.println("5) Back");

            int choice = readIntInRange("Select: ", 1, 5);

            if (choice == 1) menuAdd();
            else if (choice == 2) menuView();
            else if (choice == 3) menuUpdate();
            else if (choice == 4) menuSearch();
            else return;
        }
    }

    static void menuAdd() {
        System.out.println("\n[Add Menu Item]");
        if (menuSize >= MAX_MENU) {
            System.out.println("Menu storage full.");
            return;
        }

        String name;
        while (true) {
            name = readNonEmptyLine("Item name: ");

            if (!isLettersAndSpaces(name)) {
                System.out.println("Invalid item name. Use letters and spaces only.");
                continue;
            }

            if (findMenuByName(name) != -1) {
                System.out.println("This item already exists.");
                continue;
            }
            break;
        }

        int price = readNonNegativeInt("Item price (>=0): ");

        itemName[menuSize] = name;
        itemPrice[menuSize] = price;
        menuSize++;

        System.out.println("Menu item added.");
    }

    static void menuView() {
        System.out.println("\n[View Menu]");
        if (menuSize == 0) {
            System.out.println("Menu is empty.");
            return;
        }
        for (int i = 0; i < menuSize; i++) {
            System.out.println(i + ") " + itemName[i] + " | Price: " + itemPrice[i]);
        }
    }

    static void menuUpdate() {
        System.out.println("\n[Update Menu Item]");
        menuView();
        if (menuSize == 0) return;

        int idx = readIntInRange("Enter item index: ", 0, menuSize - 1);

        String newName;
        while (true) {
            newName = readNonEmptyLine("New name: ");

            if (!isLettersAndSpaces(newName)) {
                System.out.println("Invalid item name. Use letters and spaces only.");
                continue;
            }

            int found = findMenuByName(newName);
            if (found != -1 && found != idx) {
                System.out.println("Another item already uses this name.");
                continue;
                }
            break;
            }

        int newPrice = readNonNegativeInt("New price (>=0): ");

        itemName[idx] = newName;
        itemPrice[idx] = newPrice;

        System.out.println("Menu item updated.");
    }
    static void menuSearch() {
        System.out.println("\n[Search Menu Item]");
        String name = readNonEmptyLine("Enter item name: ");
        int idx = findMenuByName(name);
        if (idx == -1) {
            System.out.println("Item not found.");
        } else {
            System.out.println("Found: " + itemName[idx] + " | Price: " + itemPrice[idx] + " | Index: " + idx);
        }
    }

    // BILLING RECORDS MENU (Add/View/Update/Search)
    static void billingRecordsMenu() {
        while (true) {
            System.out.println("\n--- Billing System ---");
            System.out.println("1) Add Bill (Admin booking)");
            System.out.println("2) View Bills");
            System.out.println("3) Update Bill (Paid status only)");
            System.out.println("4) Search Bill");
            System.out.println("5) Back");

            int choice = readIntInRange("Select: ", 1, 5);

            if (choice == 1) adminCreateBill();
            else if (choice == 2) billViewAll();
            else if (choice == 3) billUpdatePaid();
            else if (choice == 4) billSearch();
            else return;
        }
    }

    // CUSTOMER BOOKING AND BILL GENERATING
    static void customerBookRoom() {
        System.out.println("\n[Book Room]");
        if (loggedCustomer == -1) {
            System.out.println("Please login first.");
            return;
        }

        int active = findActiveBillForCustomer(loggedCustomer);
        if (active != -1) {
            System.out.println("You already have an active booking (Bill ID: " + billId[active] + "). Checkout first.");
            return;
        }

        showAvailableRooms();
        if (!hasAnyAvailableRoom()) return;

        int number = readInt("Enter room number to book: ");
        int rIdx = findRoomByNumber(number);

        if (rIdx == -1) {
            System.out.println("Room does not exist.");
            return;
        }
        if (roomTaken[rIdx]) {
            System.out.println("Room is already occupied.");
            return;
        }

        int nights = readIntInRange("Number of nights (1-30): ", 1, 30);

        // Create bill
        createBillInternal(loggedCustomer, rIdx, nights);

        System.out.println("Room booked successfully. Bill created.");
    }

    // ADMIN CREATING BILLS
    static void adminCreateBill() {
        System.out.println("\n[Admin Create Bill / Booking]");
        customerView();
        if (custSize == 0) return;

        int cIdx = readIntInRange("Select customer index: ", 0, custSize - 1);

        int active = findActiveBillForCustomer(cIdx);
        if (active != -1) {
            System.out.println("Customer already has an active booking (Bill ID: " + billId[active] + ").");
            return;
        }

        showAvailableRooms();
        if (!hasAnyAvailableRoom()) return;

        int number = readInt("Enter room number: ");
        int rIdx = findRoomByNumber(number);

        if (rIdx == -1 || roomTaken[rIdx]) {
            System.out.println("Invalid room or room occupied.");
            return;
        }

        int nights = readIntInRange("Nights (1-30): ", 1, 30);

        createBillInternal(cIdx, rIdx, nights);
        System.out.println("Bill created for customer successfully.");
    }

    // CREATE BILL INTERNAL
    static void createBillInternal(int cIdx, int rIdx, int nights) {
        if (billSize >= MAX_BILLS) {
            System.out.println("Bill storage full.");
            return;
        }

        // Mark room occupied
        roomTaken[rIdx] = true;

        // Assign bill fields
        billId[billSize] = nextBillId();
        billCustomerIndex[billSize] = cIdx;
        billRoomIndex[billSize] = rIdx;
        billNights[billSize] = nights;

        int roomCost = roomRate[rIdx] * nights;
        billRoomCost[billSize] = roomCost;

        billFoodCost[billSize] = 0;
        billTotal[billSize] = roomCost;
        billPaid[billSize] = false;

        // Reset 2D quantities for this bill
        for (int j = 0; j < menuSize; j++) {
            billFoodQty[billSize][j] = 0;
        }

        billSize++;
    }

    // CUSTOMER WORK OF ORDER FOOD
    static void customerOrderFood() {
        System.out.println("\n[Order Food]");
        if (loggedCustomer == -1) {
            System.out.println("Please login first.");
            return;
        }

        int bIdx = findActiveBillForCustomer(loggedCustomer);
        if (bIdx == -1) {
            System.out.println("No active booking found. Book a room first.");
            return;
        }

        if (menuSize == 0) {
            System.out.println("Menu is empty.");
            return;
        }

        while (true) {
            menuView();
            System.out.println("Enter -1 to stop ordering.");

            int itemIdx = readInt("Select item index: ");
            if (itemIdx == -1) break;

            if (itemIdx < 0 || itemIdx >= menuSize) {
                System.out.println("Invalid item index.");
                continue;
            }

            int qty = readIntInRange("Quantity (1-20): ", 1, 20);

            // Update 2D quantity
            billFoodQty[bIdx][itemIdx] += qty;

            // Recalculate food cost
            recalcFoodAndTotal(bIdx);

            System.out.println("Added: " + itemName[itemIdx] + " x " + qty);
        }
    }

    // VIEW BILL (CUSTOMER)
    static void customerViewOwnBill() {
        System.out.println("\n[My Bill]");
        if (loggedCustomer == -1) {
            System.out.println("Please login first.");
            return;
        }

        int bIdx = findActiveBillForCustomer(loggedCustomer);
        if (bIdx == -1) {
            // maybe paid bill exists, so show most recent
            int recent = findMostRecentBillForCustomer(loggedCustomer);
            if (recent == -1) {
                System.out.println("No bills found.");
            } else {
                printBill(recent);
            }
            return;
        }
        printBill(bIdx);
    }

    // CHECKOUT AND MARK PAID AND REE ROOM NOW
    static void customerCheckout() {
        System.out.println("\n[Checkout]");
        if (loggedCustomer == -1) {
            System.out.println("Please login first.");
            return;
        }

        int bIdx = findActiveBillForCustomer(loggedCustomer);
        if (bIdx == -1) {
            System.out.println("No active bill found.");
            return;
        }

        printBill(bIdx);
        int confirm = readIntInRange("Confirm checkout? (1=Yes, 2=No): ", 1, 2);
        if (confirm == 2) return;

        billPaid[bIdx] = true;

        // Free room
        int rIdx = billRoomIndex[bIdx];
        if (rIdx >= 0 && rIdx < roomSize) roomTaken[rIdx] = false;

        System.out.println("Checkout complete. Bill marked as PAID and room is now AVAILABLE.");
    }

    // BILL RECORDS (ADMIN)
    static void billViewAll() {
        System.out.println("\n[View Bills]");
        if (billSize == 0) {
            System.out.println("No bills in system.");
            return;
        }
        for (int i = 0; i < billSize; i++) {
            System.out.println("Index " + i + " | BillID: " + billId[i] +
                    " | Customer: " + safeCustName(i) +
                    " | Room: " + safeRoomNo(i) +
                    " | Total: " + billTotal[i] +
                    " | " + (billPaid[i] ? "PAID" : "UNPAID"));
        }
    }

    static void billUpdatePaid() {
        System.out.println("\n[Update Bill]");
        billViewAll();
        if (billSize == 0) return;

        int idx = readIntInRange("Select bill index: ", 0, billSize - 1);

        int choice = readIntInRange("Set status (1=UNPAID, 2=PAID): ", 1, 2);
        billPaid[idx] = (choice == 2);

        // If marked PAID, free the room; if marked UNPAID, keep room occupied
        int rIdx = billRoomIndex[idx];
        if (rIdx >= 0 && rIdx < roomSize) {
            roomTaken[rIdx] = !billPaid[idx];
        }

        System.out.println("Bill status updated.");
    }

    static void billSearch() {
        System.out.println("\n[Search Bill]");
        int id = readInt("Enter Bill ID: ");

        int idx = findBillById(id);
        if (idx == -1) {
            System.out.println("Bill not found.");
        } else {
            printBill(idx);
        }
    }

    // BILL PRINTING + RECALCULATE
    static void printBill(int bIdx) {
        System.out.println("\n-------------------- BILL --------------------");
        System.out.println("Bill ID      : " + billId[bIdx]);
        System.out.println("Customer     : " + custName[billCustomerIndex[bIdx]]);
        System.out.println("Age          : " + custAge[billCustomerIndex[bIdx]]);
        System.out.println("Room         : " + roomNo[billRoomIndex[bIdx]]);
        System.out.println("Rate/Night   : " + roomRate[billRoomIndex[bIdx]]);
        System.out.println("Nights       : " + billNights[bIdx]);
        System.out.println("Room Cost    : " + billRoomCost[bIdx]);
        System.out.println("\nFood Details :");
        boolean any = false;
        for (int j = 0; j < menuSize; j++) {
            if (billFoodQty[bIdx][j] > 0) {
                any = true;
                int line = billFoodQty[bIdx][j] * itemPrice[j];
                System.out.println("  - " + itemName[j] + " x " + billFoodQty[bIdx][j] + " = " + line);
            }
        }
        if (!any) System.out.println("  (No food ordered)");

        System.out.println("\nFood Cost    : " + billFoodCost[bIdx]);
        System.out.println("TOTAL        : " + billTotal[bIdx]);
        System.out.println("STATUS       : " + (billPaid[bIdx] ? "PAID" : "UNPAID"));
        System.out.println("----------------------------------------------");
    }

    static void recalcFoodAndTotal(int bIdx) {
        int sum = 0;
        for (int j = 0; j < menuSize; j++) {
            int qty = Math.abs(billFoodQty[bIdx][j]);
            sum += qty * itemPrice[j];
        }
        billFoodCost[bIdx] = sum;
        billTotal[bIdx] = billRoomCost[bIdx] + billFoodCost[bIdx];
    }
    // SEARCH HELPERS
    static int findAdminByUser(String u) {
        for (int i = 0; i < adminSize; i++) {
            if (adminUser[i].equalsIgnoreCase(u)) return i;
        }
        return -1;
    }

    static int findCustomerByName(String name) {
        for (int i = 0; i < custSize; i++) {
            if (custName[i].equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static int findRoomByNumber(int number) {
        for (int i = 0; i < roomSize; i++) {
            if (roomNo[i] == number) return i;
        }
        return -1;
    }

    static int findMenuByName(String name) {
        for (int i = 0; i < menuSize; i++) {
            if (itemName[i].equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    static int findBillById(int id) {
        for (int i = 0; i < billSize; i++) {
            if (billId[i] == id) return i;
        }
        return -1;
    }

    static int findActiveBillForCustomer(int cIdx) {
        for (int i = 0; i < billSize; i++) {
            if (billCustomerIndex[i] == cIdx && !billPaid[i]) return i;
        }
        return -1;
    }

    static int findMostRecentBillForCustomer(int cIdx) {
        int last = -1;
        for (int i = 0; i < billSize; i++) {
            if (billCustomerIndex[i] == cIdx) last = i;
        }
        return last;
    }

    static boolean hasAnyAvailableRoom() {
        for (int i = 0; i < roomSize; i++){
            if (!roomTaken[i]) 
                return true;
        }
        System.out.println("No available rooms right now.");
        return false;
    }

    // VALIDATION UTILITIES (String, Character usage)
    static boolean isLettersAndSpaces(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!(Character.isLetter(ch) || ch == ' ')) return false;
        }
        return true;
    }

    static boolean isUsernameSafe(String s) {
    boolean hasLetter = false;
    for (int i = 0; i < s.length(); i++) {
        char ch = s.charAt(i);
        if (Character.isLetter(ch)) {
            hasLetter = true;
        }
        if (!(Character.isLetterOrDigit(ch) || ch == '_')) return false;
    }
    return s.length() >= 3 && hasLetter;
}


    // SAFE INPUT METHODS (Exception Handling, never crash)
    static String readNonEmptyLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            if (line.length() == 0) {
                System.out.println("Input cannot be empty.");
                continue;
            }
            return line;
        }
    }

    static int readInt(String prompt) {
    while (true) {
        System.out.print(prompt);
        String line = input.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter a valid integer number.");
        }
    }
}


    static int readNonNegativeInt(String prompt) {
        while (true) {
            int v = readInt(prompt);
            if (v < 0) {
                System.out.println("Value cannot be negative.");
                continue;
            }
            return v;
        }
    }

    static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int v = readInt(prompt);
            if (v < min || v > max) {
                System.out.println("Enter between " + min + " and " + max + ".");
                continue;
            }
            return v;
        }
    }
    // DEFAULT DATA
    static void defaultDataIfEmpty() {
        // Seed one admin if none exists
        if (adminSize == 0) {
            adminUser[0] = "admin";
            adminPass[0] = "admin123";
            adminSize = 1;
        }

        // Default rooms if empty
        if (roomSize == 0) {
            int[] demoRooms = {101, 102, 103, 201, 202, 203};
            int[] demoRates = {1200, 1200, 1500, 1800, 2000, 2500};
            for (int i = 0; i < demoRooms.length && roomSize < MAX_ROOMS; i++) {
                roomNo[roomSize] = demoRooms[i];
                roomRate[roomSize] = demoRates[i];
                roomTaken[roomSize] = false;
                roomSize++;
            }
        }

        // Default menu if empty
        if (menuSize == 0) {
            String[] demoItems = {"Tea", "Coffee", "Sandwich", "Burger", "Pizza"};
            int[] demoPrices = {30, 60, 150, 250, 450};
            for (int i = 0; i < demoItems.length && menuSize < MAX_MENU; i++) {
                itemName[menuSize] = demoItems[i];
                itemPrice[menuSize] = demoPrices[i];
                menuSize++;
            }
        }
    }

    static int nextBillId() {
        int id;
        do {
            id = 1000 + (int)(Math.random() * 9000); 
        } while (findBillById(id) != -1); 
        return id;
    }
    static String safeCustName(int billIndex) {
        int ci = billCustomerIndex[billIndex];
        if (ci >= 0 && ci < custSize) return custName[ci];
        return "Unknown";
    }

    static String safeRoomNo(int billIndex) {
        int ri = billRoomIndex[billIndex];
        if (ri >= 0 && ri < roomSize) return "" + roomNo[ri];
        return "Unknown";
    }

    static void loadAllFiles() {
        loadAdmins();
        loadCustomers();
        loadRooms();
        loadMenu();
        loadBills();
        loadBillQty();
    }

    static void saveAllFiles() {
        saveAdmins();
        saveCustomers();
        saveRooms();
        saveMenu();
        saveBills();
        saveBillQty();
    }

    // FILE HELPER
    static String readWholeFile(String filename) {
        String content = "";
        try {
            FileReader fr = new FileReader(filename);
            int ch;
            while ((ch = fr.read()) != -1) {
                content = content + (char) ch;
            }
            fr.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename + ". A new file will be created.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }

        return content;
    }

    static void writeWholeFile(String filename, String content) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(content);
            fw.close();
        } catch (IOException e) {
            System.out.println("Error writing file: " + filename);
        }

    }

    static String[] splitLines(String text) {
        return text.replace("\r\n", "").split("\n");
    }

    // ADMINS
    static void loadAdmins() {
        String data = readWholeFile(FILE_ADMINS);
        String[] lines = splitLines(data);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.length() == 0) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            if (adminSize >= MAX_ADMINS) break;
            adminUser[adminSize] = parts[0];
            adminPass[adminSize] = parts[1];
            adminSize++;
        }
    }

    static void saveAdmins() {
    String data = "";
    for (int i = 0; i < adminSize; i++) {
        data = data + adminUser[i] + "," + adminPass[i] + "\n";
    }
    writeWholeFile(FILE_ADMINS, data);
}


    //CUSTOMERS
    static void loadCustomers() {
        String data = readWholeFile(FILE_CUSTOMERS);
        String[] lines = splitLines(data);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.length() == 0) continue;

            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            if (custSize >= MAX_CUSTOMERS) break;

            custName[custSize] = parts[0];
            try {
                custAge[custSize] = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid age format found in file. Default age set to 18.");
                custAge[custSize] = 18;
        }

            custPass[custSize] = parts[2];
            custSize++;
        }
    }

    static void saveCustomers() {
    String data = "";
    for (int i = 0; i < custSize; i++) {
        data = data + custName[i] + "," 
                    + custAge[i] + "," 
                    + custPass[i] + "\n";
    }
    writeWholeFile(FILE_CUSTOMERS, data);
}

    //ROOMS
    static void loadRooms() {
        String data = readWholeFile(FILE_ROOMS);
        String[] lines = splitLines(data);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.length() == 0) continue;

            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            if (roomSize >= MAX_ROOMS) break;

            try {
                roomNo[roomSize] = Integer.parseInt(parts[0]);
                roomRate[roomSize] = Integer.parseInt(parts[1]);
                roomTaken[roomSize] = parts[2].equals("1");
                roomSize++;
            } catch (NumberFormatException e) {
                System.out.println("Invalid room number or rate found. Record skipped.");
            }

        }
    }

    static void saveRooms() {
        String data = "";
        for (int i = 0; i < roomSize; i++) {
            data = data + roomNo[i] + ","
                + roomRate[i] + ","
                + (roomTaken[i] ? "1" : "0") + "\n";
            }
            writeWholeFile(FILE_ROOMS, data);
    }

    //MENU
    static void loadMenu() {
        String data = readWholeFile(FILE_MENU);
        String[] lines = splitLines(data);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.length() == 0) continue;

            String[] parts = line.split(",");
            if (parts.length < 2) continue;

            if (menuSize >= MAX_MENU) break;

            itemName[menuSize] = parts[0];
            try {
                itemPrice[menuSize] = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid menu price found. Price set to 0.");
                itemPrice[menuSize] = 0;
        }

            menuSize++;
        }
    }

   static void saveMenu() {
    String data = "";
    for (int i = 0; i < menuSize; i++) {
        data = data + itemName[i] + ","
                    + itemPrice[i] + "\n";
    }
    writeWholeFile(FILE_MENU, data);
}


    //BILLS
    static void loadBills() {
        String data = readWholeFile(FILE_BILLS);
        String[] lines = splitLines(data);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.length() == 0) continue;

            // Bill format:
            // billId,custIndex,roomIndex,nights,roomCost,foodCost,total,paid(1/0)
            String[] parts = line.split(",");
            if (parts.length < 8) continue;
            if (billSize >= MAX_BILLS) break;

            try {
                billId[billSize] = Integer.parseInt(parts[0]);
                billCustomerIndex[billSize] = Integer.parseInt(parts[1]);
                billRoomIndex[billSize] = Integer.parseInt(parts[2]);
                billNights[billSize] = Integer.parseInt(parts[3]);
                billRoomCost[billSize] = Integer.parseInt(parts[4]);
                billFoodCost[billSize] = Integer.parseInt(parts[5]);
                billTotal[billSize] = Integer.parseInt(parts[6]);
                billPaid[billSize] = parts[7].equals("1");
                billSize++;
            } catch (NumberFormatException e) {
                System.out.println("Invalid bill data found in file. Record skipped.");
            }

        }
    }

    static void saveBills() {
    String data = "";
    for (int i = 0; i < billSize; i++) {
        data = data + billId[i] + ","
                    + billCustomerIndex[i] + ","
                    + billRoomIndex[i] + ","
                    + billNights[i] + ","
                    + billRoomCost[i] + ","
                    + billFoodCost[i] + ","
                    + billTotal[i] + ","
                    + (billPaid[i] ? "1" : "0") + "\n";
    }
    writeWholeFile(FILE_BILLS, data);
}

    //2D QUANTITY FILE
    static void loadBillQty() {
        String data = readWholeFile(FILE_QTY);
        String[] lines = splitLines(data);

        // Each line: billIndex,itemIndex,qty
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.length() == 0) continue;

            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            try {
                int b = Integer.parseInt(parts[0]);
                int it = Integer.parseInt(parts[1]);
                int q = Integer.parseInt(parts[2]);

                if (b >= 0 && b < MAX_BILLS && it >= 0 && it < MAX_MENU) {
                    billFoodQty[b][it] = q;
            }
            } catch (NumberFormatException e) {
                System.out.println("Invalid food quantity data found. Entry skipped.");
            }       

        }
    }

    static void saveBillQty() {
    String data = "";

    for (int b = 0; b < billSize; b++) {
        for (int it = 0; it < menuSize; it++) {
            if (billFoodQty[b][it] != 0) {
                data = data + b + ","
                            + it + ","
                            + billFoodQty[b][it] + "\n";
            }
        }
    }
    writeWholeFile(FILE_QTY, data);
}
}


