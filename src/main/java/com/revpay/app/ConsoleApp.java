package com.revpay.app;

import com.revpay.model.MoneyRequest;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.service.*;
//import com.revpay.service.PinService;

import java.util.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);

    private final AuthService authService = new AuthService();
    private final WalletService walletService = new WalletService();
    private final TransactionService transactionService = new TransactionService();
    private final MoneyRequestService moneyRequestService = new MoneyRequestService();
    private final PinService pinService = new PinService();
    private final PaymentMethodService paymentService = new PaymentMethodService();
    private final InvoiceService invoiceService = new InvoiceService();
    private final BusinessProfileService businessService = new BusinessProfileService();
    private final LoanService loanService = new LoanService();
    private final NotificationService notificationService = new NotificationService();





    public void start() {

        while (true) {

            System.out.println("\n====== REV PAY ======");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice;

            try {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Please enter a number.");
                    continue;
                }

                choice = Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter numbers only.");
                continue;
            }

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> {
                    System.out.println("Bye");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }



    // ================= REGISTER =================
    private void register() {

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        long id = authService.register(username, name, email, phone, password, "PERSONAL");

        System.out.println(" Registered! User ID: " + id);
    }


    // ================= LOGIN =================
    private void login() {

        System.out.print("Email/Phone: ");
        String input = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = authService.login(input, password);

        if (userOpt.isEmpty()) {
            System.out.println(" Invalid credentials");
            return;
        }

        walletMenu(userOpt.get());
    }


    // ================= WALLET MENU =================
    private void walletMenu(User user) {

        while (true) {

            System.out.println("\n--- WALLET MENU ---");

            boolean business = "Business".equalsIgnoreCase(user.getAccountType());

            LinkedHashMap<String, Runnable> menu = new LinkedHashMap<>();

            // Wallet
            menu.put("Create Wallet", () -> createWallet(user));
            menu.put("Check Balance", () -> checkBalance(user));
            menu.put("Add Money", () -> addMoney(user));
            menu.put("Withdraw", () -> withdraw(user));
            menu.put("Send Money", () -> sendMoney(user));

            // Transactions
            menu.put("View Transactions", () -> history(user));
            menu.put("Request Money", () -> requestMoney(user));
            menu.put("Incoming Requests", () -> incoming(user));
            menu.put("Outgoing Requests", () -> outgoing(user));
            menu.put("Accept Request", () -> accept(user));
            menu.put("Decline Request", () -> decline(user));

            // Cards
            menu.put("Set/Change PIN", () -> setPin(user));
            menu.put("Add Card", () -> addCard(user));
            menu.put("List Cards", () -> listCards(user));
            menu.put("Topup Wallet (Card)", () -> topup(user));
            menu.put("Upgrade to Business Account", () -> upgradeBusiness(user));

            // Business only
            if (business) {
                menu.put("Create Invoice", () -> createInvoice(user));
                menu.put("Add Item to Invoice", () -> addInvoiceItem(user));
                menu.put("List My Invoices", () -> listInvoices(user));
                menu.put("View Invoice Items", () -> viewItems(user));
                menu.put("Apply Loan", () -> applyLoan(user));
                menu.put("List Loans", () -> listLoans(user));
//                menu.put("Approve Loan", () -> approveLoan(user));
                menu.put("Repay Loan", () -> repayLoan(user));
                menu.put("Pay Invoice", () -> payInvoice(user));
            }

            menu.put("View Notifications", () -> viewNotifications(user));
            menu.put("LogOut", () -> {});

            // print dynamically
            int option = 1;
            for (String label : menu.keySet()) {
                System.out.println(option++ + ". " + label);
            }
            int choice = readChoice(menu.size());

            if (choice == menu.size()) return;

            new ArrayList<>(menu.values()).get(choice - 1).run();
        }
    }



    // ================= WALLET =================

    private void createWallet(User user) {
        walletService.createWallet(user.getId());
        System.out.println("Wallet ready!");
    }

    private void checkBalance(User user) {
        System.out.println("Balance: ₹" + walletService.getBalance(user.getId()));
    }

    private void addMoney(User user) {
        System.out.print("Enter amount: ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Invalid amount");
            return;
        }
        BigDecimal amt = new BigDecimal(input);
        walletService.addMoney(user.getId(), amt);
        System.out.println(" Money added!");
    }

    private void withdraw(User user) {
        System.out.print("Enter amount: ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Invalid amount");
            return;
        }

        BigDecimal amt = new BigDecimal(input);
        boolean ok = walletService.withdraw(user.getId(), amt);
        if (ok)
            System.out.println(" Withdraw successful!");
        else
            System.out.println(" Insufficient funds!");
    }



    // ================= TRANSFER =================

//    private void sendMoney(User user) {
//        long to = Long.parseLong(scanner.nextLine());
//        BigDecimal amt = new BigDecimal(scanner.nextLine());
//
//        boolean ok = transactionService.sendMoney(user.getId(), to, amt);
//
//        System.out.println(ok ? "Transfer success" : "Failed");
//    }

    private void sendMoney(User user) {

        if (!verifyPin(user)) return;

        System.out.print("Enter receiver user ID: ");
        long to = Long.parseLong(scanner.nextLine());

        System.out.print("Enter amount: ");
        BigDecimal amt = new BigDecimal(scanner.nextLine());

        boolean ok = transactionService.sendMoney(user.getId(), to, amt);

        System.out.println(ok ? "Transfer success" : "Failed");
    }


    private void history(User user) {
        List<Transaction> list = transactionService.getHistory(user.getId());

        list.forEach(System.out::println);
    }


    // ================= MONEY REQUEST =================

    private void requestMoney(User user) {

        System.out.print("Enter payer user ID: ");
        long to = Long.parseLong(scanner.nextLine());

        System.out.print("Amount: ");
        BigDecimal amt = new BigDecimal(scanner.nextLine());

        long id = moneyRequestService.createRequest(user.getId(), to, amt, "Request");

        System.out.println("Request created ID: " + id);
    }

    private void incoming(User user) {

        List<MoneyRequest> list = moneyRequestService.getIncoming(user.getId());

        if (list.isEmpty()) {
            System.out.println("No incoming requests.");
            return;
        }

        System.out.println("\n--- Incoming Requests ---");

        for (MoneyRequest r : list) {
            System.out.println(
                    "ID: " + r.getId() +
                            " | From User: " + r.getFromUserId() +
                            " | Amount: ₹" + r.getAmount() +
                            " | Status: " + r.getStatus()
            );
        }
    }



    private void outgoing(User user) {
        List<MoneyRequest> list = moneyRequestService.getOutgoing(user.getId());
        list.forEach(System.out::println);
    }

    private void accept(User user) {

        if (!verifyPin(user)) return;
        System.out.print("Enter Request ID to accept: ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Invalid input");
            return;
        }

        long id = Long.parseLong(input);

        boolean ok = moneyRequestService.acceptRequest(id, user.getId());

        if (ok)
            System.out.println(" Request accepted & payment done!");
        else
            System.out.println(" Failed (wallet/balance/ownership issue)");
    }



    private void decline(User user) {
        System.out.print("Enter request ID: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("Invalid ID");
            return;
        }

        long id = Long.parseLong(input);
        boolean ok = moneyRequestService.declineRequest(id, user.getId());
        System.out.println(ok ? "Declined" : "Failed");
    }

    private void setPin(User user) {

        System.out.print("Enter new 4-digit PIN: ");

        String pin = scanner.nextLine().trim();

        if (pin.length() != 4) {
            System.out.println("PIN must be 4 digits");
            return;
        }

        pinService.setPin(user.getId(), pin);

        System.out.println(" PIN saved successfully");
    }

    private boolean verifyPin(User user) {

        if (!pinService.hasPin(user.getId())) {
            System.out.println("⚠ Please set PIN first");
            return false;
        }

        System.out.print("Enter PIN: ");

        String pin = scanner.nextLine();

        boolean ok = pinService.verify(user.getId(), pin);

        if (!ok) {
            System.out.println(" Wrong PIN");
            return false;
        }

        return true;
    }

    private void addCard(User user) {

        System.out.print("Enter provider (HDFC/ICICI/etc): ");
        String provider = scanner.nextLine();

        System.out.print("Enter card number: ");
        String number = scanner.nextLine();

        long id = paymentService.addCard(user.getId(), provider, number);

        System.out.println(" Card added. ID: " + id);
    }

    private void listCards(User user){

        var list = paymentService.list(user.getId());

        if(list.isEmpty()){
            System.out.println("No saved caards");
            return;
        }

        System.out.println("\n--- Your Cards ---");

        list.forEach(System.out::println);
    }
    private void topup(User user) {

        if (!verifyPin(user)) return;

        listCards(user);

        System.out.print("Enter card ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.print("Enter amount: ");
        BigDecimal amt = new BigDecimal(scanner.nextLine());

        boolean ok = paymentService.topup(user.getId(), id, amt);

        if (ok)
            System.out.println(" Wallet topped up!");
        else
            System.out.println(" Failed");
    }

    private void createInvoice(User user) {

        System.out.print("Customer name: ");
        String name = scanner.nextLine();

        System.out.print("Customer contact: ");
        String contact = scanner.nextLine();

        long id = invoiceService.createInvoice(
                user.getId(),
                name,
                contact,
                java.time.LocalDate.now().plusDays(7)
        );

        System.out.println(" Invoice created. ID: " + id);
    }

    private void addInvoiceItem(User user) {

        System.out.print("Invoice ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.print("Description: ");
        String desc = scanner.nextLine();

        System.out.print("Qty: ");
        int qty = Integer.parseInt(scanner.nextLine());

        System.out.print("Unit price: ");
        BigDecimal price = new BigDecimal(scanner.nextLine());

        invoiceService.addItem(id, desc, qty, price);

        System.out.println(" Item added");
    }

    private void listInvoices(User user) {

        var list = invoiceService.list(user.getId());

        if (list.isEmpty()) {
            System.out.println("No invoices.");
            return;
        }

        list.forEach(System.out::println);
    }

    private void viewItems(User user) {

        System.out.print("Invoice ID: ");
        long id = Long.parseLong(scanner.nextLine());

        var items = invoiceService.getItems(id);

        items.forEach(System.out::println);
    }

    private void payInvoice(User user) {

        if (!verifyPin(user)) return;

        System.out.print("Invoice ID: ");
        long id = Long.parseLong(scanner.nextLine());

        boolean ok = invoiceService.payInvoice(id, user.getId());

        System.out.println(ok ? " Invoice paid!" : " Failed");
    }

    private void upgradeBusiness(User user) {

        if ("Business".equals(user.getAccountType())) {
            System.out.println("Already a Business account");
            return;
        }

        System.out.print("Business name: ");
        String name = scanner.nextLine();

        System.out.print("Business type: ");
        String type = scanner.nextLine();

        businessService.upgradeToBusiness(user.getId(), name, type);

        // IMPORTANT: update current user object also
        user.setAccountType("Business");

        System.out.println(" Upgraded to Business account!");
    }

    private void applyLoan(User user) {

        System.out.print("Loan amount: ");
        BigDecimal amt = new BigDecimal(scanner.nextLine());

        long id = loanService.apply(user.getId(), amt);

        System.out.println(" Loan applied. ID: " + id);
    }

    private void listLoans(User user) {

        var list = loanService.list(user.getId());

        if (list.isEmpty()) {
            System.out.println("No loans.");
            return;
        }

        list.forEach(System.out::println);
    }

    private void approveLoan(User user) {

        System.out.print("Loan ID: ");
        long id = Long.parseLong(scanner.nextLine());

        boolean ok = loanService.approve(id);

        System.out.println(ok ? " Approved + money credited" : " Failed");
    }

    private void repayLoan(User user) {

        if (!verifyPin(user)) return;

        System.out.print("Loan ID: ");
        long id = Long.parseLong(scanner.nextLine());

        System.out.print("Repay amount: ");
        BigDecimal amt = new BigDecimal(scanner.nextLine());

        boolean ok = loanService.repay(id, user.getId(), amt);

        System.out.println(ok ? " Repaid" : " Failed");
    }

    private void viewNotifications(User user) {

        var list = notificationService.list(user.getId());

        if (list.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }

        System.out.println("\n--- Notifications ---");

        list.forEach(System.out::println);

        notificationService.markAllRead(user.getId());
    }

    private int readChoice(int max) {

        while (true) {

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                if (choice >= 1 && choice <= max)
                    return choice;

            } catch (NumberFormatException ignored) {}

            System.out.println("Invalid choice. Try again.");
        }
    }







}
