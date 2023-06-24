import java.util.Scanner;

class VendingMachine {
    private Item[] items;
    private int[] inventory;
    private int[] prices;
    private int[] calories;
    private int currentTransactionId;
    private int totalSales;
    private int[] denominations;
    private int[] changeInventory;

    public VendingMachine() {
        items = new Item[10];
        inventory = new int[10];
        prices = new int[10];
        calories = new int[10];
        currentTransactionId = 1;
        totalSales = 0;
        denominations = new int[]{1, 5, 10, 20, 50, 100};
        changeInventory = new int[denominations.length];
        initializeChangeInventory();
    }

    private void initializeChangeInventory() {
        for (int i = 0; i < denominations.length; i++) {
            changeInventory[i] = 10;
        }
    }

    public void addNewItem(String name, int price, int calories, int initialQuantity) {
        int id = findNextAvailableItemId();
        if (id != -1) {
            Item item = new Item(id, name, price, calories, initialQuantity);
            items[id] = item;
            inventory[id] = initialQuantity;
            prices[id] = price;
            this.calories[id] = calories;
        } else {
            System.out.println("Vending machine is full. Cannot add new item.");
        }
    }

    private int findNextAvailableItemId() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public void restockItem(String name, int quantity) {
        int itemId = findItemIdByName(name);
        if (itemId != -1) {
            int currentQuantity = inventory[itemId];
            inventory[itemId] = currentQuantity + quantity;
        } else {
            System.out.println("Item not found.");
        }
    }

    public void setItemPrice(String name, int price) {
        int itemId = findItemIdByName(name);
        if (itemId != -1) {
            prices[itemId] = price;
        } else {
            System.out.println("Item not found.");
        }
    }

    private int findItemIdByName(String name) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public void displayItems() {
        System.out.println("Available items:");
        System.out.println("------------------------------");
        System.out.printf("%-10s %-10s %-10s %-10s\n", "Slot", "Item", "Stock", "Price");
        System.out.println("------------------------------");
        for (Item item : items) {
            if (item != null) {
                System.out.printf("%-10s %-10s %-10d %-10d\n" , item.getId()+1, item.getName(), inventory[item.getId()], prices[item.getId()]);
            }
        }

        System.out.println();
    }

    public void startTransaction() {
        System.out.println("Please select an item or enter 0 for change:");
        displayItems();
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        if (choice == 0) {
            produceChange();
            return;
        }
        Item selected = getItemByChoice(choice);
        if (selected != null) {
            System.out.println("Selected item: " + selected.getName());
            processPayment(selected);
        } else {
            System.out.println("Invalid choice!");
        }
    }

    private Item getItemByChoice(int choice) {
        choice--; // Adjust choice to match array index
        if (choice >= 0 && choice < items.length) {
            return items[choice];
        }
        return null;
    }

    private void processPayment(Item item) {
        System.out.println("Enter payment amount:");
        Scanner scanner = new Scanner(System.in);
        int paymentAmount = scanner.nextInt();
        if (paymentAmount < prices[item.getId()]) {
            System.out.println("Insufficient payment!");
            return;
        }

        int changeAmount = paymentAmount - prices[item.getId()];
        if (hasSufficientChange(changeAmount)) {
            inventory[item.getId()]--;
            totalSales += prices[item.getId()];
            giveChange(changeAmount);
            System.out.println("Transaction successful! Enjoy your " + item.getName());
        } else {
            System.out.println("Sorry, not enough change available.");
        }
    }

    private boolean hasSufficientChange(int changeAmount) {
        int remainingChange = changeAmount;
        for (int i = denominations.length - 1; i >= 0; i--) {
            int denomination = denominations[i];
            int availableNotes = changeInventory[i];
            int requiredNotes = remainingChange / denomination;
            if (requiredNotes > availableNotes) {
                requiredNotes = availableNotes;
            }
            remainingChange -= denomination * requiredNotes;
        }
        return remainingChange == 0;
    }

    private void giveChange(int changeAmount) {
        int remainingChange = changeAmount;
        for (int i = denominations.length - 1; i >= 0; i--) {
            int denomination = denominations[i];
            int availableNotes = changeInventory[i];
            int requiredNotes = remainingChange / denomination;
            if (requiredNotes > availableNotes) {
                requiredNotes = availableNotes;
            }
            remainingChange -= denomination * requiredNotes;
            changeInventory[i] = availableNotes - requiredNotes;
            System.out.println("Dispensing " + requiredNotes + " notes of " + denomination + " denomination.");
        }
    }

    private void produceChange() {
        System.out.println("Enter change amount:");
        Scanner scanner = new Scanner(System.in);
        int changeAmount = scanner.nextInt();
        if (hasSufficientChange(changeAmount)) {
            giveChange(changeAmount);
            System.out.println("Change produced.");
        } else {
            System.out.println("Sorry, not enough change available.");
        }
    }

    public void printTransactionSummary() {
        System.out.println("Transaction Summary:");
        System.out.println("Total Sales: " + totalSales);
        System.out.println("Item Quantity:");
        for (Item item : items) {
            if (item != null) {
                System.out.println(item.getName() + ": " + (item.getInitialQuantity() - inventory[item.getId()]));
            }
        }
        System.out.println();
    }
}

class Item {
    private int id;
    private String name;
    private int price;
    private int calories;
    private int initialQuantity;

    public Item(int id, String name, int price, int calories, int initialQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.calories = calories;
        this.initialQuantity = initialQuantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getCalories() {
        return calories;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }
}

public class RegVend {
    public static void main(String[] args) {
        VendingMachine vendingMachine = new VendingMachine();
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("Vending Machine Menu:");
            System.out.println("1. Add New Item");
            System.out.println("2. Restock Item");
            System.out.println("3. Set Item Price");
            System.out.println("4. Start Transaction");
            System.out.println("5. Display Items");
            System.out.println("6. Print Transaction Summary");
            System.out.println("7. Exit");
            System.out.println("Enter your choice:");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Enter item details:");
                    scanner.nextLine(); // Consume newline character
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Price: ");
                    int price = scanner.nextInt();
                    System.out.print("Calories: ");
                    int calories = scanner.nextInt();
                    System.out.print("Initial Quantity: ");
                    int quantity = scanner.nextInt();
                    vendingMachine.addNewItem(name, price, calories, quantity);
                    break;
                case 2:
                    System.out.print("Enter item name to restock: ");
                    String restockName = scanner.next();
                    System.out.print("Enter quantity to restock: ");
                    int restockQuantity = scanner.nextInt();
                    vendingMachine.restockItem(restockName, restockQuantity);
                    break;
                case 3:
                    System.out.print("Enter item name to set price: ");
                    String priceName = scanner.next();
                    System.out.print("Enter new price: ");
                    int newPrice = scanner.nextInt();
                    vendingMachine.setItemPrice(priceName, newPrice);
                    break;
                case 4:
                    vendingMachine.startTransaction();
                    break;
                case 5:
                    vendingMachine.displayItems();
                    break;
                case 6:
                    vendingMachine.printTransactionSummary();
                    break;
                case 7:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
