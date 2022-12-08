import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Program to encrypt and decrypt files using a key derived from a password by PBKDF2
 * and AES-128
 */

/**
 * @author les
 * @version $Revision: 1.1 $
 */
public class FileEncryptor {

    private static final String progName = "FileEncryptor";
    private static final int bufSize = 128;

    /**
     * @param args
     */
    public static void main(String[] args) {

        BufferedInputStream in = null;            // A buffered input stream to read from
        BufferedOutputStream out = null;          // And a buffered output stream to write to
        SecretKeyFactory kf = null;               // Something to create a key for us
        KeySpec ks;                        	      // This is how we specify what kind of key we want it to generate
        byte[] salt = new byte[20];               // Some salt for use with PBKDF2, only not very salty
        SecretKey key = null;                     // The key that it generates
        Cipher cipher = null;                     // The cipher that will do the real work
        SecretKeySpec keyspec = null;             // How we pass the key to the Cipher
        int bytesRead = 0;                        // Number of bytes read into the input file buffer
        final int iterations = 1024;              // Number of iterations required to create a password from the key
        byte[] initializationVector = new byte[16];
        IvParameterSpec ivParameterSpec;

        // First, check the user has provided all the required arguments, and if they haven't, tell them then exit
        if (args.length != 4) {
            printUsageMessage();
            System.exit(1);
        }

        // Open the input file
        try {
            in = new BufferedInputStream(new FileInputStream(args[1]));
        } catch (FileNotFoundException e) {
            printErrorMessage("Unable to open input file: " + args[1], null);
            System.exit(1);
        }

        // And then the output file
        try {
            out = new BufferedOutputStream(new FileOutputStream(args[2]));
        } catch (FileNotFoundException e) {
            printErrorMessage("Unable to open output file: " + args[2], e);
            System.exit(1);
        }

        // Create a PBKDF2 secret key factory
        String algorithm = "PBKDF2WithHmacSHA256";	//stating the algorithm name
        try {
            kf = SecretKeyFactory.getInstance(algorithm);	// calling secret key factory to get the instance of "algorithm" 
        } catch (NoSuchAlgorithmException e) {
            printErrorMessage(algorithm + " not found", e);	// throws error message if "algo" not found
            System.exit(2);
        }

        // Set up a KeySpec for password-based key generation of a 128-bit key
        char[] password = args[3].toCharArray();		// argument 3 is E > Infile20.txt > outfile20.txt > [3]"passphrase"
        int keyLength = 128;							// 128 bit key
        ks = new PBEKeySpec(password, salt, iterations, keyLength); //PBEKeySpec specified with password, salty salt and 1024 iteration w keylength

        // Now run the passphrase through PBKDF2 to get the key
        try {
            key = kf.generateSecret(ks);		// generates the key for use
        } catch (InvalidKeySpecException e) {
            printErrorMessage("Invalid key specification exception", e);
            System.exit(2);
        }

        // Get the byte encoded key value as a byte array
        byte[] aeskey = key.getEncoded();

        // Now generate a Cipher object for AES encryption in ECB or CBC mode with PKCS #5 padding
        // Use ECB for the first task, then switch to CBC for versions 2 and 3
        try {
            String algolo = "AES/CBC/PKCS5PADDING";
            cipher = Cipher.getInstance(algolo);
        } catch (NoSuchAlgorithmException e) {
            printErrorMessage("No Such Algorithm Exception when creating main cipher", e);
            System.exit(2);
        } catch (NoSuchPaddingException e) {
            printErrorMessage("No Such Padding Exception when creating main cipher", e);
            System.exit(2);
        }

        // Set a variable to indicate whether we're in encrypt or decrypt mode, based upon args[0]
        int cipherMode = -1;
        char mode = Character.toLowerCase(args[0].charAt(0));
        switch (mode) {
            case 'e':
                cipherMode = Cipher.ENCRYPT_MODE;
                break;
            case 'd':
                cipherMode = Cipher.DECRYPT_MODE;
                break;
            default:
                printUsageMessage();
                System.exit(1);
        }

        // Set up a secret key specification, based on the 16-byte (128-bit) AES key array previously generated
        algorithm = "AES";
        keyspec = new SecretKeySpec(aeskey, algorithm);
        try {
            String ivTextFile = "iv.txt"; 			// CREATE A TEXT FILE iv.txt for Storing IV
            if (cipherMode == Cipher.ENCRYPT_MODE) {
                new SecureRandom().nextBytes(initializationVector);
                @SuppressWarnings("resource")
				FileOutputStream fileOutputStream = new FileOutputStream(ivTextFile);
                fileOutputStream.write(initializationVector);
            }
            if (cipherMode == Cipher.DECRYPT_MODE) {
                @SuppressWarnings("resource")
				FileInputStream ivFileInputStream = new FileInputStream(ivTextFile);
                ivFileInputStream.read(initializationVector);
            }
        } catch (IOException e) {
            printErrorMessage("File read/write failed", e);
            System.exit(1);
        }

        // Now initialize the cipher in the right mode, with the keyspec and the ivspec
        try {
            ivParameterSpec = new IvParameterSpec(initializationVector);
            cipher.init(cipherMode, keyspec, ivParameterSpec);
        } catch (InvalidKeyException e) {
            printErrorMessage("Invalid Key Spec", e);
            System.exit(2);
        } catch (InvalidAlgorithmParameterException e) {
            printErrorMessage("Invalid Algorithm Parameter", e);
            System.exit(2);
        }

        // Set up some input and output byte array buffers
        byte[] inputBuffer = new byte[bufSize];
        byte[] outputBuffer = null;

        // "Prime the pump" - we've got to read something before we can encrypt it
        // and not encrypt anything if we read nothing.
        try {
            bytesRead = in.read(inputBuffer);
        } catch (IOException e) {
            printErrorMessage("Error reading input file " + args[1], e);
            System.exit(1);
        }

        // As long as we've read something, loop around encrypting, writing and reading
        // bytesRead will be zero if nothing was read, or -1 on EOF - treat them both the same
        while (bytesRead > 0) {
            // Now encrypt this block
            outputBuffer = cipher.update(inputBuffer, 0, bytesRead);

            // Write the generated block to file
            try {
                out.write(outputBuffer);
            } catch (IOException e) {
                printErrorMessage("Error writing to output file " + args[2], e);
                System.exit(1);
            }

            // And read in the next block of the file
            try {
                bytesRead = in.read(inputBuffer);
            } catch (IOException e) {
                printErrorMessage("Error reading input file " + args[1], e);
                System.exit(1);
            }
        }

        // Now do the final processing
        try {
            outputBuffer = cipher.doFinal();
        } catch (IllegalBlockSizeException e) {
            printErrorMessage(e.getMessage(), e);
            System.exit(2);
        } catch (BadPaddingException e) {
            printErrorMessage(e.getMessage(), e);
            System.exit(2);
        }

        // Write the final block of output
        try {
            out.write(outputBuffer);
        } catch (IOException e) {
            printErrorMessage("Error on final write to output file " + args[2], e);
            System.exit(1);
        }

        // Close the output files
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            printErrorMessage("Error closing file", e);
        }

        // If we were continuing beyond this point, we should really overwrite key material, drop KeySpecs, etc.
    }

    /**
     * Print an error message on stderr, optionally picking up additional detail from
     * a passed exception
     *
     * @param errMsg
     * @param e
     */
    private static void printErrorMessage(String errMsg, Exception e) {
        System.err.println(errMsg);
        if (e != null)
            System.err.println(e.getMessage());
    }

    /**
     * Print a usage message
     */
    private static void printUsageMessage() {
        System.out.println(progName + " $Revision: 1.1 $: Usage: " + progName + " E/D infile outfile passphrase");
    }

}
