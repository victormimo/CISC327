/**
 * Account class which contains the name, number, and value of a bank account.
 * Also contains helper functions for accessing and altering account data.
 */
public class Account implements Comparable<Account> {
    private Integer accountNumber;
    private Double accountValue;
    private String accountName;
    private Double withdraw;

    Account(String number, String value, String name) {
        try {
            this.accountNumber = new Integer(number);
            this.accountValue = new Double(value) / 100;
            this.accountName = name;
            this.withdraw = 0.00;
        } catch (Exception e) {
            System.out.println("There was an error with the account information provided.");
        }
    }

    void setWithdraw(Double d) {
        this.withdraw = d;
    }

    Double getWithdraw() {
        return this.withdraw;
    }

    Integer getAccountNumber() {
        return this.accountNumber;
    }

    Double getAccountValue() {
        return this.accountValue;
    }

    void setAccountValue(Double value) {
        this.accountValue = value;
    }

    String getAccountName() {
        return this.accountName;
    }

    /**
     * Withdraw from the account.
     *
     * @param value: The amount for withdraw.
     */
    void withdrawFromAccount(Double value) throws Exception {
        Double newValue = this.accountValue - value;
        if (newValue < 0) {
            // no account should ever have a negative balance.
            System.out.println("The transaction below fails.");
            System.out.println("Withdraw $" + value + " from account: " + this.accountNumber);
            System.out.println("No account should ever have a negative balance. ");
            System.out.println("");
        } else {
            this.setAccountValue(newValue);
        }
    }

    /**
     * Deposit to the account
     *
     * @param value: The amount for deposit
     */
    void depositIntoAccount(Double value) throws Exception {
        Double newValue = this.getAccountValue() + value;
        this.setAccountValue(newValue);
    }

    /**
     * transfer from the account.
     *
     * @param value: The amount for transfer
     */
    boolean transferFromAccount(Double value) throws Exception {
        Double newValue = this.accountValue - value;
        if (newValue < 0) {
            // no account should ever have a negative balance.
            System.out.println("The transaction below fails.");
            System.out.println("Transfer $" + value + " from account: " + this.accountNumber);
            System.out.println("No account should ever have a negative balance. ");
            System.out.println("");
            return false;
        } else {
            this.setAccountValue(newValue);
            return true;
        }
    }

    /**
     * transfer to the account.
     *
     * @param value: The amount for transfer
     */
    void transferToAccount(Double value) throws Exception {
        depositIntoAccount(value);
    }

    /**
     * Make Account object comparable.
     * When it is added to an arrayList, it should be sorted automatically.
     */
    public int compareTo(Account other) {
    	return this.accountNumber - other.accountNumber;
    }

}
