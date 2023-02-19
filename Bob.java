import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Bob {
    public static void main(String[] args){
        try {
            Scanner scanner = new Scanner(System.in);
            BigInteger N, alpha, beta,sk;
            //beta = BigInteger.probablePrime(64,new Random());
            System.out.println("Enter the private key (beta) >");
            beta = new BigInteger(scanner.next());

            String serverName = "localhost";
            int port = 8088;

            // Established the connection
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);
            System.out.println("Just connected to " + client.getRemoteSocketAddress());

            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            DataInputStream in = new DataInputStream(client.getInputStream());



            //Receive public key
            alpha  = new BigInteger(in.readUTF());
            N  = new BigInteger(in.readUTF());


            //Sending V1
            BigInteger beta_alpha = beta.modPow(alpha,N);
            BigInteger beta_beta = beta.modPow(beta,N);

            out.writeUTF(beta_alpha.toString());
            out.writeUTF(beta_beta.toString());

            //Receiving V2
            BigInteger v2 = new BigInteger(in.readUTF());

            if(! v2.equals(beta.modPow(alpha,N))){
                throw new Exception("Verification failed....");
            }
            else{
                sk = beta;
                System.out.println("The signing key is set to "+sk);
                out.writeUTF("Key exchange finished");
            }
            client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
