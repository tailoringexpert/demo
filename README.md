# tailoringexpert-demo-plattform

Sample for tenant implementention of plattform interfaces.
Also a run configuration for plattform using the demo tenant is provided

## Modules

### tailoringexpert-demo-catalog

Example catalog to use where url are replaced by running maven resource plugin.

### tailoringexpert-demo-config

Spring configuration of demo implementation of plattform interfaces.

### tailoringexpert-demo-core

Demo implementation of required functionalities.

### tailoringexpert-demo-db

Liquibase to create and maintain db. Also launch configurations are provided.

### tailoringexpert-demo-distribution

Sample for packaging libraries to install libraries into plattform via maven assembly.

### tailoringexpert-demo-integrationtest

Integrationtest to test demo implementations used by the plattform.

### tailoringexpert-demo-templates

Templates used for generating documents.

### tailoringexpert-demo-assets

Images and resources to use for output document creation

### tailoringexpert-demo-www

Static html like help, dataprotection and impressum 

## Limitations

All tenant specific interfaces are implemented in a very simple way to fulfill plattform requirements.
Implementations are to be understand as _non productive_ examples!

## System-Plattform configuration

The easiest way to configure the demo plattform is using a _XAMP_ stack.
The stack can be downloaded as portable version from [Apache Friends](https://www.apachefriends.org/de/index.html) .

### Installation

Unzip file in any directory.
In case of a Windows environment you first have to run batchfile _setup_xampp.bat_.
This script will configure paths and writes them to the corresponding configuration files.

#### Apache Webserver

To access catalog pictures using the apache httpd websever it is required to create a *vhost*.
An example is given at

> src/assembly/localhost/apache/conf/extra/httpd_vhost.conf

This file has to be copied starting from *apache* to directory  **%XAMPP_HOME%/apache/conf/extra**.
After that change directories to system needs.

#### MariaDB Datenbank

Before first run of database encoding shall be changed to **UTF 8** geändert.
An example is file

> %XAMPP_HOME%/mysql/bin/my.conf

Section of **UTF 8 Settings** has to be changed to

    ## UTF 8 Settings
    #init-connect=\'SET NAMES utf8\'
    collation_server=utf8_unicode_ci
    character_set_server=utf8
    skip-character-set-client-handshake
    #character_sets-dir="/Users/baed_mi/entwicklung/seu/xamp/xampp-portable-windows-x64-8.1.6-0-VS16/mysql/share/charsets"
    sql_mode=NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION
    log_bin_trust_function_creators = 1

Admin account _*root*_ is **passwordless**!

Following commands can be used to create user, database and grant permissions:

    CREATE USER 'tailoringexpert_demo'@'localhost' IDENTIFIED BY 'test1234';
    CREATE DATABASE TAILORINGEXPERT_DEMO CHARACTER SET utf8mb4;
    GRANT ALL PRIVILEGES ON TAILORINGEXPERT_DEMO.* TO 'tailoringexpert_demo'@'localhost';

### Start

The easiest way to start webserver and database is using _xampp-control.exe_ app.

### Database

To create/update database for plattform tenant, there are several maven exec tasks definded in `tailoringexpert-demo-db/pom.xml`.
There is also a prepared intellij launch configuration. For general approch, please see `README.md` in 
module `tailoringexpert-demo-db".

### Plattform
To run the application create a run configuration in intellij using `App.class` and add `.env` file.

### Base catalog
Use a rest client to upload an initial base catalog.
An example base catalog can be found [here](src/test/resources/basecatalog.json).
It is necessary to add  header attribute `X-Tenant` with value `demo`. 

### Web
In project `tailoringexpert-web` contains a `.env.development.local.example` file. Copy this to `.env.development.local`
and set `VUE_APP_TENANT=demo`.