#!/bin/bash

/*
bash keys.sh
Enter keystore password:
Re-enter new password:
What is your first and last name?
  [Unknown]:  terry
What is the name of your organizational unit?
  [Unknown]:  cnn
What is the name of your organization?
  [Unknown]:  cnn_org
What is the name of your City or Locality?
  [Unknown]:  shanghai
What is the name of your State or Province?
  [Unknown]:  shanghai
What is the two-letter country code for this unit?
  [Unknown]:  cn
Is CN=terry, OU=cnn, O=cnn_org, L=shanghai, ST=shanghai, C=cn correct?
  [no]:  yes

Enter key password for <privatekey>
	(RETURN if same as keystore password):
*/

# create a private key store
keytool -genkey -alias privatekey -keystore privateKeys.store

# export the public key to a certificate file
#keytool -export -alias privatekey -file certfile.cer -keystore privateKeys.store

# import the certificate containing the public key into a new key store that will be distributed with the application
#keytool -import -alias publiccert -file certfile.cer -keystore publicCerts.store



