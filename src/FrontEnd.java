import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

	public static ArrayList<String> accountFile = new ArrayList<>();
	public static String user;
	
	// read the account file
	// alex
	public static void readFile() {
		// request file path from console
		System.out.println("Please provide the path of the account list file");
		try {
			// use the file path to get a file
			String input = getInput();
			Path path = FileSystems.getDefault().getPath(input);
			File file = new File(input);

			if (file.exists()) {
				System.out.println("\tFile found.");
			} else {
				System.out.println("\tFile not found.");
				return;
			}

			// assumed that file is coded in US-ASCII
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(path, charset);

			// print each line of the file
			String line = null;
			while((line = reader.readLine()) != null) {
				System.out.println(line);
				accountFile.add(line);
			}

			System.out.println("Account number: " + getAccountNumber(accountFile.get(0)));
			System.out.println("Account name: " + getAccountName(accountFile.get(0)));
			System.out.println("Account value: " + getAccountValue(accountFile.get(0)));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	public static String getAccountNumber(String accountInfo) {
		String[] split = accountInfo.split("\\s+");
		return split[0];
	}

	public static String getAccountValue(String accountInfo) {
		String[] split = accountInfo.split("\\s+");
		return split[1];
	}

	public static String getAccountName(String accountInfo) {
		String accountName = "";
		String[] split = accountInfo.split("\\s+");
		for (int i = 2; i < split.length; i++) {
			if (split[i] != null) {
				accountName += split[i];
				if (i < split.length - 1)
					accountName += " ";
			}
		}
		return accountName;
	}

	public static ArrayList<String> getAllAccountNumbers() {
		ArrayList<String> accountNumbers = new ArrayList<>();

		return accountNumbers;
	}
	
	// alex
	// write the transaction summary file
	public static void writeFile(String input) {
		
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
		while (true) {
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
