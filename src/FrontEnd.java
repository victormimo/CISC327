import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

	/** The account file is stored as an array list of strings, where each string has the form:
	 * xxxxxxx yyyyyy zzzzzzz
	 * Where the x's are the account number, the y's are the account value, and the z's are the account name.
	 **/
	public static ArrayList<String> accountFile = new ArrayList<>();
	public static ArrayList<Account> accounts = new ArrayList<>(); /* using the account class */
	public static String user; /* user is "atm" or "agent" */

	/**
	 * Reads the account file.
	 *
	 * Prompts the user for the path of the account file, and returns a warning to the user if the file isn't found at
	 * that path.
	 * Reads each line from the account file and adds it to the global array list containing the account file data.
	 * todo: add an error chain if the file reading is unsuccessful
	 */
	private static void readFile() {
		// request file path from console
		System.out.println("Please enter the path of the account list file.");
		try {
			// use the file path from the user to get the file
			String pathFromUser = getInput();
			Path path = FileSystems.getDefault().getPath(pathFromUser);
			File file = new File(pathFromUser); /* used to determine that there is a file at that path */

			// If the file doesn't exist, warn user and return.
			if (!file.exists()) {
				System.out.println("\tFile not found.");
				return;
			}

			// create a reader to read the file, tell the reader how the file is encoded (assumed UTF-8)
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(path, charset);

			// add each line from the file into the global array list containing contents of the account file
			String line;
			Account account; /* for the account class */
			while((line = reader.readLine()) != null) {
				accountFile.add(line);
				// using the account class:
				account = new Account(getAccountNumber(line), getAccountValue(line), getAccountName(line));
				accounts.add(account);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Retrieves the account number from an account in its String format.
	 * todo: check that the String provided is a valid account info String
	 *
	 * @param accountInfo - the String containing the information for an account.
	 * @return the account number.
	 */
	private static String getAccountNumber(String accountInfo) {
		// since the account information is separated by spaces, split string at all spaces and take the first split
		String[] split = accountInfo.split("\\s+");
		return split[0];
	}

	/**
	 * Retrieves the account value from an account in its String format.
	 *
	 * @param accountInfo - the String containing the information for an account.
	 * @return the account's value.
	 */
	private static String getAccountValue(String accountInfo) {
		// since the account information is separated by spaces, split string at all spaces and take the second split
		String[] split = accountInfo.split("\\s+");
		return split[1];
	}

	/**
	 * Retrieves the account name from an account in its String format
	 *
	 * @param accountInfo - the String containing the information for an account.
	 * @return the account's name.
	 */
	private static String getAccountName(String accountInfo) {
		String accountName = "";

		// since the account info is separated by spaces, split string at all spaces
		// the account name starts at the third split, but may contain spaces that we don't want to erase, so those
		// 		spaces need to be added back to the name
		String[] split = accountInfo.split("\\s+");
		for (int i = 2; i < split.length; i++) {
			if (split[i] != null) {
				accountName += split[i];
				// if there are more parts to add to the name that have been split at the spaces, add a space to the
				// 		variable containing the account name (no lost information)
				if (i < split.length - 1)
					accountName += " ";
			}
		}

		return accountName;
	}

	/**
	 * Retrieves all the account numbers from the file and wraps them up into an ArrayList of type String.
	 *
	 * @return a String array containing all the account numbers from the account file.
	 */
	private static String[] getAllAccNumStr() {
		// Using the ArrayList<String> accountFile
		ArrayList<String> accountNumbersFromString = new ArrayList<>();
		for (String account : accountFile) {
			accountNumbersFromString.add(getAccountNumber(account));
		}

		// Using the ArrayList<Account> accounts
		ArrayList<Integer> accountNumbersFromAccount = new ArrayList<>();
		for (Account account : accounts) {
			accountNumbersFromAccount.add(account.getAccountNumber());
		}
		String[] accNumString = new String[accountNumbersFromString.size()];
		for (int i = 0; i < accountNumbersFromString.size(); i++) {
			accNumString[i] = accountNumbersFromString.get(i);
		}
		return accNumString;
	}

	// write the transaction summary file - alex
	private static void writeFile(String input) {

	}

	/**
	 * Get the user input as text lines.
	 * And end the program whenever the user types end or END.
	 * @return the user input
	 */
	public static String getInput() {
		Scanner screen = new Scanner(System.in);
		String input = screen.nextLine();
		if (input.equalsIgnoreCase("end")) {
			writeFile("EOS");
			System.exit(0);
		}
		return input;
	}

	/**
	 * check if a string input is valid
	 * @param input - the user input
	 * @param validInput - an array of String, containing all the valid input.
	 * @return a boolean value showing if the input is valid
	 */
	public static boolean checkInputOK(String input, String[] validInput) {
		boolean inputOK = false;
		for (String valid : validInput) {
			if (input.equalsIgnoreCase(valid))
				inputOK = true;
		}
		return inputOK;
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
		} while (!checkInputOK(acc1, getAllAccNumStr()));
		do {
			System.out.println("Please enter the to account number: ");
			acc2 = getInput();
		} while (!checkInputOK(acc2, getAllAccNumStr()));
		do { // still need to check if the input is number here
			System.out.println("Please enter the amount to deposit in cents: ");
			amountString = getInput();
			amount =Integer.parseInt(amountString)/100;
		} while ((user.equalsIgnoreCase("agent") && ((amount > 99999999) || (amount < 0))) ||
				((user.equalsIgnoreCase("ATM") && ((amount > 100000) || (amount < 0)))));
		// find the account in the account file and do the withdrawing.
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(acc1)) {
				Double newValue = acct.getAccountValue() - amount;
				if (newValue < 0) // This account do not have that much money.
					System.out.println("You only have " + acct.getAccountValue() + "(in cents) in your account.");
				else {
					acct.setAccountValue(newValue);
					output = output + amount + " " + acct.getAccountNumber() + " " + acct.getAccountName();
				}
			}
			if (acct.getAccountNumber() == Integer.parseInt(acc2)) {
				Double newValue = acct.getAccountValue() + amount;
				acct.setAccountValue(newValue);
				output = "XFR " + acct.getAccountNumber() + " " + output;
				writeFile(output);
			}
		}
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
		} while (!checkInputOK(acc, getAllAccNumStr()));
		do {  // still need to check if the input is number here
			System.out.println("Please enter the amount to deposit in cents: ");
			amountString = getInput();
			amount =Integer.parseInt(amountString)/100;
		} while ((user.equalsIgnoreCase("agent") && ((amount > 99999999) || (amount < 0))) ||
				((user.equalsIgnoreCase("ATM") && ((amount > 100000) || (amount < 0)))));
		// find the account in the account file and do the withdrawing.
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(acc)) {
				Double newValue = acct.getAccountValue() - amount;
				if (newValue < 0) // This account do not have that much money.
					System.out.println("You only have " + acct.getAccountValue() + " in your account.");
					// In the ATM session at most $1000 can be withdrawn from a single account.
				else if (((acct.getTotalWithdraw() + amount) > 100000) && user.equalsIgnoreCase("atm"))
					System.out.println("a total of at most $1,000 can be withdrawn from a single account in a single ATM session");
				else {
					acct.setAccountValue(newValue);
					writeFile("WDR " + acct.getAccountNumber() + " " + amount + " (none) " + acct.getAccountName());
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
		} while (!checkInputOK(acc, getAllAccNumStr()));
		do {  // still need to check if the input is number here
			System.out.println("Please enter the amount to deposit in cents: ");
			amountString = getInput();
			amount = Integer.parseInt(amountString)/100;
		} while ((user.equalsIgnoreCase("agent") && ((amount > 99999999) || (amount < 0))) ||
				((user.equalsIgnoreCase("ATM") && ((amount > 100000) || (amount < 0)))));
		// find the account and do the depositing.
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(acc)) {
				acct.depositIntoAccount((Double)amount);
				writeFile("DEP " + acct.getAccountNumber() + " " + amount + " (none) " + acct.getAccountName());
			}
		}
	}

	public static void deleteAcc() {

	}

	/**
	 * check it a new account number is different than all accounts
	 * @param the new account number in string
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

	//todo: if you like the account class, create a new account with this method and then add to the list of accounts
	public static void createAcc() {
		/*-----Getting account Number and checking for validity -----*/ //STILL NEED TO CHECK ACC NUM IS DIFFERENT THAN ALL ACCOUNTS
		System.out.println("Please type in account number 7 digits long not starting with 0");
		String accountNumStr = getInput(); //get the account number
		int accountNumber =Integer.parseInt(accountNumStr); // convert to integer
		do {
			System.out.println("Please enter valid account number");
			accountNumStr = getInput();
			accountNumber = Integer.parseInt(accountNumStr);
		} while(String.valueOf(Math.abs((long)accountNumber)).charAt(0) == '0' || String.valueOf(accountNumber).length() != 7 || !newAcctOK(accountNumStr));

		/*-----Getting account Name and checking for validity -----*/
		System.out.println("Please enter account name between 3 and 30 alphanumeric digits");
		String accountName = getInput(); //get the account name
		do {
			System.out.println("Please enter valid account name");
			accountName = getInput();
		}while(accountName.length() < 3 && accountName.length() > 30 && !accountName.matches("[A-Za-z0-9]+"));
		// write the new account to the transaction file
		String userInput = accountNumber + " " + accountName;
		writeFile(userInput);
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
		String[] validInput = {"A", "B", "C", "D", "E"};
		do {
			System.out.println("Please enter the letter: ");
			input = getInput();
		} while (!checkInputOK(input, validInput));
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
		} while (!checkInputOK(input, validInput));
		return input.toLowerCase();
	}

	/**
	 * get the user, get the transaction and call the other transaction
	 * @throws Exception
	 */
	public static void login() throws Exception {
		Boolean logout = false;
		String[] validInput = {"ATM", "agent"};
		do {
			System.out.println("Login as: \"ATM\" or \"agent\"");
			user = getInput().toLowerCase();
		} while (!checkInputOK(user, validInput));
		while (!logout) {
			String tran;
			if (user.equals("agent"))
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
	}

	/**
	 * show the welcome message and read the account file
	 * get the login input and call the login method.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		readFile();
		while (true) {
			System.out.println("Welcome to QBASIC.");
			String input;
			String[] validInput = {"login"};
			do {
				System.out.println("please type login");
				input = getInput();
			} while (!checkInputOK(input, validInput));
			login();
		}
	}
}
