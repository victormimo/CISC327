import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Helper class that reads and writes files, and parses account and transaction files
 */
public class FileIOHelper {

    /**
     * Creates a BufferedReader from a file name which is used to create accounts and validate provided files.
     *
     * @param fileName the file to be read
     * @return the buffered reader containing the file's information
     */
    public static BufferedReader readerFromFile(String fileName) {
        try {
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

    //-------------------------------------------------------------------------------------------------------------
    // Read Account Files
    //-------------------------------------------------------------------------------------------------------------

    /**
     * Reads the account file.
     *
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
            while((line = readerFromFile(accountFileName).readLine()) != null) {
                account = new Account(getAccountNumber(line), getAccountValue(line), getAccountName(line));
                accounts.add(account);
            }
            return accounts;

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

}
