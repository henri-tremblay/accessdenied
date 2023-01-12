package pro.tremblay.accessdenied;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import javax.swing.BoxLayout;
import java.sql.Timestamp;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;

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
    public void boxLayoutSamePackageSameClassLoader() {
        test(BoxLayout.class, ClassFactory::samePackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void boxLayoutSamePackageThisClassLoader() {
        test(BoxLayout.class, ClassFactory::samePackage, ClassFactory::thisClassLoader);
    }

    @Test
    public void boxLayoutThisPackageSameClassLoader() {
        test(BoxLayout.class, ClassFactory::thisPackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void boxLayoutThisPackageThisClassLoader() {
        test(BoxLayout.class, ClassFactory::thisPackage, ClassFactory::thisClassLoader);
    }

    @Test
    public void appSamePackageSameClassLoader() {
        test(App.class, ClassFactory::samePackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void appSamePackageThisClassLoader() {
        test(App.class, ClassFactory::samePackage, ClassFactory::thisClassLoader);
    }

    @Test
    public void appThisPackageSameClassLoader() {
        test(App.class, ClassFactory::thisPackage, ClassFactory::sameClassLoader);
    }

    @Test
    public void appThisPackageThisClassLoader() {
        test(App.class, ClassFactory::thisPackage, ClassFactory::thisClassLoader);
    }

    private void test(Class<?> clazz, Function<Class<?>, String> classNameProvider, Function<Class<?>, ClassLoader> classLoaderProvider) {
        System.out.println("============== Running " + name.getMethodName() + " ==============");
        System.out.println("Class loader: " + clazz.getClassLoader());
        System.out.println("Module: " + clazz.getModule());
        System.out.println("Layer: " + clazz.getModule().getLayer());
        ClassFactory factory = new ClassFactory(classNameProvider, classLoaderProvider);
        Object result = factory.wrap(clazz);
        assertNotNull(result);
    }

}
