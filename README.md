Little project exposing an issue while creating a class extending one from the JDK.

# Run the tests

Use JDK 11 and then to  `./mvnw test`.

# Description

The tests are creating a proxy of a class using ByteBuddy (generate the extending class) and Objenesis (instantiate it without any constructor).
* `StandardTest` is just a normal test
* `PowerMockTest` is using a PowerMock running which plays with class loader to allow bytecode instrumentation

Then we are trying to mock `Timestamp` and `JTable`.
Both classes are from the JDK but one is in the `java` package and the other in the `javax` package.

And we try permutations of these two things:
* The proxy is created in the package of the original class or in `pro.tremblay.accessdenied.internal` package
* The proxy is created in the class loader of the original class or in the `ClassFactory` class loader

Results are

| Type       | Class      | Same Package | Same Class loader | Result                                                                                                                                     |
|------------|------------|--------------|-------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| Standard   | JTable     | Yes          | Yes               | It works                                                                                                                                   |
| Standard   | JTable     | Yes          | No                | It works                                                                                                                                   |
| Standard   | JTable     | No           | Yes               | It works                                                                                                                                   |
| Standard   | JTable     | No           | No                | It works                                                                                                                                   |
| Standard   | Timestamp  | Yes          | Yes               | It works                                                                                                                                   |
| Standard   | Timestamp  | Yes          | No                | SecurityException: [...]] 'app' tried to load prohibited package name: java.sql                                                            |
| Standard   | Timestamp  | No           | Yes               | It works                                                                                                                                   |
| Standard   | Timestamp  | No           | No                | It works                                                                                                                                   |
| PowerMock  | JTable     | Yes          | Yes               | LinkageError: loader constraint violation in interface itable initialization for class [...]                                               |
| PowerMock  | JTable     | Yes          | No                | NoClassDefFoundError: Could not initialize class javax.swing.JTable$$$MyThing                                                              |
| PowerMock  | JTable     | No           | Yes               | NoClassDefFoundError: Could not initialize class javax.swing.JTable                                                                        |
| PowerMock  | JTable     | No           | No                | NoClassDefFoundError: Could not initialize class pro.tremblay.accessdenied.internal.JTable$$$MyThing                                       |
| PowerMock  | Timestamp  | Yes          | Yes               | It works                                                                                                                                   |
| PowerMock  | Timestamp  | Yes          | No                | SecurityException: [...] org.powermock.core.classloader.javassist.JavassistMockClassLoader tried to load prohibited package name: java.sql |
| PowerMock  | Timestamp  | No           | Yes               | It works                                                                                                                                   |
| PowerMock  | Timestamp  | No           | No                | It works                                                                                                                                   |

What we want is to select the right 