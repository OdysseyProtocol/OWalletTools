package com.owallet.server.transaction;

import com.google.gson.Gson;
import com.owallet.server.api.EtherScanApi;
import com.owallet.server.bean.EtherScanResponse;
import com.owallet.server.utils.RawTransactionUtils;
import com.owallet.server.utils.RequestUtils;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static com.owallet.server.api.EtherScanApi.*;
import static org.web3j.abi.Utils.convert;

/**
 * Created by y on 2018/3/5.
 */
public class OWalletTransaction {


    /**
     * @param walletAddress
     * @return
     */
    public static String balanceOfETH(String walletAddress) {
        String responseResult = RequestUtils.sendGet(getBalanceUrl(walletAddress));
        responseResult = responseResult.replace("/n", "");
        Gson gson = new Gson();
        EtherScanResponse responseToken = gson.fromJson(responseResult, EtherScanResponse.class);
        String balanceOfETH = new BigDecimal(responseToken.result).divide(new BigDecimal(1000000000000000000d), 7, BigDecimal.ROUND_UP).toPlainString();
        return balanceOfETH;

    }


    /**
     * @param walletAddress
     * @param contractAddress
     */
    public static String balanceOfContractToken(String walletAddress, String contractAddress)  {
        Function function = new Function("balanceOf",
                Arrays.<Type>asList(new Address(walletAddress)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        String data = FunctionEncoder.encode(function);
        String url = eth_call(contractAddress, data);
        String responseResult = RequestUtils.sendGet(url);
        responseResult = responseResult.replace("/n", "");
        Gson gson = new Gson();
        EtherScanResponse responseToken = gson.fromJson(responseResult, EtherScanResponse.class);
        String result = responseToken.result;
        if (!"".equals(result) && result.startsWith("0x")) {
            List<TypeReference<?>> typeReferences = Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
            });
            List<Type> decode = FunctionReturnDecoder.decode(result, convert(typeReferences));
            BigDecimal ethbal = new BigDecimal(decode.get(0).getValue().toString());
            BigDecimal divide = ethbal.divide(new BigDecimal(1000000000000000000d), 3, BigDecimal.ROUND_UP);
            return divide.toString();
        }
        throw new RuntimeException("get token fail");
    }


    /**
     * @param ethAmount eg:"0.05";//(实际额为0.05 Eth)
     * @param toAddress eg:"0x7e8247c7d145debe8a8c2d2a2ab450992aa884c9";
     * @param gas_price eg:"30000000000";
     * @param gas_limit eg:"200000"
     * @param data      default ""
     * @param ecKeyPair
     * @return txHex
     * @throws IOException
     * @throws InterruptedException
     */
    public static String transactionEth(ECKeyPair ecKeyPair, String ethAmount, String toAddress, String gas_price, String gas_limit, String data) throws IOException, InterruptedException {
        Credentials credentials = Credentials.create(ecKeyPair);
        String walletAddress = credentials.getAddress();
        String responseResult = RequestUtils.sendGet(getNonceForAddress(walletAddress));
        responseResult = responseResult.replace("/n", "");
        Gson gson = new Gson();
        EtherScanResponse responseToken = gson.fromJson(responseResult, EtherScanResponse.class);
        BigInteger nonce = new BigInteger(responseToken.result.substring(2), 16);
        RawTransaction tx = RawTransactionUtils.getTransaction(nonce, null, ethAmount, gas_price, gas_limit, data, toAddress);
        System.out.println("Nonce:" + tx.getNonce() + "\n" +
                "gasPrice: " + tx.getGasPrice() + "\n" +
                "gasLimit: " + tx.getGasLimit() + "\n" +
                "To: " + tx.getTo() + "\n" +
                "Amount: " + tx.getValue() + "\n" +
                "Data: " + tx.getData());
        byte[] signed = TransactionEncoder.signMessage(tx, (byte) CHAIN_ID, credentials);
        String url = forwardTransaction("0x" + Hex.toHexString(signed));
        //进行交易需要先获得nonce,该账号的交易次数
        String transactionResp = RequestUtils.sendGet(url);
        transactionResp = transactionResp.replace("/n", "");
        EtherScanResponse txHashResponse = gson.fromJson(transactionResp, EtherScanResponse.class);
        return txHashResponse.result;

    }



    /**
     * @param ecKeyPair
     * @param OCNAmount
     * @param toAddress
     * @param gas_price
     * @param gas_limit
     * @param data
     * @param ERC20Address
     * @return
     * @throws IOException
     * @throws InterruptedException
     */

    public static String transactionOnContract(ECKeyPair ecKeyPair, String OCNAmount, String toAddress, String gas_price, String gas_limit, String data, String ERC20Address) throws IOException, InterruptedException {
        Credentials credentials = Credentials.create(ecKeyPair);
        String walletAddress = credentials.getAddress();
        String responseResult = RequestUtils.sendGet(getNonceForAddress(walletAddress));
        responseResult = responseResult.replace("/n", "");
        Gson gson = new Gson();
        EtherScanResponse responseToken = gson.fromJson(responseResult, EtherScanResponse.class);
        BigInteger nonce = new BigInteger(responseToken.result.substring(2), 16);
        RawTransaction tx = RawTransactionUtils.getTransaction(nonce, ERC20Address, OCNAmount, gas_price, gas_limit, data, toAddress);
        System.out.println("Nonce:" + tx.getNonce() + "\n" +
                "gasPrice: " + tx.getGasPrice() + "\n" +
                "gasLimit: " + tx.getGasLimit() + "\n" +
                "To: " + tx.getTo() + "\n" +
                "Amount: " + tx.getValue() + "\n" +
                "Data: " + tx.getData());
        byte[] signed = TransactionEncoder.signMessage(tx, (byte) EtherScanApi.CHAIN_ID, credentials);
        String url = forwardTransaction("0x" + Hex.toHexString(signed));
        String transactionResp = RequestUtils.sendGet(url);
        transactionResp = transactionResp.replace("/n", "");
        EtherScanResponse txHashResponse = gson.fromJson(transactionResp, EtherScanResponse.class);
        return txHashResponse.result;

    }


    public static final String txHash = "0x1a763acca69a048d413311bdf2ce795b01415c5220892dcb5765a47fce559143";


    public void transactionReceipt() {
        String url = getTransactionReceipt(txHash);
        String responseResult = RequestUtils.sendGet(url);
        responseResult = responseResult.replace("/n", "");
        System.out.println("TransactionReceipt:" + responseResult);
    }


}
