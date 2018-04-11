package p3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.CipherInputStream;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * 
 * @author Javier Ballesteros Morón
 * @author Carlos Muñoz Zapata
 *
 */

public class Main {
	/**
	 * Metodo que pide un dato por pantalla y lo devuelve
	 * @return String -> dato
	 */
	private static KeyPair keyPair = null;
	
	private static String pedirDato() {
	        String entradaTeclado = "";
	        Scanner entradaEscaner = new Scanner (System.in); //CreaciÃ³n de un objeto Scanner
	        entradaTeclado = entradaEscaner.nextLine (); //Invocamos un mÃ©todo sobre un objeto Scanner
	        
	        if(entradaEscaner != null){
	        	
	        }
	        //0 entradaEscaner.close();
	        return entradaTeclado;
	}
	
	
	/**
	 * Método que se encarga del cifrado del fichero
	 * @param header creado previamente.
	 * @return true si termina la ejecución del método.
	 * @throws NoSuchAlgorithmException 
	 */
	
	private static void generateKey(Header header) throws NoSuchAlgorithmException{
		//Se obtiene una instancia
		int size = 512;
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		//se inicializa el generador con un tamaño de 512 bits
		kpg.initialize(size);
		
		//Generamos el par de llaves
		keyPair = kpg.generateKeyPair();
		
		//Obtenemos la clave pública y la privada
		PublicKey pubk = keyPair.getPublic();
		PrivateKey privk= keyPair.getPrivate();
		
		System.out.println("The private key is: " + privk.serialVersionUID);
		System.out.println("The public key is: " + pubk.serialVersionUID);
		System.out.println("----------------------");
		System.out.println("The private key is: " + privk.serialVersionUID);
		System.out.println("The public key is: " + pubk.serialVersionUID);
		System.out.println("----------------------");
		
	}
	
	private static boolean cipher(Header header){
		String opcion;
			
		System.out.println("Algoritmo de cifrado: RSA/ECB/PKCS1Padding");
		
		//Generamos el header
		header = new Header("RSA/ECB/PKCS1Padding");

			
		//pedimos el nombre del archivo
		System.out.println("Introduzca el nombre completo del archivo a encriptar:");
		System.out.print("Debe estar en la carpeta raiz:\t");
		String rutaArchivoIn = pedirDato();
		System.out.println();	
		
		Cipher c = null;
	
		try {
			c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();	
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		//flujos de entrada y salida
		File inFile = new File(rutaArchivoIn);
		File outFile = new File(rutaArchivoIn + ".enc");
		
		FileOutputStream os = null;
		FileInputStream is = null;
		

		try {
			os = new FileOutputStream(outFile);
			is = new FileInputStream(inFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		header.save(os);
		
		int blockSize = 53;
		byte[] bff = new byte[blockSize];
		
		int length;
		try {

			while ((length = is.read(bff)) != -1) {
				byte outBuffer[] = c.doFinal(bff);
				System.out.print(length + ". ");
				os.write(bff, 0, length);
			}
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//aÃ±adimos la cabecera al fichero encriptado con los datos de la encriptacion
		
		
				
		//cerramos los flujos de los ficheros
		System.out.println("\nEstableciendo cabecera en el archivo encriptado...\n");
		try {
			
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		System.out.println("\n\n Finalizado con exito ~");
		return true;
	}
	/**
	 * Método que descifra un fichero
	 * @param header creado previamente.
	 * @return true si termina la ejecución del método
	 */
	
	/*
	private static boolean decipher(Header header, PrivateKey keypair){
		String pwd = new String();
		//obtener ruta del archivo (preguntar por pantalla)
		System.out.println("Introduzca el nombre completo del archivo a desencriptar:");
		System.out.print("Debe estar en la carpeta raiz:\t");
		String rutaArchivoIn = pedirDato();
		System.out.println();	
		
		//flujos de entrada y salida
		File inFile = new File(rutaArchivoIn);
		File outFile = new File(rutaArchivoIn + ".dec");
		
		FileOutputStream os = null;
		FileInputStream is = null;
		
		try {
			os = new FileOutputStream(outFile);
			is = new FileInputStream(inFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//cargar datos de la cabecera
		header.load(is);
		
		//desencriptar con los datos de la cabecera (salt, algoritmo)
		//pedimos las PASSWORD
		
		
	
		System.out.println("Introduzca la contraseña para el archivo:\t");
		pwd = pedirDato();

			
		PBEKeySpec pbeKeySpec = new PBEKeySpec(pwd.toCharArray());
		PBEParameterSpec pPS = new PBEParameterSpec(header.salt,ITERATION_COUNT);
	
		SecretKeyFactory kf;
		SecretKey sKey;
		Cipher c = null;
		
		try {
			kf = SecretKeyFactory.getInstance(header.getAlgorithm());
			sKey = kf.generateSecret(pbeKeySpec);
			
			c = Cipher.getInstance(header.getAlgorithm());
			c.init(Cipher.DECRYPT_MODE,sKey,pPS);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		
		CipherInputStream cis = new CipherInputStream(is,c);
			
		
		byte[] bff = new byte[TAM_BUFFER];
			
		int length;
		try {

			while ((length = cis.read(bff)) != -1) {
				os.write(bff, 0, length);
			
				System.out.print(".");
			}
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	
		try {
			
			os.close();
			is.close();
			cis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
		System.out.println("\n\n Finalizado con exito ~");
		return true;
			
	}
	*/
	
	/**
	 * Método principal que permite elegir entre cifrado y descifrado.
	 * @param args (no se requieren argumentos)
	 */
	public static void main(String[] args) {
		
		
		Header header = new Header();
		String opcion;
		
		boolean salir = false;
		
		while(!salir){
			System.out.print("Seleccione una opcion:\n"
					+ "[0]\tGenerar clave\n"
					+ "[1]\tEncriptar un fichero.\n"
					+ "[2]\tDesencriptar un fichero.\n"
					+ "[3]\tFirmar un fichero.\n"
					+ "[4]\tComprobarFirma.\n"
					+ "[5]\tSalir.\n"
					+ "\n-> ");
			opcion = pedirDato();
			System.out.println();
			
			switch(opcion.charAt(0)) {
			case '0':
				//GENERAR CLAVE
				try{
					generateKey(header);
					System.out.println("The private key is: " + keyPair.getPrivate().serialVersionUID);
					System.out.println("The public key is: " + keyPair.getPublic().serialVersionUID);
				} catch(Exception e){
					System.out.println("Error al generar clave.");
				}
				break;
			case '1':
				//ENCRIPTAR UN FICHERO
				try{
					if(keyPair != null)
						salir = cipher(header);
					else 
						System.out.println("Genere antes una clave");
				} catch(Exception e){
					System.out.println("Error al cifrar.");
				}
				break;
			case '2':
				//DESENCRIPTAR UN FICHERO
				try{
					if(keyPair != null){
						//salir = decipher(header, keyPair.getPrivate());
					}
					else 
						System.out.println("Genere antes una clave");
				} catch(Exception e){
					System.out.println("Error al cifrar.");
				}
				break;
			case '3':
				//FIRMAR UN FICHERO
				try{
					//salir = firmar(...);
				} catch(Exception e){
					System.out.println("Error al firmar");
				}
				break;
			case '4':
				//FIRMAR UN FICHERO
				try{
					//salir = comprobarFirma(...);
				} catch(Exception e){
					System.out.println("Error al comprobar firma");
				}
				break;
			case '5':
				salir = true;
				break;
			default:
				System.out.println("Introduzca una opcion valida -> ");
				break;
			}
		}
		
	//Proceso de salida
		System.out.println("Saliendo...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
