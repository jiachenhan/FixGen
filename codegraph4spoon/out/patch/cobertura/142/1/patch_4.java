class SummaryXMLReport {
  public SummaryXMLReport(
      ProjectData projectData,
      File destinationDir,
      FileFinder finder,
      ComplexityCalculator complexity)
      throws IOException {
    File file = new File(destinationDir, "coverage-summary.xml");
    try {
      printHeader();
      double ccn = complexity.getCCNForProject(projectData);
      int numLinesCovered = projectData.getNumberOfCoveredLines();
      int numLinesValid = projectData.getNumberOfValidLines();
      int numBranchesCovered = projectData.getNumberOfCoveredBranches();
      int numBranchesValid = projectData.getNumberOfValidBranches();
      printCoverageElement(projectData, complexity);
      increaseIndentation();
      println("<packages />");
      decreaseIndentation();
      println("</coverage>");
      setPrintWriter(IOUtil.getPrintWriter(file));
    } finally {
      pw.close();
    }
  }
}
