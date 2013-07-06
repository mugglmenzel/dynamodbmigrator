DynamoDB Migrator
=================

The DynamoDB Migrator tool is an elegant and quick implementation of a migration tool for DynamoDB tables (API v2) between AWS accounts.

Usage
-----

Enter both access and secret Key for each your original AWS account and destination AWS account of the migration into the parameters section of the scala file https://github.com/mugglmenzel/dynamodbmigrator/blob/master/src/main/scala/DynamoDBMigrator.scala.
Also, enter a table prefix to be considered. In case a table prefix absent for your setup enter full table name and execute the script for each table.

Warning: The tool uses your AWS resources and AWS bills your for the usage of the DynamoDB service.
