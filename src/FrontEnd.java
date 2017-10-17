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
	public static String user;

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
	 * @return an ArrayList containing all the account numbers from the account file.
	 */
	private static ArrayList<String> getAllAccountNumbers() {
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

		return accountNumbersFromString;
	}

	// write the transaction summary file - alex
	private static void writeFile(String input) {
		
	}
	
	// get the console window input
	public static String getInput() {
		Scanner screen = new Scanner(System.in);
		String input = screen.nextLine();
		return input;
	}
	
	// check if the input is good.
	public static boolean checkInputOK(String input, ArrayList<String> validInput) {
		boolean inputOk = false;
		for (String valid : validInput) {
			if (input.toLowerCase().equals(valid.toLowerCase()))
				inputOk = true;
		}
		return inputOk;
	}
	
	
	public static void logout() {
		
	}
	
	public static void transfer() {
		
	}
	
	public static void withdraw() {
		
	}
	
	public static void deposit() {
		String acc = "";
		do {
			System.out.println("Please enter account number: ");
			acc = getInput();
		} while (checkInputOK(acc, getAllAccountNumbers()));
		do { 
			System.out.println("Please enter the amount to deposit in cents: ");
			String amountString = getInput();
		} while (true);
	}
	
	public static void deleteacc() {
		
	}

	//todo: if you like the account class, create a new account with this method and then add to the list of accounts
	public static void createAcc() {
		if (user == "agent") {
			/*-----Getting account Number and checking for validity -----*/ //STILL NEED TO CHECK ACC NUM IS DIFFERENT THAN ALL ACCOUNTS
			System.out.println("Please type in account number 7 digits long not starting with 0");
			String accountNumStr = getInput(); //get the account number
			int accountNumber =Integer.parseInt(accountNumStr); // convert to integer
			do {
				System.out.println("Please enter valid account number");
				accountNumStr = getInput();
				accountNumber = Integer.parseInt(accountNumStr);	
			} while(String.valueOf(Math.abs((long)accountNumber)).charAt(0) == '0' || String.valueOf(accountNumber).length() != 7);
	
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
		} else {
			System.out.println("Can't create an account if you're not an agent");
			return;
		}
	}
	
	// show the prompt when the user is an agent an get the transaction
	public static String loginAgent() {
		System.out.println("Please select a transaction");
		System.out.println("A. creating a new account");
		System.out.println("B. delete an existing account");
		System.out.println("C. deposit to an account");
		System.out.println("D. withdraw from an account");
		System.out.println("E. transfer from one account to another");
		String input = "";
		ArrayList<String> options = new ArrayList<>();
		options.add("A");
		options.add("B");
		options.add("C");
		options.add("D");
		options.add("E");
		do {
			System.out.println("Please enter the letter: ");
			input = getInput();
		} while (checkInputOK(input, options));
		return input.toLowerCase();
	}
	
	public static String loginATM() {
		System.out.println("Please select a transaction and enter the letter: ");
		System.out.println("A. deposit to an account");
		System.out.println("B. withdraw from an account");
		System.out.println("C. transfer from one account to another");
		String input = "";

		// How to initialize an ArrayList
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("A");
		arrayList.add("B");
		arrayList.add("C");

		do {
			System.out.println("Please enter the letter: ");
			input = getInput();
		} while (checkInputOK(input, arrayList));
		return input.toLowerCase();
	}
	
	// get the user, get the transaction and call the other transaction
	public static void login() {
		String tran = "";
		ArrayList<String> arrayList = new ArrayList<>();
		arrayList.add("ATM");
		arrayList.add("agent");
		do {
			System.out.println("Login as: \"ATM\" or \"agent\")");
			user = getInput();
		} while (checkInputOK(user, arrayList));
		if (user.equals("agent")) 
			tran = loginAgent();
		else
			tran = loginATM();
		if (tran.equals("a"))
			createAcc();
		else if (tran.equals("b"))
			deleteacc();
		else if (tran.equals("c"))
			deposit();
		else if (tran.equals("d"))
			withdraw();
		else
			transfer();
	}
	
	// show the welcome message, read the account file 
	// get the login input and call the login method.
	public static void main(String[] args) {
		// use sessionCurrent for the exit() method
		Boolean sessionCurrent = true;
		while (sessionCurrent) {
			System.out.println("Welcome to QBASIC");
			boolean inputOK = false;
			do {
				System.out.println("please type login");
				String input = getInput();
				if (input.equals("login"))
					inputOK = true;
			} while (!inputOK);
			login();
			readFile();
		}
	}
}
