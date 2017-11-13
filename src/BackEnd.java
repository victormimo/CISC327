import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class BackEnd {

	private static String oldMasterAccountFile = "";
	private static String newMasterAccountFile = "";
	private static String transactionSummaryFile = "";
	private static String newValidAccountsList = "";
	private static PrintWriter newListFile;
	private static PrintWriter newMasterFile;
	private static ArrayList<Account> accounts = new ArrayList<>(); /* using the account class */

	/**
	 * Retrieves the account number from an account in its String format.
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
	 * Reads the account file.
	 *
	 * Returns a warning to the user if the file isn't found at that path.
	 * Reads each line from the account file and adds it to the global array list containing the account file data.
	 */
	private static void readOldMasterAccount() {
		try {
			// use the file path from the user to get the file
			Path path = FileSystems.getDefault().getPath(oldMasterAccountFile);
			File file = new File(oldMasterAccountFile); /* used to determine that there is a file at that path */

			// If the file doesn't exist, warn user and return.
			if (!file.exists()) {
				System.out.println("\tFile not found.");
				System.exit(0);
				return;
			}

			// create a reader to read the file, tell the reader how the file is encoded (assumed UTF-8)
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(path, charset);

			// add each line from the file into the global array list containing contents of the account file
			String line;
			Account account; /* for the account class */
			while((line = reader.readLine()) != null) {
				// using the account class:
				account = new Account(getAccountNumber(line), getAccountValue(line), getAccountName(line));
				accounts.add(account);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Reads the transaction summary file and do the analysis line by line.
	 */
	private static void readTransaction() {
		try {
			// use the file path from the user to get the file
			Path path = FileSystems.getDefault().getPath(transactionSummaryFile);
			File file = new File(transactionSummaryFile); /* used to determine that there is a file at that path */

			// If the file doesn't exist, warn user and return.
			if (!file.exists()) {
				System.out.println("\tFile not found.");
				System.exit(0);
				return;
			}

			// create a reader to read the file, tell the reader how the file is encoded (assumed UTF-8)
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(path, charset);

			String line;
			while((line = reader.readLine()) != null) 
				analyzeLine(line);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Do the deposit transaction to the account list.
	 * @param tran: The split String array from each line of the transaction summary file.
	 */
	private static void depositTran(String[] tran) throws NumberFormatException, Exception {
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(tran[1])) 
				acct.depositIntoAccount(Double.parseDouble(tran[2]) / 100);
		}
	}
	
	/**
	 * Do the withdraw transaction to the account list.
	 * @param tran: The split String array from each line of the transaction summary file.
	 */
	private static void withdrawTran(String[] tran) throws Exception {
		String accountNum = tran[1];
		Double amount = Double.parseDouble(tran[2]) / 100;
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(accountNum))
				acct.withdrawFromAccount(amount);
		}
	}
	
	/**
	 * Do the transfer transaction to the account list.
	 * @param tran: The split String array from each line of the transaction summary file.
	 */
	private static void transferTran(String[] tran) throws NumberFormatException, Exception {
		String toAccountNum = tran[1];
		String fromAccountNum = tran[3];
		Double amount = Double.parseDouble(tran[2]) / 100;
		boolean transferOK = true;
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(fromAccountNum))
				transferOK = acct.transferFromAccount(amount);
		}
		if (transferOK) {
			for (Account acct : accounts) {
				if (acct.getAccountNumber() == Integer.parseInt(toAccountNum))
					acct.transferToAccount(amount);
			}	
		}
	}
	
	/**
	 * Check if a created account have a new, unused account number
	 * @param accountName
	 * @return false if the new account is not valid
	 */
	private static boolean createOK(String accountNum, String accountVal) {
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(accountNum) || Double.parseDouble(accountNum) == 0)
				return false;
		}
		return true;
	}
	
	/**
	 * Do the create transaction to the account list.
	 * @param tran: The split String array from each line of the transaction summary file.
	 */
	private static void createTran(String[] tran) {
		String accountNum = tran[1];
		String accountVal = tran[2];
		String accountName = "";
		for (int i = 4; i < tran.length; i++) {
			if (tran[i] != null) {
				accountName += tran[i];
				if (i < tran.length - 1)
					accountName += " ";
			}
		}
		if (createOK(accountNum, accountVal))
			accounts.add(new Account(accountNum, "0", accountName));
		else {
			System.out.println("The transaction below fails.");
			System.out.println("Create account: " + accountName + " " + accountNum);
			System.out.println("A created account have a new, unused account number.");
			System.out.println("");
		}
	}
	
	/**
	 * Check if a deleted account have a zero balance.
	 * Check if the name given in a delete transaction matches the name associated with the deleted acccount
	 * @param accountVal
	 * @param accountNum
	 * @param accountName
	 * @return 1 if it does not have a zero balance, 2 if the name does not match, and 0 if it is good to delete.
	 */
	private static int deleteOK(Double accountVal, String accountNum, String accountName) {
		if (accountVal != 0)
			return 1;
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(accountNum)) {
				if (acct.getAccountName().equals(accountName))
					return 0;
				return 2;
			}
		}
		return 0;
	}
	
	/**
	 * Do the delete transaction to the account list.
	 * @param tran: The split String array from each line of the transaction summary file.
	 */
	private static void deleteTran(String[] tran) {
		String accountNum = tran[1];
		String accountName = "";
		for (int i = 4; i < tran.length; i++) {
			if (tran[i] != null) {
				accountName += tran[i];
				if (i < tran.length - 1)
					accountName += " ";
			}
		}
		for (Account acct : accounts) {
			if (acct.getAccountNumber() == Integer.parseInt(accountNum)) {
				int i = deleteOK(acct.getAccountValue(), accountNum, accountName);
				if (i == 0) 
					accounts.remove(acct);
				else {
					System.out.println("The transaction below fails.");
					System.out.println("Delete account: " + accountName + " " + accountNum);
					if (i == 1)
						System.out.println("A deleted account must have a zero balance.");
					else if (i == 2)
						System.out.println("The name given in a delete transaction must match the name associated with the deleted account.");
					System.out.println("");
				}
				return;
			}
		}
	}
	
	/**
	 * Write to the new valid list file
	 */
	private static void writeNewList() {
		try {
	        newListFile = new PrintWriter(newValidAccountsList, "UTF-8");
        } catch (IOException e) {
            System.out.println("Error creating new list file.");
            System.exit(1);
        }
		for (Account acct : accounts) {
			String line = acct.getAccountNumber() + "\n";
			newListFile.write(line);
		}
		newListFile.close();
	}
	
	/**
	 * Write to the new master file.
	 */
	private static void writeMasterFile() {
		try {
	        newMasterFile = new PrintWriter(newMasterAccountFile, "UTF-8");
        } catch (IOException e) {
            System.out.println("Error creating new master file.");
            System.exit(1);
        }
		for (Account acct : accounts) {
			String line = acct.getAccountNumber() + " " + (int)(acct.getAccountValue() * 100) + " " + acct.getAccountName() + '\n';
			newMasterFile.write(line);
		}
		newMasterFile.close();
	}
	
	/**
	 * Analyze every lines and call functions based on what kind of the transaction it is.
	 * @param line: each time it takes one line from the transaction summary file and analyze it.
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void analyzeLine(String line) throws NumberFormatException, Exception {
		String[] split = line.split("\\s+");
		if (split[0].equals("DEP"))
			depositTran(split);
		else if (split[0].equals("WDR"))
			withdrawTran(split);
		else if (split[0].equals("XFR"))
			transferTran(split);
		else if (split[0].equals("NEW"))
			createTran(split);
		else if (split[0].equals("DEL")) 
			deleteTran(split);
		else { // when it is EOS
			writeNewList();
			writeMasterFile();
		}
	}
	
	/**
	 * Check the validation of the commend line arguments.
	 * @param args: The commend line arguments.
	 */
	private static void checkArgs(String[] args) {
		// Used for debugging.
		transactionSummaryFile = "transaction-log.txt";
		oldMasterAccountFile = "MasterAccountFileValid.txt";
		newMasterAccountFile = "newMaster.txt";
		newValidAccountsList = "newList.txt";
		/*
		// get the file names for account list and transaction summary from terminal
		if (args != null) {
			if (args.length >= 4) {
				transactionSummaryFile = args[0];
				oldMasterAccountFile = args[1];
				newMasterAccountFile = args[2];
				newValidAccountsList = args[3];
			}
			else {
				System.out.println("Insufficient files provided.");
				System.exit(0);
			}
		} else {
			System.out.println("File names not provided.");
			System.exit(0);
		}
		*/
	}
	
	public static void main(String[] args) {
		checkArgs(args);
		readOldMasterAccount();
		readTransaction();
	}
	
}
