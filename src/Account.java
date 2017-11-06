/**
 * Account class which contains the name, number, and value of a bank account.
 * Also contains helper functions for accessing and altering account data.
 */
public class Account {
    private Integer accountNumber;
    private Double accountValue;
    private String accountName;
    private Double withdraw;

    Account(String number, String value, String name) {
        try {
            this.accountNumber = new Integer(number);
            this.accountValue = new Double(value)/100;
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

    void withdrawFromAccount(Double value) throws Exception {
        Double newValue = this.getAccountValue() - value;
        if (newValue < 0) {
            throw new Exception("Withdrawal exceeds available amount.");
        } else {
            this.setAccountValue(newValue);
        }
    }

    void depositIntoAccount(Double value) throws Exception {
        Double newValue = this.getAccountValue() + value;
        this.setAccountValue(newValue);
    }

}
