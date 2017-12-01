import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Holds the functionality of the Front End of QBasic
 */

public class FrontEnd {

    private static Scanner screen;
    private static String[] list = null;
    private static UserType user; /* user is "atm" or "agent" */
    private static PrintWriter logFile; /* the log file for all transactions */
    private static String accountFileName = "";
    private static String transactionSummaryName = "";
    /**
     * Retrieves all the account numbers from the file and wraps them up into an ArrayList of type String.
     *
     * @return a String array containing all the account numbers from the account file.
     */
    /*
    private static String[] getAllAccNumStr() {

        ArrayList<Integer> accountNumbersFromAccount = new ArrayList<>();

        for (Account account : accounts) {
            accountNumbersFromAccount.add(account.getAccountNumber());
        }
        String[] accNumString = new String[accountNumbersFromAccount.size()];
        for (int i = 0; i < accountNumbersFromAccount.size(); i++)
            accNumString[i] = accountNumbersFromAccount.get(i).toString();
        return accNumString;
    }
*/
    /**
     * Get the user input as text lines.
     * And end the program whenever the user types end or END.
     *
     * @return the user input
     */
    private static String getInput() {
        String input = screen.nextLine();
        if (input.equalsIgnoreCase("end")) {
            FileIOHelper.closeLogFile(logFile);
            System.exit(0);
        }
        return input;
    }

    private static boolean isNumber(String input) {
        boolean isNumber = true;
        char[] ch = input.toCharArray();
        for (char aCh : ch) {
            isNumber = Character.isDigit(aCh);
            if (!isNumber)
                return isNumber;
        }
        return isNumber;
    }

    /**
     * Transfer from an existing account to another existing account.
     * <p>
     * First read the from and to account name and the amount.
     * Then find the from account and check the account has that much money
     * Finally find the to account and finish the transferring.
     */
    public static void transfer() {
        String acc1; // the from account name
        String acc2; // the to account name
        String amountString;
        String output = ""; // the output to the transaction summary file.
        double amount;
        // get the from account number, to account number and the amount to withdraw in cents.
        do {
            System.out.println("Please enter the from account number: ");
            acc1 = getInput();
        } while (!FileIOHelper.checkInputOK(acc1, list));
        do {
            System.out.println("Please enter the to account number: ");
            acc2 = getInput();
        } while (!FileIOHelper.checkInputOK(acc2, list));
        do {
            do {
                System.out.println("Please enter the amount to transfer in cents: ");
                amountString = getInput();
            } while (!isNumber(amountString));
            amount = Integer.parseInt(amountString) / 100;
        } while ((UserType.AGENT.equals(user) && ((amount > 999999) || (amount < 0))) ||
                ((UserType.ATM.equals(user) && ((amount > 1000) || (amount < 0)))));
        output = "XFR " + acc2 + " " + amount * 100 + " " + acc1 + " ***";
        FileIOHelper.writeTransactionToLogFile(output, logFile);
    }

    /**
     * Withdraw from an existing account.
     * <p>
     * Get the valid existing account number and the amount.
     * Then find the account in the account list and do the withdrawing.
     * todo1: for the amount to withdraw, still need to check if the user input only contains digits.
     * todo2: when the user cannot withdraw because the amount is not valid, need a exit() method to go back to the previous part.
     */
    public static void withdraw() {
        String acc;
        String amountString;
        double amount;
        // get the account number and the amount to withdraw in cents.
        do {
            System.out.println("Please enter account number: ");
            acc = getInput();
        } while (!FileIOHelper.checkInputOK(acc, list));
        do {
            do {
                System.out.println("Please enter the amount to withdraw in cents: ");
                amountString = getInput();
            } while (!isNumber(amountString));
            amount = Integer.parseInt(amountString) / 100;
        } while ((UserType.AGENT.equals(user) && ((amount > 999999) || (amount < 0))) ||
                ((UserType.ATM.equals(user) && ((amount > 1000) || (amount < 0)))));
        // find the account in the account file and do the withdrawing.
        String output = "WDR " + acc + " " + amount * 100 + " 0000000 ***";
        FileIOHelper.writeTransactionToLogFile(output, logFile);
    }

    /**
     * Deposit to an existing account.
     * <p>
     * Get the valid existing account number and the amount
     * Then find the account in the account list and do the depositing.
     * todo: for the amount to deposit, still need to check if the user input only contains digits.
     *
     * @throws Exception in case file can't be written to
     */
    private static void deposit() throws Exception {
        String acc;
        String amountString;
        double amount;
        // get the account number and the amount to deposit in cents.
        do {
            System.out.println("Please enter account number: ");
            acc = getInput();
        } while (!FileIOHelper.checkInputOK(acc, list));

        do {  // still need to check if the input is number here
            do {
                System.out.println("Please enter the amount to deposit in cents: ");
                amountString = getInput();
            } while (!isNumber(amountString));
            amount = Integer.parseInt(amountString) / 100;
        } while ((UserType.AGENT.equals(user) && ((amount > 999999) || (amount < 0))) ||
                ((UserType.ATM.equals(user) && ((amount > 1000) || (amount < 0)))));
        String output = "DEP " + acc + " " + amount * 100 + " 0000000 ***";
        FileIOHelper.writeTransactionToLogFile(output, logFile);
    }

    /**
     * Delete an existing account.
     */
    private static void deleteAcc() {
        System.out.println("Please enter the account name you wish to delete");
        String name = getInput();
        String accNumber;
        do { // newAccOK returns true if account does not exist
        	System.out.println("Please enter the account number you wish to delete");
            accNumber = getInput();
        } while (!newAcctOK(accNumber) || !isNumber(accNumber));
        String output = "DEL " + accNumber + " 000 0000000 " + name;
        FileIOHelper.writeTransactionToLogFile(output, logFile);
        list = remove(list, accNumber);
    }

    /**
     * remove an account number from an account list.
     * @param list : The original list
     * @param num : The account number to be deleted
     * @return the new list
     */
    private static String[] remove(String[] list, String num) {
    	String[] newList = new String[list.length - 1];
    	int i = 0;
    	for (String s : list) {
    		if (!s.equals(num)) {
    			newList[i] = s;
    			i++;
    		}
    	}
    	return newList;
    }
    /**
     * check it a new account number is different than all accounts
     *
     * @param newAcct - the new account number in string
     * @return a boolean showing if it is valid.
     */
    private static boolean newAcctOK(String newAcct) {
        for (String acc : list) {
            if (newAcct.equals(acc))
                return false;
        }
        return true;

    }

    private static boolean nameOK(String[] split) {
        for (String s : split) {
            if (!s.matches("[A-Za-z0-9]+"))
                return false;
        }
        return true;
    }

    /**
     * create new account by accepting account number and name from user
     * only valid in agent mode
     */
    private static void createAcc() {
        /*-----Getting account Number and checking for validity -----*/ //STILL NEED TO CHECK ACC NUM IS DIFFERENT THAN ALL ACCOUNTS
        String accountNumStr;
        int accountNumber;
        do {
            do {
                System.out.println("Please type in account number 7 digits long not starting with 0");
                accountNumStr = getInput(); //get the account number
            } while (!isNumber(accountNumStr));
            accountNumber = Integer.parseInt(accountNumStr); // convert to integer
        }
        while (String.valueOf(Math.abs((long) accountNumber)).charAt(0) == '0' || String.valueOf(accountNumber).length() != 7 || !newAcctOK(accountNumStr));

		/*-----Getting account Name and checking for validity -----*/
        System.out.println("Please enter account name between 3 and 30 alphanumeric digits");
        String accountName = getInput(); //get the account name
        String[] split = accountName.split("\\s+");
        while (accountName.length() < 3 || accountName.length() > 30
                || accountName.charAt(0) == ' ' || accountName.charAt(accountName.length() - 1) == ' '
                || !nameOK(split)) {
            System.out.println("Please enter valid account name");
            accountName = getInput();
        }
        String output = "NEW " + accountNumStr + " 000 0000000 " + accountName;
        FileIOHelper.writeTransactionToLogFile(output, logFile);
    }

    /**
     * show the prompt when the user is an agent and get the transaction
     *
     * @return the valid user input
     */
    private static String loginAgent() {
        System.out.println("Please select a transaction");
        System.out.println("A. deposit to an account");
        System.out.println("B. withdraw from an account");
        System.out.println("C. transfer from one account to another");
        System.out.println("D. creating a new account");
        System.out.println("E. delete an existing account");
        String input;
        String[] validInput = {"A", "B", "C", "D", "E", "logout"};
        do {
            System.out.println("Please enter the letter: ");
            input = getInput();
        } while (!FileIOHelper.checkInputOK(input, validInput));
        return input.toLowerCase();
    }

    /**
     * show the prompt when the user is an ATM and get the transaction
     *
     * @return the valid user input
     */
    private static String loginATM() {
        System.out.println("Please select a transaction or type logout: ");
        System.out.println("A. deposit to an account");
        System.out.println("B. withdraw from an account");
        System.out.println("C. transfer from one account to another");
        String input;
        String[] validInput = {"A", "B", "C", "logout"};
        do {
            System.out.println("Please enter the letter or type logout: ");
            input = getInput();
        } while (!FileIOHelper.checkInputOK(input, validInput));
        return input.toLowerCase();
    }

    /**
     * get the user, get the transaction and call the other transaction
     *
     * @throws Exception in case file is not properly created
     */
    private static void login() throws Exception {
        // call initializeLogFile as soon as user successfully logs in
        logFile = FileIOHelper.initializeLogFile(transactionSummaryName);

        String userInput;
        do {
            System.out.println("Please type login.");
            userInput = getInput().toLowerCase();
        } while (!userInput.equals("login"));
        Boolean logout = false;
        String[] validInput = {"machine", "agent"};
        do {
            System.out.println("Please login as: machine or agent");
            userInput = getInput().toLowerCase();
            if (userInput.equalsIgnoreCase("agent"))
                user = UserType.AGENT;
            else if (userInput.equalsIgnoreCase("machine"))
                user = UserType.ATM;
        } while (!FileIOHelper.checkInputOK(userInput, validInput));

        // check valid account list file, read the valid account list file
        if (FileIOHelper.validateValidAccountListFile(accountFileName)) {
            list = FileIOHelper.readAccountList(accountFileName);
        } else {
            System.out.println("Account List file not valid.");
            System.exit(0);
        }

        while (!logout) {
            String tran;
            if (UserType.AGENT.equals(user))
                tran = loginAgent();
            else
                tran = loginATM();

            // although switch cases are a code smell that imply the need for inheritance, this is a justified use
            // of a switch case because it represents possible choices made by the user
            switch (tran) {
                case "a":
                    deposit();
                    break;
                case "b":
                    withdraw();
                    break;
                case "c":
                    transfer();
                    break;
                case "d":
                    createAcc();
                    break;
                case "e":
                    deleteAcc();
                    break;
                default:
                    logout = true;
                    break;
            }
        }

        // close the log file when the user logs out
        FileIOHelper.closeLogFile(logFile);
    }

    /**
     * show the welcome message and read the account file
     * get the login input and call the login method.
     *
     * @throws Exception in case file is not properly created
     */
    public static void main(String[] args) throws Exception {
        screen = new Scanner(System.in);
        // get the file names for account list and transaction summary from terminal
        if (args != null) {
            if (args.length >= 2) {
                accountFileName = args[0];
                transactionSummaryName = args[1];
            } else {
                System.out.println("Insufficient files provided.");
                System.exit(0);
            }
        } else {
            System.out.println("File names not provided.");
            System.exit(0);
        }
        System.out.println("Welcome to QBASIC.");
        login();
    }
}