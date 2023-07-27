class PlaceHold{
public void testRelaxedJavadoc() throws Exception {
    checkConfig.setClassname(JavadocMethodCheck.class.getName());
    .addAttribute(null, PROTECTED.getName());
    final Checker c = createChecker(checkConfig);
    final String fname = getPath("InputPublicOnly.java");
    final String[] expected = new String[]{ "59:5: Missing a Javadoc comment.", "64:5: Missing a Javadoc comment.", "79:5: Missing a Javadoc comment.", "84:5: Missing a Javadoc comment." };
    verify(c, fname, expected);
    DefaultConfiguration  = createCheckConfig(.);
}
}