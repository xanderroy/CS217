package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.*;

public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account();
        Assertions.assertTrue(a != null);
    }

    @Test
    public void accountStartsEmpty() {
        Account a = new Account();
        Assertions.assertTrue(a.getBalance() == 0);
    }

    @Test
    public void depositTest() {
        Account a = new Account();
        a.deposit(20);
        a.deposit(50);
        Assertions.assertTrue(a.getBalance() == 70);
    }

    @Test
    public void withdrawTest() {
        Account a = new Account();
        a.deposit(40);
        a.withdraw(20);
        Assertions.assertTrue(a.getBalance()==20);
    }

    /*@Test
    public void overdraftTest() {
        Account a = new Account();
        a.deposit(30);
        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    } */

    @Test
    public void saverTest() {
        Account a = new Account();
        a.deposit(20);
        for (int i = 0; i < 5; ++i) {
            a.deposit(10);
        }
        for (int i = 0; i < 3; ++i) {
            a.withdraw(20);
        }
        Assertions.assertTrue(a.getBalance()==10);
    }

    @Test
    public void penniesTest() {
        Account a = new Account();
        a.deposit(5.45);
        a.deposit(17.56);
        System.out.println(a.getBalance());
        Assertions.assertTrue(a.getBalance()==23.01);

    }

    @Test
    public void getNameTest() {
        Account a = new Account("Stephen");
        Assertions.assertEquals("Stephen", a.getName());
    }

    @Test
    public void toStringTest() {
        Account a = new Account("Stephen", 543.21);
        Assertions.assertEquals("Name: Stephen, Balance: 543.21", a.toString());
    }

    @Test
    public void testTransfer() {
        Account a = new Account("John", 50, false, "id1");
        Account b = new Account("Stephen", 100, false, "id2");
        Accounts.addAccount(a);
        Accounts.addAccount(b);
        Accounts.transfer(a.getId(), b.getId(), 20);
        Assertions.assertEquals(70, a.getBalance());
        Assertions.assertEquals(80, b.getBalance());
    }

    @Test
    public void testGetAccount() {
        Account a = new Account("Stephen", 100, false, "id1");
        Accounts.addAccount(a);
        Account b = Accounts.getAccount("id1");
        Assertions.assertEquals(a, b);
    }

    @Test
    public void testGetAccountError() {
        Account b = Accounts.getAccount("id1");
        Assertions.assertNull(b);
    }

    @Test
    public void testRoundUps() {
        Account a = new Account("Stephen", 100, false, "id1");
        Accounts.addAccount(a);
        Transactions.addTransaction(new Transaction("tr1", "KFC", "id1", "PAYMENT", 9.50));
        Accounts.getAccount("id1").enableRoundUps();
        Assertions.assertEquals(90, Accounts.getAccount("id1").getBalance());
        Assertions.assertEquals(0.5, Accounts.getAccount("id1").getRoundUpsPot());
        Accounts.getAccount("id1").reclaimRoundUps();
        Assertions.assertEquals(90.5, Accounts.getAccount("id1").getBalance());
    }

}
