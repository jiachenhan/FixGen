class PlaceHold {
  public void testNL() throws Exception {
    mCheckConfig.addAttribute("option", NL.toString());
    final String[] expected =
        new String[] {
          "49:14: '{' should be on a new line.",
          "53:14: '{' should be on a new line.",
          "58:18: '{' should be on a new line.",
          "62:18: '{' should be on a new line.",
          "67:12: '{' should be on a new line.",
          "72:18: '{' should be on a new line."
        };
    verify(getPath("InputScopeInnerInterfaces.java"), expected, mCheckConfig);
  }
}
