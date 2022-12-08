# FileEncryptor


The file Encryptor is a simple Encrypting/Decrypting java program. The Encryption algorithm used in this program is PBKDF2WithHmacSHA256. 

  Algolo = AES/CBC/PKCS5PADDING
  First, check the user has provided all the required arguments, and if they haven't, tell them then   exit
  Open the input file ~  in = new BufferedInputStream(new FileInputStream(args[1]));
  And then the output file ~  out = new BufferedOutputStream(new FileOutputStream(args[2]));
  Create a PBKDF2 secret key factory -- PBKDF2WithHmacSHA256.  ~  kf = SecretKeyFactory.getInstance(algorithm);
  Set up a KeySpec for password-based key generation of a 128-bit key ~ ks = new PBEKeySpec(password, salt, iterations, keyLength);
  run the passphrase through PBKDF2 to get the key ~ key = kf.generateSecret(ks);
  Get the byte encoded key value as a byte array ~ byte[] aeskey = key.getEncoded();
  
  ... 
  ...
  
  ...
  
  THe code has been commented with all the necessary steps !

The program ask for a binary file to be encrypted in infile20 and the output is saved in outfile20. The IV file contains the IV 
