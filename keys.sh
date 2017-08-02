#!/bin/bash

keytool -genkey -alias privatekey -keystore privateKeys.store

keytool -export -alias privatekey -file certfile.cer -keystore privateKeys.store

keytool -import -alias publiccert -file certfile.cer -keystore publicCerts.store



