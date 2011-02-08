== Using PostgreSQL on localhost with standard credentials ===

To use PostgreSQL with the standard settings you need to:
- Create a database called "acrsapp". Usually this means calling "createdb acrsapp" on the
  shell as postgres user
- Create a user "acrsapp" with password "acrsapp" and grant full access to the "acrsapp"
  database
- create a file "local/acrs.properties" in the project's root directory containing the line:
    persistenceUnitName=acrs-pgsql

A common sequence of commands on a Ubuntu installation looks like this (with abbreviated prompt):

    uqpbecke$ sudo su postgres
    postgres$ createdb acrsapp;
    postgres$ psql acrsapp;
    Welcome to psql 8.3.7, the PostgreSQL interactive terminal.

    Type:  \copyright for distribution terms
           \h for help with SQL commands
           \? for help with psql commands
           \g or terminate with semicolon to execute query
           \q to quit

    acrsapp=# create user acrsapp password 'acrsapp';
    CREATE ROLE
    acrsapp=# grant all on database acrsapp to acrsapp;
    GRANT

== Using MySQL on localhost with standard credentials ===

To use the ACRS portlet application with a MySQL database you need to:
- Create a database in Mysql named "acrsapp" use the "CREATE DATABASE acrsapp;" in the
  command line
- Execute the following command to grant access acrsapp database user:
    GRANT ALL ON acrsapp.* TO 'acrsapp'@'localhost' IDENTIFIED BY 'acrsapp' WITH GRANT OPTION;
- create a file "local/acrs.properties" in the project's root directory containing the line:
    persistenceUnitName=acrs-mysql

=== Advanced configuration ===

If you want to run against a non-local database or use different credentials, you will need
to edit the "persistence.xml" file which can be found in the "META-INF" folder. This folder can
be found in the root of the deployment or in the src/main/resources folder.

If you want to use another database you will need to also add a dependency to the matching JDBC
driver in the "pom.xml" file.

=== Compiling and Deploying
- Run the following command from the project base directory
 mvn package -Dmaven.test.skip=true && cp target/acrs-portlets.war /path/to/liferay-portal-5.2.3/deploy
- Then go into the Add Application section of liferay and drag and drop the portlet 
