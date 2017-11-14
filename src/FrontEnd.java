import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

	private static Scanner screen;
	private static ArrayList<Account> accounts = new ArrayList<>(); /* using the account class */
	private static UserType user; /* user is "atm" or "agent" */
    private static PrintWriter logFile; /* the log file for all transactions */
	private static String accountFileName = "";
	private static String transactionSummaryName = "";
	private FileIOHelper ioHelper = new FileIOHelper();

	/**
	 * Retrieves all the account numbers from the file and wraps them up into an ArrayList of type String.
	 *
	 * @return a String array containing all the account numbers from the account file.
	 */
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

	/**
	 * Get the user input as text lines.
	 * And end the program whenever the user types end or END.
	 * @return the user input
	 */
	public static String getInput() {
		String input = screen.nextLine();
		if (input.equalsIgnoreCase("end")) {
			FileIOHelper.closeLogFile(logFile);
			System.exit(0);
		}
		return input;
	}
	
	public static boolean isNumber(String input) {
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
	 *
	 * First read the from and to account name and the amount.
	 * Then find the from account and check the account has that much money
	 * Finally find the to account and finish the transferring.
	 */
	public static void transfer() {
		String acc1 = ""; // the from account name
		String acc2 = ""; // the to account name
		String amountString = "";
		String output = ""; // the output to the transaction summary file.
		double amount = 0.0;
		// get the from account number, to account number and the amount to withdraw in cents.
		do {
			System.out.println("Please enter the from account number: ");
			acc1 = getInput();
		} while (!FileIOHelper.checkInputOK(acc1, getAllAccNumStr()));
		do {
			System.out.println("Please enter the to account number: ");
			acc2 = getInput();
		} while (!FileIOHelper.checkInputOK(acc2, getAllAccNumStr()));
		do { 
			do {
				System.out.println("Please enter the amount to transfer in cents: ");
				amountString = getInput();
			} while (!isNumber(amountString));
			amount =Integer.parseInt(amountString)/100;
		} while ((UserType.AGENT.equals(user) && ((amount > 999999) || (amount < 0))) ||
				((UserType.ATM.equals(user) && ((amount > 1000) || (amount < 0)))));
		// find the account in the account file and do the withdrawing.
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(acc1)) {
				Double newValue = acct.getAccountValue() - amount;
				if (newValue < 0)  {
					// This account do not have that much money.
					System.out.println("You only have " + acct.getAccountValue() + "(in cents) in your account.");
					return;
				}
				else {
					acct.setAccountValue(newValue);
					output = output + amount * 100 + " " + acct.getAccountNumber() + " " + acct.getAccountName();
				}
			}
			if (acct.getAccountNumber() == Integer.parseInt(acc2)) {
				Double newValue = acct.getAccountValue() + amount;
				acct.setAccountValue(newValue);
				output = "XFR " + acct.getAccountNumber() + " " + output;
			}
		}
		FileIOHelper.writeTransactionToLogFile(output, logFile);
	}

	/**
	 * Withdraw from an existing account.
	 *
	 * Get the valid existing account number and the amount.
	 * Then find the account in the account list and do the withdrawing.
	 * todo1: for the amount to withdraw, still need to check if the user input only contains digits.
	 * todo2: when the user cannot withdraw because the amount is not valid, need a exit() method to go back to the previous part.
	 */
	public static void withdraw() {
		String acc = "";
		String amountString = "";
		double amount = 0;
		// get the account number and the amount to withdraw in cents.
		do {
			System.out.println("Please enter account number: ");
			acc = getInput();
		} while (!FileIOHelper.checkInputOK(acc, getAllAccNumStr()));
		do {  
			do {
				System.out.println("Please enter the amount to withdraw in cents: ");
				amountString = getInput();
			} while (!isNumber(amountString));
			amount =Integer.parseInt(amountString)/100;
		} while ((UserType.AGENT.equals(user)&& ((amount > 999999) || (amount < 0))) ||
				((UserType.ATM.equals(user) && ((amount > 1000) || (amount < 0)))));
		// find the account in the account file and do the withdrawing.
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(acc)) {
				Double newValue = acct.getAccountValue() - amount;
				if (newValue < 0) // This account do not have that much money.
					System.out.println("You only have " + acct.getAccountValue() + " in your account.");
					// In the ATM session at most $1000 can be withdrawn from a single account.
				else if (((acct.getWithdraw() + amount) > 1000) && UserType.ATM.equals(user))
					System.out.println("a total of at most $1,000 can be withdrawn from a single account in a single ATM session");
				else {
					acct.setAccountValue(newValue);
					acct.setWithdraw(acct.getWithdraw() + amount);
					String output = "WDR " + acct.getAccountNumber() + " " + amount * 100 + " (none) " + acct.getAccountName();
					FileIOHelper.writeTransactionToLogFile(output, logFile);
				}
			}
		}
	}

	/**
	 * Deposit to an existing account.
	 *
	 * Get the valid existing account number and the amount
	 * Then find the account in the account list and do the depositing.
	 * todo: for the amount to deposit, still need to check if the user input only contains digits.
	 * @throws Exception
	 */
	public static void deposit() throws Exception {
		String acc = "";
		String amountString = "";
		double amount = 0;
		// get the account number and the amount to deposit in cents.
		do {
			System.out.println("Please enter account number: ");
			acc = getInput();
		} while (!FileIOHelper.checkInputOK(acc, getAllAccNumStr()));
		do {  // still need to check if the input is number here
			do {
				System.out.println("Please enter the amount to deposit in cents: ");
				amountString = getInput();
			} while (!isNumber(amountString));
			amount =Integer.parseInt(amountString)/100;
		} while ((UserType.AGENT.equals(user) && ((amount > 999999) || (amount < 0))) ||
				((UserType.ATM.equals(user) && ((amount > 1000) || (amount < 0)))));
		// find the account and do the depositing.
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(acc)) {
				acct.depositIntoAccount((Double)amount);
				String output = "DEP " + acct.getAccountNumber() + " " + amount * 100 + " (none) " + acct.getAccountName();

				FileIOHelper.writeTransactionToLogFile(output, logFile);
			}
		}
	}

	public static void deleteAcc() {
		System.out.println("Please enter the account number and name you wish to delete");
		String input = getInput();
		String[] split = input.split("\\s+");
		String accNumber = split[0];
		
		while(newAcctOK(accNumber)) { // newAccOK returns true if account does not exist
			System.out.println("Invalid account number, please enter valid account number");
			accNumber = getInput();
		}

		Account accountToDelete = null;
		for(Account account : accounts) {
			if (account.getAccountNumber().toString().equals(accNumber)) {
				accountToDelete = account;
				String output = "DEL "
						+ account.getAccountNumber()
						+ " "
						+ account.getAccountValue()
						+ " (none) "
						+ account.getAccountName();
				FileIOHelper.writeTransactionToLogFile(output, logFile);
			}
		}
		accounts.remove(accountToDelete);
	}

	/**
	 * check it a new account number is different than all accounts
	 * @param newAcct - the new account number in string
	 * @return a boolwan showing if it is valid.
	 */
	public static boolean newAcctOK(String newAcct) {
		String[] existingAcct = getAllAccNumStr();
		for (String acc : existingAcct) {
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
	 * adds account to account arraylist
	 */
	public static void createAcc() {
		/*-----Getting account Number and checking for validity -----*/ //STILL NEED TO CHECK ACC NUM IS DIFFERENT THAN ALL ACCOUNTS
		String accountNumStr;
		int accountNumber;
		do {
			do {
				System.out.println("Please type in account number 7 digits long not starting with 0");
				accountNumStr = getInput(); //get the account number
			} while (!isNumber(accountNumStr));
			accountNumber =Integer.parseInt(accountNumStr); // convert to integer
		} while(String.valueOf(Math.abs((long)accountNumber)).charAt(0) == '0' || String.valueOf(accountNumber).length() != 7 || !newAcctOK(accountNumStr));

		/*-----Getting account Name and checking for validity -----*/
		System.out.println("Please enter account name between 3 and 30 alphanumeric digits");
		String accountName = getInput(); //get the account name 
		String[] split = accountName.split("\\s+");
		while(accountName.length() < 3 || accountName.length() > 30 
				|| accountName.charAt(0) == ' ' || accountName.charAt(accountName.length() - 1) == ' ' 
				|| !nameOK(split)) {
			System.out.println("Please enter valid account name");
			accountName = getInput();
		}
		String output = "NEW " + accountNumStr + " 0 (none) " + accountName;
		FileIOHelper.writeTransactionToLogFile(output, logFile);
	}

	/**
	 * show the prompt when the user is an agent and get the transaction
	 * @return the valid user input
	 */
	public static String loginAgent() {
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
	 * @return the valid user input
	 */
	public static String loginATM() {
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
	 * @throws Exception
	 */
	public static void login() throws Exception {
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

		// read the master account file
		accounts = FileIOHelper.readAccountsFromFile(accountFileName);
		
		while (!logout) {
			String tran;
			if (UserType.AGENT.equals(user))
				tran = loginAgent();
			else
				tran = loginATM();

			if (tran.equals("a"))
				deposit();
			else if (tran.equals("b"))
				withdraw();
			else if (tran.equals("c"))
				transfer();
			else if (tran.equals("d"))
				createAcc();
			else if (tran.equals("e"))
				deleteAcc();
			else
				logout = true;
		}

		// close the log file when the user logs out
        FileIOHelper.closeLogFile(logFile);
	}

	/**
	 * show the welcome message and read the account file
	 * get the login input and call the login method.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		screen = new Scanner(System.in);
		// get the file names for account list and transaction summary from terminal
		if (args != null) {
			if (args.length >= 2) {
				accountFileName = args[0];
				transactionSummaryName = args[1];
			}
			else {
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
