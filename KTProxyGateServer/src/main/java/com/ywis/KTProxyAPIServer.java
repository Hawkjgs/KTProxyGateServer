package com.ywis;


 
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import sm.comm.AES128;
 

@SpringBootApplication
@EnableScheduling

//DB 접속 없이 동작 
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class KTProxyAPIServer {
	
 	public static void main(String[] args) {
 		
 		
// 		String key = "KT!wlsmdakd1588!";
//		
//		AES128 aes = new AES128();
//		 
//		String encrypt = "9QP/h4OD+MyOFDaBdsgN/g==";//aes.encrypt(txt);
//		String decrypt;
//		try {
//			
//			//encrypt = aes.strEncode(encrypt, key);
//			decrypt = aes.strDecode(encrypt,key);
//			System.out.println("암호화 : " + encrypt);
//			System.out.println("복호화 : " + decrypt);
//
//		} catch (InvalidKeyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchPaddingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidAlgorithmParameterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalBlockSizeException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (BadPaddingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		
//		//System.out.println("평문 : " + txt);
//		
//		
 		
// 		KT!wlsmdakd1588!
 		
		SpringApplication.run(KTProxyAPIServer.class, args);
	}
	 
}
