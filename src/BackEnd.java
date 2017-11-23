import java.io.BufferedReader;
import java.util.ArrayList;

/**
 * Holds the functionality of the Back End of QBasic
 */

public class BackEnd {

    private static String oldMasterAccountFile = "";
    private static String newMasterAccountFile = "";
    private static String transactionSummaryFile = "";
    private static String newValidAccountsList = "";
    private static ArrayList<Account> accounts = new ArrayList<>();
    private FileIOHelper ioHelper = new FileIOHelper();

    /**
     * Reads the transaction summary file and do the analysis line by line.
     */
    private static void readTransactionFromFile() {
        try {
            BufferedReader reader = FileIOHelper.readerFromFile(transactionSummaryFile);
            String line;
            if (reader != null) {
                while ((line = reader.readLine()) != null)
                    analyzeLine(line);
            } else {
                System.out.println("Error reading the file.");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Do the deposit transaction to the account list.
     *
     * @param tran: The split String array from each line of the transaction summary file.
     */
    private static void depositTran(String[] tran) throws Exception {
        for (Account acct : accounts) {
            if (acct.getAccountNumber() == Integer.parseInt(tran[1]))
                acct.depositIntoAccount(Double.parseDouble(tran[2]) / 100);
        }
    }

    /**
     * Do the withdraw transaction to the account list.
     *
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
     *
     * @param tran: The split String array from each line of the transaction summary file.
     */
    private static void transferTran(String[] tran) throws Exception {
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
     *
     * @return false if the new account is not valid
     */
    private static boolean createOK(String accountNum, String accountName) {
        // check that new account number doesn't begin with 0
        if ("0".equals(accountNum.substring(0,1))) {
            System.out.println("Account number begins with a 0.");
            return false;
        }

        // check that new account number doesn't have more than 7 digits
        if (accountNum.length() != 7) {
            System.out.println("Account number not the right length (7 digits).");
            return false;
        }

        // make sure the account number is unique
        for (Account acct : accounts) {
            if (acct.getAccountNumber() == Integer.parseInt(accountNum) || Double.parseDouble(accountNum) == 0)
                System.out.println("Account number is not unique.");
                return false;
        }

        // make sure the first and last characters of the name are not spaces
        if (" ".equals(accountName.substring(0,1)) || " ".equals(accountName.substring(accountName.length()-1,accountName.length()))) {
            System.out.println("account name begins or ends with a space.");
            return false;
        }

        // make sure the account name is between 3 and 30 characters
        if (accountName.length() < 3 || accountName.length() > 30) {
            System.out.println("Account name invalid - must be between 3 and 30 characters.");
            return false;
        }
        return true;
    }

    /**
     * Parses the string array for the transaction to get the account name
     *
     * @param tran the string array holding details for the transaction
     * @return the account name
     */
    private static String getAccountNameFromTransaction(String[] tran) {
        String accountName = "";
        for (int i = 4; i < tran.length; i++) {
            if (tran[i] != null) {
                accountName += tran[i];
                if (i < tran.length - 1)
                    accountName += " ";
            }
        }
        return accountName;
    }

    /**
     * Do the create account transaction to the account list.
     *
     * @param tran: The split String array from each line of the transaction summary file.
     */
    private static void createTran(String[] tran) {
        String accountNum = tran[1];
        String accountName = getAccountNameFromTransaction(tran);

        if (createOK(accountNum, accountName))
            accounts.add(new Account(accountNum, "0", accountName));
        else {
            System.out.println("The transaction below fails.");
            System.out.println("Create account: " + accountName + " " + accountNum);
            System.out.println("A created account must have a new, unused account number.");
            System.out.println("");
        }
    }

    /**
     * Check if a deleted account have a zero balance.
     * Check if the name given in a delete transaction matches the name associated with the deleted acccount
     *
     * @param accountVal  the value of the account
     * @param accountNum  the account number
     * @param accountName the name of the account
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
     *
     * @param tran: The split String array from each line of the transaction summary file.
     */
    private static void deleteTran(String[] tran) {
        String accountNum = tran[1];
        String accountName = getAccountNameFromTransaction(tran);
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
     * Analyze every lines and call functions based on what kind of the transaction it is.
     *
     * @param line: each time it takes one line from the transaction summary file and analyze it.
     * @throws Exception when there is an error writing the new files
     */
    private static void analyzeLine(String line) throws Exception {
        String[] split = line.split("\\s+");
        switch (split[0]) {
            case "DEP":
                depositTran(split);
                break;
            case "WDR":
                withdrawTran(split);
                break;
            case "XFR":
                transferTran(split);
                break;
            case "NEW":
                createTran(split);
                break;
            case "DEL":
                deleteTran(split);
                break;
            default:  // when it is EOS
                FileIOHelper.writeNewList(newValidAccountsList, accounts);
                FileIOHelper.writeMasterFile(newMasterAccountFile, accounts);
                break;
        }
    }

    /**
     * Checks to see if the master account file is in the right format, then initializes the account list
     */
    private static void initializeAccounts() {
        if (FileIOHelper.validateMasterAccountFile(oldMasterAccountFile)) {
            accounts = FileIOHelper.readAccountsFromFile(oldMasterAccountFile);
        } else {
            System.out.println("Could not read master account file.");
            System.exit(0);
        }
    }

    /**
     * Check the validation of the commend line arguments.
     *
     * @param args: The command line arguments.
     */
    private static void checkArgs(String[] args) {
    	/*
        // Used for debugging.
        transactionSummaryFile = "transaction-log.txt";
        oldMasterAccountFile = "MasterAccountFileValid.txt";
        newMasterAccountFile = "newMaster.txt";
        newValidAccountsList = "newList.txt";
        */
        // get the file names for account list and transaction summary from terminal
		if (args != null) {
			if (args.length >= 4) {
				transactionSummaryFile = args[0];
				oldMasterAccountFile = args[1];
				newMasterAccountFile = args[2];
				newValidAccountsList = args[3];

				initializeAccounts();
			}
			else {
				System.out.println("Insufficient files provided.");
				System.exit(0);
			}
		} else {
			System.out.println("File names not provided.");
			System.exit(0);
		}
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        checkArgs(args);
        readTransactionFromFile();
    }

}
