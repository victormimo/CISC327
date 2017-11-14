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
	private FileIOHelper ioHelper = new FileIOHelper();

	/**
	 * Reads the transaction summary file and do the analysis line by line.
	 */
	public static void readTransactionFromFile() {
		try {
			BufferedReader reader = FileIOHelper.readerFromFile(transactionSummaryFile);
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
	 * @param accountVal
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
		accounts = FileIOHelper.readAccountsFromFile(oldMasterAccountFile);
		readTransactionFromFile();
	}
	
}
