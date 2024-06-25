# Play Java Pekko Cluster Example

This example demonstrates how to setup the Play-provided `ActorSystem` to build a [cluster](https://pekko.apache.org/docs/pekko/current/typed/cluster.html).

The example is a very simple Play application with a counter in an [actor](app/services/CounterActor.java).

When creating a counter in a system with multiple nodes running we have two options:

1. Keep the count on a database and block on each concurrent access
2. Keep the count in memory with eventual copies on the database and have that memory copy be a persistent singleton across our cluster.

This application demonstrates how to use an [Pekko Cluster Singleton](https://pekko.apache.org/docs/pekko/current/typed/cluster-singleton.html#example) in
 Play to implement the counter.
 
To turn a non-clustered Play application to a clustered one you need to do the following:

1. include the dependency to `"org.apache.pekko" %% "pekko-cluster-typed" % PlayVersion.pekkoVersion` (see [build.sbt](build.sbt)).
2. use one of the available methods to form a cluster [provided by Pekko Cluster](https://pekko.apache.org/docs/pekko/current/typed/cluster.html#joining).

### Considerations for a clustered Play application in Dev Mode

But when you try to run a clustered Play application in Dev Mode there is a problem. In most common cases, clustering an application should require
 at least three nodes. So if so many nodes are required, how can a clustered Play application run in Dev Mode if there's only one node? The answer
  is "forming a one-node cluster". In Dev Mode, then, we will use a special mechanism to join the cluster in which the Application's `ActorSystem
  ` joins itself.
  
See the source code in [`modules/AppModule.java`](modules/AppModule.java) for the logic controlling how the `ActorSystem` of anode participates on
 the cluster formation. This example application demonstrates:
 
1. the [Pekko Cluster API](https://pekko.apache.org/docs/pekko/current/typed/cluster.html#joining-programmatically-to-seed-nodes) for self-joining in Dev Mode, and 
2. the setup of [`seed-node` in configuration](https://pekko.apache.org/docs/pekko/current/typed/cluster.html#joining-configured-seed-nodes) for Prod Mode
 
but it doesn't demonstrate the [`Pekko Cluster Bootstrap`](https://pekko.apache.org/docs/pekko/current/typed/cluster.html#joining-automatically-to-seed-nodes-with-cluster-bootstrap)

## Server backend

By default, the project uses the Pekko HTTP Server backend. To switch to the Netty Server backend, enable the `PlayNettyServer` sbt plugin in the `build.sbt` file.
In the `build.sbt` of this project, you'll find a commented line for this setting; simply uncomment it to make the switch.
For more detailed information, refer to the Play Framework [documentation](https://www.playframework.com/documentation/3.0.x/Server).

## Running this sample in Dev Mode

To run this sample in `Dev Mode` use the regular 

```bash
sbt run
```
or
```bash
./gradlew playRun
```

and open the URL http://localhost:9000/.

## Running this sample in Production Mode

You can run this sample in `Production Mode` on your local machine. This section will guide you on starting 3 different nodes in your local machine
 and forming a cluster between them. The following steps will package the application and prepare folder with necessary artifacts for the execution: 
 
1. open a terminal and change directory to the `play-java-pekko-cluster-example` folder.
2. package the application using `sbt dist` or `./gradlew distZip` from the 
3. locate the file `play-java-pekko-cluster-example/target/universal/play-java-pekko-cluster-example-1.0-SNAPSHOT.zip` or `build/distributions/play-java-pekko-cluster-example.zip`, copy it to a folder of your
 choice and `unzip` it.

> NOTE: This sample application ships with extra config files that make it very easy to run multiple nodes on a single machine. If you want to
> deploy a Play application with Pekko Cluster you should refer to the Pekko and Play documentation for extra considerations regarding port biding
>, secret management, [cluster bootstrapping[(https://pekko.apache.org/docs/pekko/current/typed/cluster.html#joining-automatically-to-seed-nodes-with-cluster-bootstrap), etc...


Open three separate terminals on the folder where you unzipped the file and run the following commands (one on each terminal):
 
```bash
bin/play-java-pekko-cluster-example  -Dconfig.file=local1.conf
```
or for Gradle distribution
```bash
JAVA_OPTS="-Dconfig.resource=local1.conf -Dplay.http.secret.key=some-long-key-that-will-be-used-by-your-application" ./bin/play-java-pekko-cluster-example
```

```bash
bin/play-java-pekko-cluster-example  -Dconfig.file=local2.conf
```
or for Gradle distribution
```bash
JAVA_OPTS="-Dconfig.resource=local2.conf -Dplay.http.secret.key=some-long-key-that-will-be-used-by-your-application" ./bin/play-java-pekko-cluster-example
```


```bash
bin/play-java-pekko-cluster-example  -Dconfig.file=local3.conf
```
or for Gradle distribution
```bash
JAVA_OPTS="-Dconfig.resource=local3.conf -Dplay.http.secret.key=some-long-key-that-will-be-used-by-your-application" ./bin/play-java-pekko-cluster-example
```

In the terminals you should see activity like:


```
2020-09-01 11:40:45 INFO  org.apache.pekko.cluster.Cluster Cluster(pekko://application) Cluster Node [pekko://application@127.0.0.1:63551] - Node [pekko://application@127.0.0.1:63551] is JOINING itself (with roles [dc-default]) and forming new cluster
2020-09-01 11:40:45 INFO  org.apache.pekko.cluster.Cluster Cluster(pekko://application) Cluster Node [pekko://application@127.0.0.1:63551] - is the new leader among reachable nodes (more leaders may exist)
2020-09-01 11:40:45 INFO  org.apache.pekko.cluster.Cluster Cluster(pekko://application) Cluster Node [pekko://application@127.0.0.1:63551] - Leader is moving node [pekko://application@127.0.0.1:63551] to [Up]
```

The logs above indicate node 1 (identified as `pekko://application@127.0.0.1:63551`) and node 2 (identified as `pekko://application@127.0.0.1:63552
`) have seen each other and established a connection, then `pekko://application@127.0.0.1:63551` became the leader and that leader decided to mark
 `pekko://application@127.0.0.1:63551`'s status as `Up`. Finally, the singleton `counter-actor` that we use on this sample app is available. You
  should also see, in the logs, how the node 3 (identified as `pekko://application@127.0.0.1:63553`) also joins the cluster.

Finally, open three browser tabs to the URLs http://localhost:9001/, http://localhost:9002/, and http://localhost:9003/ (each points to a different
 Play instance) and interact with the UI. Note how all three increment a single counter in the singleton.

