# stagediver.fx IEW Database - Database bundle to the stagediver.fx RCP platform
This project adds the ability to work with databases in your stagediver.fx projects. It is based on the [stagediver.fx](https://github.com/qaware/stagediver.fx) project.

## Introduction
This project introduces the additional features to the stagediver.fx platform:

- Connection service to manage connections to different database systems
- Build in [HyperSQL](http://hsqldb.org/) for rapid development
- Connection pooling with [c3p0](http://www.mchange.com/projects/c3p0/)

The project can be deployed as a bundle into your stagediver.fx application.

Please note this is a very simple project. It's initial intention is to learn OSGi, learn how to deploy additional bundles into a stagediver.fx project and to sum up my knowledge about non Spring projects :-)

## How to use

The following assumes you have used the stagediver.fx archetype to create your application.

- Github checkout
- mvn clean install
- Add the following to the top level **pom.xml** of your stagediver.fx application. Please change the version to the one you want to use.

             <dependency>
                 <groupId>de.iew.stagediver.fx</groupId>
                 <artifactId>database</artifactId>
                 <version>0.1.0-SNAPSHOT</version>
                 <type>pom</type>
                 <scope>provided</scope>
             </dependency>
             <dependency>
                 <groupId>de.iew.stagediver.fx</groupId>
                 <artifactId>database-api</artifactId>
                 <version>0.1.0-SNAPSHOT</version>
             </dependency>
             <dependency>
                 <groupId>de.iew.stagediver.fx</groupId>
                 <artifactId>database-core</artifactId>
                 <version>0.1.0-SNAPSHOT</version>
             </dependency>

- Add the following to the <code>dependencies</code> section of the modules which want to use the database module. It adds the **database-api** module as a compile time dependency for development. The dependency is not shipped with your module.

            <dependency>
                <groupId>de.iew.stagediver.fx</groupId>
                <artifactId>database-api</artifactId>
                <scope>provided</scope>
            </dependency>

- In your <code>maven-bundle-plugin</code> configuration of the modules add the import <code>de.iew.stagediver.fx.database.*</code> to the <code>Import-Package</code> configuration. It should look similar to:

            <Import-Package>
                ... Many other includes and excludes of the module,
                de.iew.stagediver.fx.database.*
            </Import-Package>

- Add the following to your stagediver.fx main application module. Normally this module should have the **-application** suffix.

        <dependency>
            <groupId>de.iew.stagediver.fx</groupId>
            <artifactId>database-full</artifactId>
            <version>0.1.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>provided</scope>
        </dependency>

## License
This project is licensed under the terms of the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) license.