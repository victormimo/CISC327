import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

	public static ArrayList<Integer> accountFile;
	public static String user;
	
	// read the account file
	public static void readFile() {
		
	}
	
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
	public static boolean checkInputOK(String input; String[] validInput) {
		boolean inputOk = false;
		for (valid : validInput) {
			if (input.toLowerCase().equals(valid.toLowerCase()))
				inputOK = true;
		}
		reutrn inputOK;
	}
	
	
	public static void logout() {
		
	}
	
	public static void transfer() {
		
	}
	
	public static void withdraw() {
		
	}
	
	public static void deposit() {
		do {
			System.out.println("Please enter account number: ");
			String acc = getInput();
		} while (checkInputOK(input, getAllAccountNumber()));
		do { 
			System.out.println("Please enter the amount to deposit in cents: ");
			String amountString = getInput();
		} while ()
	}
	
	public static void deleteacc() {
		
	}
	
	public static void creatacc() {
		
	}
	
	// show the prompt when the user is an agent an get the transaction
	public static String loginAgent() {
		System.out.println("Please select a transaction");
		System.out.println("A. creating a new account");
		System.out.println("B. delete an existing account");
		System.out.println("C. deposit to an account");
		System.out.println("D. withdraw from an account");
		System.out.println("E. transfer from one account to another");
		do {
			System.out.println("Please enter the letter: ");
			String input = getInput();
		} while (checkInputOK(input, ["A", "B", "C", "D", "E"]));
		return input.toLowerCase();
	}
	
	public static String loginATM() {
		System.out.println("Please select a transaction and enter the letter: ");
		System.out.println("A. deposit to an account");
		System.out.println("B. withdraw from an account");
		System.out.println("C. transfer from one account to another");
		do {
			System.out.println("Please enter the letter: ");
			String input = getInput();
		} while (checkInputOK(input, ["A", "B", "C"]));
		return input.toLowerCase();
	}
	
	// get the user, get the transaction and call the other transaction
	public static void login() {
		do {
			System.out.println("Login as: \"ATM\" or \"agent\")");
			user = getInput();
		} while (checkInputOK(user, ["ATM", "agent"]));
		if (user.equals("agent")) 
			String tran = loginAgent();
		else
			String tran = loginATM();
		if (tran.equals("a"))
			creatacc();
		else if (tran.equals("b"))
			deleteacc();
		else if (tran.equals("c"))
			deposit();
		else if (tran.equals("d"))
			withdraw();
		else
			transfer;
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
		}
	}
}
