Little project exposing an issue while creating a class extending one from the JDK.

# Run the tests

Use JDK 11 and then to  `./mvnw test`.

# Description

The tests are creating a proxy of a class using ByteBuddy (generate the extending class) and Objenesis (instantiate it without any constructor).
* `StandardTest` is just a normal test
* `PowerMockTest` is using a PowerMock running which plays with class loader to allow bytecode instrumentation

Then we are trying to mock `Timestamp` and `BoxLayout`.
Both classes are from the JDK but one is in the `java` package and the other in the `javax` package.

And we try permutations of these two things:
* The proxy is created in the package of the original class or in `pro.tremblay.accessdenied.internal` package
* The proxy is created in the class loader of the original class or in the `ClassFactory` class loader

Results are

| Type       | Class     | Same Package | Same Class loader | Result                                                                          |
|------------|-----------|--------------|-------------------|---------------------------------------------------------------------------------|
| Standard   | BoxLayout | Yes          | Yes               | It works                                                                        |
| Standard   | BoxLayout | Yes          | No                | It works                                                                        |
| Standard   | BoxLayout | No           | Yes               | It works                                                                        |
| Standard   | BoxLayout | No           | No                | It works                                                                        |
| Standard   | Timestamp | Yes          | Yes               | It works                                                                        |
| Standard   | Timestamp | Yes          | No                | SecurityException: [...]] 'app' tried to load prohibited package name: java.sql |
| Standard   | Timestamp | No           | Yes               | It works                                                                        |
| Standard   | Timestamp | No           | No                | It works                                                                        |
| PowerMock  | BoxLayout | Yes          | Yes               | It works                                                                        |
| PowerMock  | BoxLayout | Yes          | No                | It works                                                                        |
| PowerMock  | BoxLayout | No           | Yes               | It works                                                                        |
| PowerMock  | BoxLayout | No           | No                | It works                                                                        |
| PowerMock  | Timestamp | Yes          | Yes               | It works                                                                        |
| PowerMock  | Timestamp | Yes          | No                | It works                                                                        |
| PowerMock  | Timestamp | No           | Yes               | It works                                                                        |
| PowerMock  | Timestamp | No           | No                | It works                                                                        |

What we want is to select the right way to instantiate these classes.
Plus other normal classes like `App.

It feels that we need to kno