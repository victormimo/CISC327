import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FrontEnd {

	public static ArrayList<String> accountFile;
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
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static String getAccountNumber(String accountInfo) {
		String accountNumber = null;

		return accountNumber;
	}

	public static String getAccountValue(String accountInfo) {
		String accountValue = null;

		return accountValue;
	}

	public static String getAccountName(String accountInfo) {
		String accountName = null;

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
		screen.close();
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

		readFile();
	}
}
