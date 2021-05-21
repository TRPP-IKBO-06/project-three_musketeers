#!/bin/sh
cd app
gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PASSPHRASE" \
--output "google-services.json" "google-services.json.gpg"
