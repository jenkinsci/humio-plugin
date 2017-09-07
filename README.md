# Humio Plugin for Jenkins

**This project is WIP, use at your own peril!**

This plugin will automatically ship all build logs to Humio.

## Usage

Go to `Manage Jenkins` -> `Global Configuration` and find the Humio section.
You will need to put in the `dataspaceId` and the auth token for your user.
The auth token kan be found in Humio under your account information.

## Installation

Build the HPI package using:

```shell
mvn package
```

Upload the `hpi` file from the target directory to Jenkins,
under `Plugins` > `Advanced`.
