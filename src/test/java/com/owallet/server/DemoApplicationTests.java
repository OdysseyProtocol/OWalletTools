package com.owallet.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void generate() {
        TestWallet.testGenerateWallet();
    }


    @Test
    public void balanceOfEth() {
        TestWallet.testBalanceOfETH();

    }

    @Test
    public void balanceOfOCN() {
        TestWallet.testBalanceOfOCN();

    }

    @Test
    public void txEth() {
        TestWallet.testTransactionEth();

    }

    @Test
    public void txOCN() {
        TestWallet.transactionOCN();
    }

}
