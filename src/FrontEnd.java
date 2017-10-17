import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

	public static ArrayList<Integer> accountFile;
	public static String user;
	
	// read the account file
	// alex
	public static void readFile() {
		
	}
	
	public static void readFile2() {
		
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
	
	
	public static void logout() {
		
	}
	
	public static void transfer() {
		
	}
	
	public static void withdraw() {
		
	}
	
	public static void deposit() {
		
	}
	
	public static void deleteacc() {
		
	}
	
	public static void creatacc() {
		
	}
	
	// get the user, get the transaction and call the other transaction
	public static void login() {
		
	}
	
	// show the welcome message, read the account file 
	// get the login input and call the login method.
	public static void main(String[] args) {
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
