# FileEncryptor

FileEncryptor
FileEncryptor is a command-line program for encrypting and decrypting files using a key derived from a password by Password-Based Key Derivation Function 2 (PBKDF2) and Advanced Encryption Standard (AES) in Cipher Block Chaining (CBC) mode with PKCS#5 padding.

Getting started
To use the program, simply download the source code and compile it using a Java compiler. Then, run the program using the following command-line arguments:

php
Copy code
java FileEncryptor <mode> <input-file> <output-file> <password>
where <mode> is either "e" for encryption or "d" for decryption, <input-file> is the name of the input file to encrypt or decrypt, <output-file> is the name of the output file to write the encrypted or decrypted data to, and <password> is the password to use for key derivation.

How it works
The program reads in a file, generates a secret key based on a password using PBKDF2, initializes a cipher object for encryption or decryption using AES in CBC mode with PKCS#5 padding, reads and writes the data in blocks of 128 bytes, finalizes the encryption or decryption process, and closes the input and output streams.

Dependencies
The program uses the following Java libraries for file I/O and cryptographic operations:

BufferedInputStream
BufferedOutputStream
FileInputStream
FileOutputStream
InvalidAlgorithmParameterException
InvalidKeyException
NoSuchAlgorithmException
SecureRandom
InvalidKeySpecException
KeySpec
Cipher
SecretKey
SecretKeyFactory
IvParameterSpec
PBEKeySpec
SecretKeySpec
NoSuchPaddingException
IllegalBlockSizeException
BadPaddingException
