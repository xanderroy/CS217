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

    @Test
    public void overdraftTest() {
        Account a = new Account();
        a.deposit(30);
        Assertions.assertThrows(ArithmeticException.class,() -> a.withdraw(100));
    }

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

}
