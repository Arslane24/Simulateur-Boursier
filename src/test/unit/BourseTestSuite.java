package test.unit;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestActif.class,
    TestEntreprise.class,
    TestPortfeuille.class,
    TestTransaction.class,
    TestActualite.class,
    TestBourse.class,
    TestGestionPortfeuille.class
})
public class BourseTestSuite {
}