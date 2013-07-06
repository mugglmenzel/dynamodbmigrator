DynamoDB Migrator
=================

The DynamoDB Migrator tool is an elegant and quick implementation of a migration tool for DynamoDB tables (API v2) between AWS accounts.

Usage
-----

Parameters needed:
- AWS Access Key of your original AWS Account
- AWS Secret Key of your original AWS Account
- AWS Access Key of your destination AWS Account
- AWS Secret Key of your destination AWS Account
- a table prefix to select the tables to migrate 

Enter both access and secret Key for each your original AWS account and destination AWS account of the migration into the parameters section of the scala file https://github.com/mugglmenzel/dynamodbmigrator/blob/master/src/main/scala/DynamoDBMigrator.scala.
Also, enter a table prefix to be considered. In case a table prefix absent for your setup enter full table name and execute the script for each table.

Compile and execute the scala file (http://www.scala-lang.org/node/166).


*Warning: The tool uses your AWS resources and AWS bills your for the usage of the DynamoDB service.*
