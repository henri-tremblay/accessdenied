package pro.tremblay.accessdenied;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import javax.swing.JTable;
import java.sql.Timestamp;
import java.util.function.Function;

public abstract class BaseTest {

    @Rule
    public TestName name = new TestName();

    @Test
    public void timestampSamePackageSameClassLoader() {
        test(Timestamp.class, ClassFactory::samePackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void timestampSamePackageThisClassLoader() {
        test(Timestamp.class, ClassFactory::samePackage, ClassFactory::thisClassLoader);
    }

    @Test
    public void timestampThisPackageSameClassLoader() {
        test(Timestamp.class, ClassFactory::thisPackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void timestampThisPackageThisClassLoader() {
        test(Timestamp.class, ClassFactory::thisPackage, ClassFactory::thisClassLoader);
    }

    @Test
    public void jtableSamePackageSameClassLoader() {
        test(JTable.class, ClassFactory::samePackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void jtableSamePackageThisClassLoader() {
        test(JTable.class, ClassFactory::samePackage, ClassFactory::thisClassLoader);
    }

    @Test
    public void jtableThisPackageSameClassLoader() {
        test(JTable.class, ClassFactory::thisPackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void jtableThisPackageThisClassLoader() {
        test(JTable.class, ClassFactory::thisPackage, ClassFactory::thisClassLoader);
    }

    private void test(Class<?> clazz, Function<Class<?>, String> classNameProvider, Function<Class<?>, ClassLoader> classLoaderProvider) {
        System.out.println("============== Running " + name.getMethodName() + " ==============");
        ClassFactory factory = new ClassFactory(classNameProvider, classLoaderProvider);
        Object result = factory.wrap(clazz);
    }

}
