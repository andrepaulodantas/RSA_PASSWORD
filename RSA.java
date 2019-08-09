/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;
/**
 *
 * @author andre
 */
import java.math.BigInteger;  
import java.util.Random; 
import java.io.File;
import java.io.*; 
import java.nio.charset.Charset;
import sun.nio.cs.UnicodeEncoder;
  
  
public class RSA {  
    
    //todos os numeros abaixo são na realidade objetos
    
    //p => primo1
    //q => primo2
    //N => multiplicação entre p e q
    //phi => resultado da função totiente <phi(n) = (p-1)(q-1)>
    //e => inteiro 'e' tal que 1 < 'e', < phi(n), de forma que 'e', e 'phi (n)', sejam primos entre si.
    //d => Numero calculado, de forma que 'd' * 'e' equivale a modulo(phi(n)), ou seja, 'd', seja o inverso multiplicativo de 'e', em modulo(phi(n))
    //r => random
    private BigInteger p;  
    private BigInteger q;  
    private BigInteger N;  
    private BigInteger phi;  
    private BigInteger e;  
    private BigInteger d;  
    private int bitlength = 512;  
    private int blocksize = 256; //blocksize in byte  
    private Random r;  
    
     public RSA() {  
         
        r = new Random();//cria objeto Random()
        p = BigInteger.probablePrime(bitlength, r);// atribui a p um provavel primo  
        q = BigInteger.probablePrime(bitlength, r);// atribui a q um provavel primo  
        N = p.multiply(q);// N é o resultado da multiplicação entre p e q
            
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));//atribui a phi a função totiente de N  
        e = BigInteger.probablePrime(bitlength/2, r); // atribui a e um provavel primo, menor que a função totiente
          

        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && phi.compareTo(e) > 0 ) {  
           e.add(BigInteger.ONE);  
        }  
        d = e.modInverse(phi);   // calculo do valor 'd' para o RSA
        //System.setProperty("file.encoding", "UTF-8");
    }  
      
    public RSA(BigInteger e, BigInteger d, BigInteger N) {  
        this.e = e;  
        this.d = d;  
        this.N = N;  
    }  
      
    public static void main (String[] args) throws IOException
{  
          
   
        RSA rsa = new RSA();
        
        encrypter(rsa);
        decrypter(rsa);
 
    } 
    
    
       //concatena os bytes e transforma em uma string
   private static void encrypter(RSA rsa) throws UnsupportedEncodingException, IOException {  
       
        //encriptação
        String teststring = null;
        File file1 = new File("entrada.txt");
        teststring = ler(file1);

        System.out.println("Encrypting Message: " + teststring);  
        //System.out.println("String in Bytes: " + bytesToString(teststring.getBytes())); 
       
        // encrypt  
        byte[] encrypted = rsa.encrypt(teststring.getBytes("ISO-8859-1"));
        String value = new String(encrypted,"ISO-8859-1");
          
        
        FileWriter writer = new FileWriter("criptografado.txt");
        PrintWriter saida = new PrintWriter(writer);
        saida.print(value);
        saida.close();
        writer.close();
        
        FileWriter writer3 = new FileWriter("key.txt");
        PrintWriter saida3 = new PrintWriter(writer3);
       
        saida3.println("Public key: " + "<" + rsa.N + "," + rsa.e + ">");
        saida3.println("Private key: " + "<" + rsa.N + "," + rsa.d + ">");
        saida3.close();
        writer3.close();
    } 
   
   private static void decrypter(RSA rsa) throws UnsupportedEncodingException, IOException {
      //decriptação

        String encrypted2 = null;
        File file = new File("criptografado.txt");
        encrypted2 = ler(file);
        
        byte []aux = encrypted2.getBytes("ISO-8859-1");
        
      
        // decrypt  
        byte[] decrypted = rsa.decrypt(aux);
        
        FileWriter writer2 = new FileWriter("saida.txt");
        PrintWriter saida2 = new PrintWriter(writer2);
        saida2.print(new String(decrypted));
        saida2.close();
        writer2.close();
        
        System.out.println("Message Decrypted :"+new String(decrypted));
   }
   
    
   //concatena os bytes e transforma em uma string
   private static String bytesToString(byte[] encrypted) {  
        String test = "";  
        for (byte b : encrypted) {  
            test+= Byte.toString(b);  
        }  
        return test;  
    }  
      
    //Para obter a cifração, fazemos c = m^e mod(n) 
    public byte[] encrypt(byte[] message) {   
        System.out.println("Encrypted Message: " + new String ((new BigInteger(message)).modPow(e, N).toByteArray()));
        return (new BigInteger(message)).modPow(e, N).toByteArray();  
    }  
    
    //Para obter a decifração, fazemos m = c^d mod(n) 
    public byte[] decrypt(byte[] message) throws UnsupportedEncodingException {
        //System.out.println("chega no decrypt: " + new String(message));
        return (new BigInteger(message)).modPow(d, N).toByteArray();  
    }  
    
    //lê o que está em um arquivo
    public static String ler(File arquivo)
    {
        StringBuilder sb = new StringBuilder();
        try {
        FileReader reader = new FileReader(arquivo);
        int c;
        do {
            c = reader.read();
        if (c!=-1) {
            sb.append( (char)c );
        }
        } while (c != -1);
            reader.close();
        } catch (IOException e) {
        }
        return sb.toString();
    }
    
    public static String bytesToStringUTFCustom(byte[] bytes) {
	 char[] buffer = new char[bytes.length >> 1];
	 for(int i = 0; i < buffer.length; i++) {
	    int bpos = i << 1;
	    char c = (char)(((bytes[bpos]&0x00FF)<<8) + (bytes[bpos+1]&0x00FF));
	    buffer[i] = c;
	 }
	 return new String(buffer);
    }
    
    public static byte[] stringToBytesUTFCustom(String str) {
	 char[] buffer = str.toCharArray();
	 byte[] b = new byte[buffer.length << 1];
             for(int i = 0; i < buffer.length; i++) {
                  int bpos = i << 1;
                  b[bpos] = (byte) ((buffer[i]&0xFF00)>>8);
                  b[bpos + 1] = (byte) (buffer[i]&0x00FF);
             }
	 return b;
	}


}
