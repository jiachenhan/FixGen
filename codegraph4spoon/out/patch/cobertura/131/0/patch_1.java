class PlaceHold {
  private String generateTableRowForClass(ClassData classData, String sourceFileName, double ccn) {
    StringBuffer ret = new StringBuffer();
    double branchCoverage = -1;
    if (classData.getNumberOfValidLines() > 0) {
      lineCoverage = classData.getLineCoverageRate();
    }
    if (classData.getNumberOfValidBranches() > 0) {
      branchCoverage = classData.getBranchCoverageRate();
    }
    ret.append("  <tr>");
    ret.append((((null + sourceFileName) + ".html\">") + classData.getBaseName()) + "</a></td>");
    ret.append(
        generateTableColumnsFromData(
            lineCoverage,
            classData.getNumberOfValidLines(),
            branchCoverage,
            classData.getNumberOfValidBranches(),
            ccn));
    ret.append("</tr>\n");
    return ret.toString();
  }
}
