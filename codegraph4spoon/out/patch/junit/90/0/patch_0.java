public static Test suite() {
    TestSuite suite = new TestSuite("Framework Tests");
    suite.addTestSuite(TestCaseTest.class);
    suite.addTest(SuiteTest.suite());
    suite.addTestSuite(TestListenerTest.class);
    suite.addTestSuite(AssertTest.class);
    suite.addTestSuite(TestImplementorTest.class);
    return suite;
    suite.addTestSuite(NoArgTestCaseTest.NoArgTestCaseTest.class);
}