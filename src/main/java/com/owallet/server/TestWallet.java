package com.owallet.server;

import com.owallet.server.api.OWalletAPI;
import com.owallet.server.bean.WalletInfo;
import com.owallet.server.utils.OWalletUtils;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
/**
 * Created by y on 2018/3/1.
 */
public class TestWallet {

    //0x41c01e8e46796fdc6910b438fa0ae6f1d8fca474
    public static String privateKey = "9c8225ebb5452dfcce768a465e964bc995f9a29f5bca87deee0baf3ff9e428e3";

    public static String walletAddress = "0x41c01e8e46796fdc6910b438fa0ae6f1d8fca474";

    private static String ContractAddress = "0xd1bcbe82f40a9d7fbcbd28cca6043d72d66d8e9d";

    public static final String txHash = "0x1a763acca69a048d413311bdf2ce795b01415c5220892dcb5765a47fce559143";

    public static void testGenerateWallet() {

        try {
            WalletInfo walletInfo = OWalletAPI.generateWallet();
            System.out.println();
            System.out.println("walletAddress:" + walletInfo.getWalletAddress());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }


    public static void testBalanceOfETH() {
        String balanceOfETH = OWalletAPI.balanceOfETH(walletAddress);
        System.out.println();
        System.out.println("balanceOfETH:" + balanceOfETH);

    }


    public static void testBalanceOfOCN() {
        String balanceOfETH = OWalletAPI.balanceOfContractToken(walletAddress, ContractAddress);
        System.out.println();
        System.out.println("balanceOfETH:" + balanceOfETH);

    }


    /**
     * gasPrice: 30000000000
     * gasLimit: 200000
     * To: 0x7e8247c7d145debe8a8c2d2a2ab450992aa884c9
     * Data:
     */

    public static final String EthAmount = "0.05";//( 0.05 Eth)
    public static final String toAddress = "0x7e8247c7d145debe8a8c2d2a2ab450992aa884c9";
    public static final String gas_price = "30000000000";
    public static final String gas_limit = "200000";
    public static final String data = "";


    public static void testTransactionEth() {
        try {
            ECKeyPair eckeyPair = OWalletUtils.getEckeyPair(privateKey);
            String txHash = OWalletAPI.transactionEth(eckeyPair, EthAmount, toAddress, gas_price, gas_limit, data);
            System.out.println();

            System.out.println("txHash:" + txHash);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static final String testAmount = "0.05";//(实际额为0.05 Eth)

    public static void transactionOCN() {
        try {
            ECKeyPair eckeyPair = OWalletUtils.getEckeyPair(privateKey);
            String txHash = OWalletAPI.transactionOnContract(eckeyPair, testAmount, toAddress, gas_price, gas_limit, data, ContractAddress);
            System.out.println();

            System.out.println("txHash:" + txHash);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
