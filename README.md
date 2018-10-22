# ShopWatchj

Java monitoring bot for Shopify.  Create your own notification plugins using the WatchListener interface or use the supplied plugins.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

The pom file contains all dependencies required to run.  The bot relies on the Jackson and Unirest libraries below:

```
	<dependencies>
		<dependency>
			<groupId>com.mashape.unirest</groupId>
			<artifactId>unirest-java</artifactId>
			<version>1.4.9</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-core</artifactId>
			<version>2.9.7</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.9.7</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-annotations</artifactId>
			<version>2.9.7</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.module</groupId>
			<artifactId>jackson-module-parameter-names</artifactId>
			<version>2.9.7</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.datatype</groupId>
			<artifactId>jackson-datatype-jdk8</artifactId>
			<version>2.9.7</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.datatype</groupId>
			<artifactId>jackson-datatype-jsr310</artifactId>
			<version>2.9.7</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.dataformat</groupId>
			<artifactId>jackson-dataformat-xml</artifactId>
			<version>2.9.7</version>
		</dependency>
	</dependencies>
```

### Installing

Simply build the binaries using mvn clean install.  Distribution files will be created in the ../target directory

```
mvn clean install
```

## Deployment

Run the bot using java -jar option from command line or via shell script

```
java -jar shopwatchj.jar
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## License

This project is licensed under the GPL-3.0 License - see the [LICENSE](LICENSE) file for details
