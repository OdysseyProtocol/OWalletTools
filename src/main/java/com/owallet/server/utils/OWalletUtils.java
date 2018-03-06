package com.owallet.server.utils;

import com.owallet.server.bean.WalletInfo;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by y on 2018/3/5.
 */
public class OWalletUtils {


    /**
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static   WalletInfo generateWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        return new WalletInfo(ecKeyPair);
    }


    /**
     * @param password
     * @param ecKeyPair
     * @return
     * @throws CipherException
     */
    public static  WalletFile getWalletFile(String password, ECKeyPair ecKeyPair) throws CipherException {
        return Wallet.createStandard(password, ecKeyPair);
    }


    /**
     * @param ecKeyPair
     * @return
     */
    public static  String getWalletPrivateKey(ECKeyPair ecKeyPair) {
        byte[] bytes = new BigInteger(ecKeyPair.getPrivateKey().toString()).toByteArray();
        byte[] encode = Hex.encode(bytes);
        return new String(encode);
    }


    /**
     * @param privateKey
     * @return
     */
    public static  ECKeyPair getEckeyPair(String privateKey) {
        //通用 private key 还原 big integer private key
        byte[] decodeCode = Hex.decode(privateKey.getBytes());
        BigInteger resultBig = Numeric.toBigInt(decodeCode);
        //通过 (BigInteger)privateKey 创建秘钥对
        return ECKeyPair.create(resultBig);
    }

}
