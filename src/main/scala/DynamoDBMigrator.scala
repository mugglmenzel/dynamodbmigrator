import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.dynamodbv2.model._
import scala.collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: mugglmenzel
 * Date: 04.07.13
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
object DynamoDBMigrator extends App {

  /*
    Parameters
  */
  val sourceAccessKey = "AWSAccessKey"
  val sourceSecretKey = "AWSSecretKey"

  val targetAccessKey = "AWSAccessKey"
  val targetSecretKey = "AWSSecretKey"

  val tablesPrefix = "prefix-"


  val sourcedynamodbclient = new AmazonDynamoDBClient(new BasicAWSCredentials(sourceAccessKey, sourceSecretKey))
  val targetdynamodbclient = new AmazonDynamoDBClient(new BasicAWSCredentials(targetAccessKey, targetSecretKey))


  /*
    Processing
   */

  println("processing tables with prefix \"" + tablesPrefix + "\".")
  sourcedynamodbclient.listTables().getTableNames.toList.filter(tableName => tableName.startsWith(tablesPrefix)).map(tableName => new Thread(new Runnable {
    def run() {

      val originalTable = sourcedynamodbclient.describeTable(new DescribeTableRequest().withTableName(tableName)).getTable

      try {
        println("table exists? " + (targetdynamodbclient.describeTable(new DescribeTableRequest().withTableName(tableName)).getTable.getTableStatus != ""))
      } catch {
        case e: ResourceNotFoundException => {
          print("table " + tableName + " does not yet exist. creating...")
          val req = new CreateTableRequest().withTableName(tableName).withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(originalTable.getProvisionedThroughput.getReadCapacityUnits).withWriteCapacityUnits(originalTable.getProvisionedThroughput.getWriteCapacityUnits)).withKeySchema(originalTable.getKeySchema).withAttributeDefinitions(originalTable.getAttributeDefinitions)
          if (originalTable.getLocalSecondaryIndexes != null) req.withLocalSecondaryIndexes(asJavaCollection(originalTable.getLocalSecondaryIndexes.toList.map(descr => new LocalSecondaryIndex().withIndexName(descr.getIndexName).withKeySchema(descr.getKeySchema).withProjection(descr.getProjection))))

          targetdynamodbclient.createTable(req)
          println("created table " + tableName + ".")
        }
      }
      println("checking status of table " + tableName + "...")
      while (targetdynamodbclient.describeTable(new DescribeTableRequest().withTableName(tableName)).getTable.getTableStatus != "ACTIVE") {
        Thread.sleep(5000)
        println("status of " + tableName + ": " + targetdynamodbclient.describeTable(new DescribeTableRequest().withTableName(tableName)).getTable.getTableStatus)
      }

      val result = sourcedynamodbclient.scan(new ScanRequest().withTableName(tableName))
      println("transferring " + result.getCount + " data items for table " + tableName + "...")
      result.getItems.toList.view.zipWithIndex.foreach{ case (item, i) => {
        if(i % 100 == 0)  println("transferred " + i + " items to " + tableName)
        targetdynamodbclient.putItem(new PutItemRequest().withTableName(tableName).withItem(item))
      }}
      println("finished transferring data for table " + tableName + ".")

    }
  })).foreach(thread => {
    thread.start()
    //optional timeout: Thread.sleep(10000)
  })


}
