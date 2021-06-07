#!/bin/sh
cd app
gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PASSPHRASE" \
--output "google-services.json" "google-services.json.gpg"
cd src/main/java/main/stager
gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PASSPHRASE" \
--output "SECRETS.java" "SECRETS.java.gpg"