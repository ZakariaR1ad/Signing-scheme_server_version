import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.Scanner;


public class Alice {

    public static void main(String[] args) throws Exception {
        try {
            int port = 8088;
            BigInteger alpha, p,q,Na, sk;
            Scanner scanner = new Scanner(System.in);

            // Safely generate Alice Keys
            /*p = BigInteger.probablePrime(256,new Random());
            q = BigInteger.probablePrime(256,new Random());
            alpha = BigInteger.probablePrime(64,new Random());*/
            System.out.println("Enter the first prime (p) >");
            p = new BigInteger(scanner.next());

            System.out.println("Enter the first prime (q) >");
            q = new BigInteger(scanner.next());

            System.out.println("Enter the intger alpha >");
            alpha = new BigInteger(scanner.next());
            Na = p.multiply(q);


            // Established the Connection
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Waiting for bob on port " + serverSocket.getLocalPort() + "...");
            Socket alice = serverSocket.accept();
            System.out.println("Just connected to " + alice.getRemoteSocketAddress());

            OutputStream outToclient = alice.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToclient);

            DataInputStream in = new DataInputStream(alice.getInputStream());
            //Sending the public key
            out.writeUTF(alpha.toString());
            out.writeUTF(Na.toString());

            //receiving v1
            BigInteger beta_alpha = new BigInteger(in.readUTF());
            BigInteger beta_beta = new BigInteger(in.readUTF());

            //Verify
            // compute the inverse
//            BigInteger inverse_alpha = alpha.modInverse((p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)));
            BigInteger inverse_alpha = Inverse(alpha,p);
            //compute beta1
            BigInteger beta_1 = beta_alpha.modPow(inverse_alpha,Na);
            //verify if beta1^beta1 == beta^beta
            if(! beta_beta.equals(beta_1.modPow(beta_1,Na))){
                throw new Exception("Did not pass the verification....");
            }
            else{
                sk = beta_1;
                System.out.println("The signing key is set to "+sk);

            }
            //sending v2
            BigInteger v2 = beta_1.modPow(alpha,Na);
            out.writeUTF(v2.toString());

            //waiting for bob response
            String resp = in.readUTF();
            System.out.println(resp);

            alice.close();
        }

        catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        }
        catch (Exception e) {
            System.out.println("Verification failed");

        }
    }
    public static BigInteger Inverse(BigInteger a, BigInteger p){
        BigInteger i = BigInteger.ONE;

        while(true){
            BigInteger tmp = p;
            tmp = tmp.add((p.subtract(BigInteger.ONE)).multiply(i));
            if(tmp.mod(a).equals(BigInteger.ZERO)){
                return tmp.divide(a);
            }
            i=i.add(BigInteger.ONE);
        }
    }
}
