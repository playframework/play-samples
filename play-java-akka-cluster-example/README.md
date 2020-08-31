# Play Java Akka Cluster Example

This example demonstrates how to setup the Play-provided `ActorSystem` to build a [cluster](https://doc.akka.io/docs/akka/current/typed/cluster
.html).

The example is a very simple Play application with a counter in an [actor](app/services/CounterActor.java).

When creating a counter in a system with multiple nodes running we have two options:

1. Keep the count on a database and block on each concurrent access
2. Keep the count in memory with eventual copies on the database and have that memory copy be a persistent singleton across our cluster.

This application demonstrates how to use an [Akka Cluster Singleton](https://doc.akka.io/docs/akka/current/typed/cluster-singleton.html#example) in
 Play to implement the counter.
 
To turn a non-clustered Play application to a clustered one you need to do the following:

1. include the dependency to `"com.typesafe.akka" %% "akka-cluster-typed" % PlayVersion.akkaVersion` (see [build.sbt](build.sbt)).
2. use one of the available methods to form a cluster [provided by Akka Cluster](https://doc.akka.io/docs/akka/current/typed/cluster.html#joining).

### Considerations for a clustered Play application in Dev Mode

But when you try to run a clustered Play application in Dev Mode there is a problem. In most common cases, clustering an application should require
 at least three nodes. So if so many nodes are required, how can a clustered Play application run in Dev Mode if there's only one node? The answer
  is "forming a one-node cluster". In Dev Mode, then, we will use a special mechanism to join the cluster in which the Application's `ActorSystem
  ` joins itself.
  
See the source code in [`modules/AppModule.java`](modules/AppModule.java) for the logic controlling how the `ActorSystem` of anode participates on
 the cluster formation. This example application demonstrates:
 
1. the [Akka Cluster API](https://doc.akka.io/docs/akka/current/typed/cluster.html#joining-programmatically-to-seed-nodes) for self-joining in Dev Mode, and 
2. the setup of [`seed-node` in configuration](https://doc.akka.io/docs/akka/current/typed/cluster.html#joining-configured-seed-nodes) for Prod Mode
 
but it doesn't demonstrate the [`Akka Cluster Bootstrap`](https://doc.akka.io/docs/akka/current/typed/cluster.html#joining-automatically-to-seed-nodes-with-cluster-bootstrap)

## Running this sample in Dev Mode

To run this sample in `Dev Mode` use the regular 

`sbt run` 

and open the URL http://localhost:9000/.

## Running this sample in Proc Mode

To run this sample in `Prod Mode` you need 
 
1. package the application using `sbt dist`
2. locate the file `target/universal/play-java-akka-cluster-example-1.0-SNAPSHOT.zip` and `unzip` it.

Then open three separate terminals on the folder where you unzipped the file and run the following commands (one on each terminal):

 
`bin/play-java-akka-cluster-example  -Dconfig.file=local1.conf`

`bin/play-java-akka-cluster-example  -Dconfig.file=local2.conf`

`bin/play-java-akka-cluster-example  -Dconfig.file=local3.conf`

Finally, open the URLs http://localhost:9000/, http://localhost:9001/, and http://localhost:9001/ (each points to a different Play instance).

