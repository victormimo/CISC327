import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Helper class that reads and writes files, and parses account and transaction files
 */
public class FileIOHelper {

    /**
     * check if a string input is included in a list of valid inputs
     *
     * @param input      - the user input
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

    //-------------------------------------------------------------------------------------------------------------
    // Read Files
    //-------------------------------------------------------------------------------------------------------------

    /**
     * Creates a BufferedReader from a file name which is used to create accounts and validate provided files.
     *
     * @param fileName the file to be read
     * @return the buffered reader containing the file's information
     */
    public static BufferedReader readerFromFile(String fileName) {
        try {
            System.out.println("third");
            // use the file path to get the file
            Path path = FileSystems.getDefault().getPath(fileName);
            File file = new File(fileName); /* used to determine that there is a file at that path */

            // If the file doesn't exist, warn user and quit system.
            if (!file.exists()) {
                System.out.println("\tFile not found.");
                System.exit(0);
                return null;
            }

            // create a reader to read the file, tell the reader how the file is encoded (assumed UTF-8)
            Charset charset = Charset.forName("UTF-8");
            return Files.newBufferedReader(path, charset);

        } catch (IOException i) {
            System.out.println(i.getMessage());
        }
        return null;
    }

    /**
     * Reads the account file.
     * <p>
     * Prompts the user for the path of the account file, and returns a warning to the user if the file isn't found at
     * that path.
     * Reads each line from the account file and adds it to the global array list containing the account file data.
     * todo: add an error chain if the file reading is unsuccessful
     */
    public static ArrayList<Account> readAccountsFromFile(String accountFileName) {
        String line; /* line from the file */
        Account account; /* holds the account described in the line */
        ArrayList<Account> accounts = new ArrayList<>(); /* a master list of all accounts */

        try {
            // add each line from the file into the global array list containing contents of the account file

            BufferedReader reader = readerFromFile(accountFileName);

            if (reader != null) {
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // heres the issue
                    String name = getAccountName(line);
                    String number = getAccountNumber(line);
                    String value = getAccountValue(line);
                    account = new Account(number,value,name);
                    //account = new Account(line, "0", "");
                    accounts.add(account);
                }
                return accounts;
            } else {
                System.out.println("Could not read file.");
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public static String[] readAccountList(String accountFileName) {
        String line = null; /* line from the file */
        ArrayList<String> list = new ArrayList<>(); /* a master list of all account numbers */
        try {
            // add each line from the file into the global array list containing contents of the account file

            BufferedReader reader = readerFromFile(accountFileName);

            if (reader != null) {
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                String[] l = new String[list.size()];
                l = list.toArray(l);
                return l;
            } else {
                System.out.println("Could not read file.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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

    //-------------------------------------------------------------------------------------------------------------
    // Validate Files
    //-------------------------------------------------------------------------------------------------------------

    public static boolean validateValidAccountListFile(String filename) {
        try {
            String line = null;
            String accountNumber = "";
            BufferedReader reader = readerFromFile(filename);
            if (reader != null) {
                while ((line = reader.readLine()) != null) {
                    // check that length of line is less than 8 characters
                    if (line.length() > 8) {
                        System.out.println("Too many characters in the line of the account list.\n");
                        return false;
                    }
                    accountNumber = line.substring(0, 7);
                    if (accountNumber.charAt(0) == '0') {
                        System.out.println("Account number begins with 0.\n");
                        return false;
                    }
                    if (line.equals("0000000")) {
                        System.out.println("Account list file not properly terminated.\n");
                        return false;
                    }
                }
            } else {
                System.out.println("Could not read file.\n");
            }
        } catch (Exception e) {
            System.out.println("Could not read file.\n");
        }
        return true;
    }

    /**
     * Validates that the Master Account File meets all constraints.
     *
     * @param filename the name of the master account file
     * @return true if the file is valid, false if the file is not valid
     */
    public static boolean validateMasterAccountFile(String filename) {
        try {
            String line;
            String[] accountData;

            BufferedReader reader = readerFromFile(filename);
            if (reader != null) {
                while ((line = reader.readLine()) != null) {
                	
                    // check that length of line is less than 48 characters
                    if (line.length() > 48) {
                        return false;
                    }

                    // check that there are not less than 3 entries in account data
                    accountData = line.split("\\s+");
                    if (accountData.length < 3) {
                        return false;
                    }

                    String accountName = "";
                    // re-build account name and check that it is between 3 and 30 characters long
                    for (int i = 0; i < accountData.length - 2; i++) {
                        accountName += accountData[2 + i];
                        System.out.println(accountName);
                    }
                    if (accountName.length() < 3 || accountName.length() > 30) {
                        return false;
                    }
                }
                
            } else {
                System.out.println("Could not read file.");
            }
            
        } catch (Exception e) {
            System.out.println("Could not read file.");
        }
		return true;
    }

    //-------------------------------------------------------------------------------------------------------------
    // Write Files
    //-------------------------------------------------------------------------------------------------------------

    /**
     * Called when a user successfully logs into the program. Initializes the global log file.
     * Overwrites any pre-existing log file which has the same filename (filename is hardcoded so the log file is
     * re-written every time the program runs).
     */
    public static PrintWriter initializeLogFile(String transactionSummaryName) {
        try {
            return new PrintWriter(transactionSummaryName, "UTF-8");
        } catch (IOException e) {
            System.out.println("Error creating log file.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Called when the program is closed, to close the log file.
     */
    public static void closeLogFile(PrintWriter file) {
        writeTransactionToLogFile("EOS", file);
        file.close();
    }

    /**
     * Called whenever something needs to be written to the log file.
     *
     * @param input - the entry to the log file.
     */
    public static void writeTransactionToLogFile(String input, PrintWriter file) {
        if (input.equals("EOS")) {
            writeToFile(input, file);
        }
        // make sure line is less than 61 characters long (including new line character)
        else if (input.length() > 60) {
            System.out.println("Transaction file line too long.");
        }
        // make sure the string is in the right format:
        // CCC AAAA MMMM BBBB NNNN
        else {
            String[] split = input.split("\\s+");
            // make sure the transaction code is correct
            String[] validTransactionCode = {"DEP", "WDR", "XFR", "NEW", "DEL", "EOS"};
            if (!checkInputOK(split[0], validTransactionCode)) {
                System.out.println("Incorrect transmission code.");
            }
            // make sure the account number is the in the correct form
            else if (split[1].length() != 7) {
                System.out.println("Account number provided to the log file is incorrect.");
            }
            // make sure the amount is between 3 and 8 decimal digits
            else if ((split[2].length() > 8 || (split[2].length() < 3) && !split[2].equals("0"))) {
                System.out.println("Value provided to log is the incorrect amount.");
            } else {
                // re-build account name and check that it is between 3 and 30 characters long
                String accountName = "";
                for (int i = 0; i < split.length - 4; i++) {
                    accountName += split[4 + i];
                }
                if (accountName.length() < 3 || accountName.length() > 30) {
                    System.out.println(accountName);
                    System.out.println("Account name provided to log file is incorrect.");
                } else {
                    try {
                        // write to file
                        writeToFile(input, file);
                    } catch (NullPointerException e) {
                        System.out.println("Log file not initialized.");
                    }
                }
            }
        }
    }

    /**
     * A simple program that writes a string to a file
     *
     * @param input the string to be included in the file
     * @param file  the file in which to write
     */
    private static void writeToFile(String input, PrintWriter file) {
        file.println(input);
    }

    /**
     * Write to the new valid list file
     */
    public static void writeNewList(String newValidAccountsList, ArrayList<Account> accounts) {
        PrintWriter newListFile = null;
        try {
            newListFile = new PrintWriter(newValidAccountsList, "UTF-8");
        } catch (IOException e) {
            System.out.println("Error creating new list file.");
            System.exit(1);
        }
        Collections.sort(accounts);
        for (Account acct : accounts) {
            String line = acct.getAccountNumber() + "\n";
            newListFile.write(line);
        }
        newListFile.close();
    }

    /**
     * Write to the new master file.
     */
    public static void writeMasterFile(String newMasterAccountFile, ArrayList<Account> accounts) {
        PrintWriter newMasterFile = null;
        try {
            newMasterFile = new PrintWriter(newMasterAccountFile, "UTF-8");
        } catch (IOException e) {
            System.out.println("Error creating new master file.");
            System.exit(1);
        }
        Collections.sort(accounts);
        for (Account acct : accounts) {
            String line = acct.getAccountNumber() + " " + (int) (acct.getAccountValue() * 100) + " " + acct.getAccountName() + '\n';
            newMasterFile.write(line);
        }
        newMasterFile.close();
    }
}
