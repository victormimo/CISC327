/**
 * Account class which contains the name, number, and value of a bank account.
 * Also contains helper functions for accessing and altering account data.
 */
public class Account {
    private Integer accountNumber;
    private Double accountValue;
    private String accountName;

    Account(String number, String value, String name) {
        try {
            this.accountNumber = new Integer(number);
            this.accountValue = new Double(value);
            this.accountName = name;
        } catch (Exception e) {
            System.out.println("There was an error with the account information provided.");
        }
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

    void withdrawFromAccount(Account account, Double value) throws Exception {
        Double newValue = account.getAccountValue() - value;
        if (newValue < 0) {
            throw new Exception("Withdrawal exceeds available amount.");
        } else {
            account.setAccountValue(newValue);
        }
    }

    void depositIntoAccount(Account account, Double value) throws Exception {
        Double newValue = account.getAccountValue() + value;
        account.setAccountValue(newValue);
    }

    void transferToAccount(Account account, Double value) throws Exception {
        try {
            withdrawFromAccount(account, value);
            depositIntoAccount(this, value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    void transferFromAccount(Account account, Double value) throws Exception {
        try {
            withdrawFromAccount(this, value);
            depositIntoAccount(account, value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
