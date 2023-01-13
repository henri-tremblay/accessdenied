Little project exposing an issue while creating a class extending one from the JDK.

# Run the tests

Use JDK 11 and then to  `./mvnw test`.

# Description

The tests are 
* Creating a proxy of a class by extending it using ByteBuddy
* Instantiate it without any constructor using Objenesis
* Set a public field that was added by ByteBuddy using a MethodHandle

We have 2 test flavors:
* `StandardTest` is just a normal test
* `PowerMockTest` is using a PowerMock running which plays with class loader to allow bytecode instrumentation

Then we are trying to mock 
* `Timestamp` from the `java` package in the JDK
* `BoxLayout` from the `javax` package in the JDK
* `Is` from another library (hamcrest)
* `App` from our code

And we try permutations of these two things:
* The proxy is created in the package of the original class or in `pro.tremblay.accessdenied.internal` package
* The proxy is created in the class loader of the original class or in the `ClassFactory` class loader

It works except

## Standard tests

* The field can't be set on `BoxLayout`. We get `java.lang.RuntimeException: java.lang.IllegalAccessException: no such field: javax.swing.BoxLayout$$$MyThing15.$callback/pro.tremblay.accessdenied.Data/putField` when staying in the original class loader
* The field can't be set on `Timestamp`. We get `java.lang.RuntimeException: java.lang.IllegalAccessException: no such field: java.sql.Timestamp$$$MyThing15.$callback/pro.tremblay.accessdenied.Data/putField` when staying in the original class loader
* The class `Timestamp` can't be created when keeping the package but moving to another class loader. We get `java.lang.IllegalStateException: java.lang.SecurityException: Class loader (instance of): 'app' tried to load prohibited package name: java.sql`

## PowerMock tests

* We can the no such field on `Is` when staying in the original class loader
* Same for `Timestamp`
* `Timestamp` also can't be created when keeping the package but moving to another class loader

## Remediation

The goal is to detect which class works with which usage.
The tests named `wiseXXX` are supposed to do that.
They should return the right class loader and package for a given class.
