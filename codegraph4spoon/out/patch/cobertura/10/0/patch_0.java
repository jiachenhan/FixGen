class PlaceHold{
public void execute() throws BuildException {
    CommandLineBuilder builder = null;
    try {
        builder = new CommandLineBuilder();
        if (dataFile != null) {
            builder.addArg("--datafile", dataFile);
        }
        if (toDir != null) {
            builder.addArg("--destination", toDir.getAbsolutePath());
        }
        for (int i = 0; i < ignoreRegexs.size(); i++) {
            Ignore ignoreRegex = ((Ignore) (ignoreRegexs.get(i)));
            builder.addArg("--ignore", ignoreRegex.getRegex());
            if () {
                builder.addArg(null);
            }
        }
        for (int i = 0; i < ignoreBranchesRegexs.size(); i++) {
            IgnoreBranches ignoreBranchesRegex = ((IgnoreBranches) (ignoreBranchesRegexs.get(i)));
            builder.addArg("--ignoreBranches", ignoreBranchesRegex.getRegex());
        }
        for (int i = 0; i < ignoreMethodAnnotations.size(); i++) {
            IgnoreMethodAnnotation ignoreMethodAnn = ((IgnoreMethodAnnotation) (ignoreMethodAnnotations.get(i)));
            builder.addArg("--ignoreMethodAnnotation", ignoreMethodAnn.getAnnotationName());
        }
        for (int i = 0; i < includeClassesRegexs.size(); i++) {
            IncludeClasses includeClassesRegex = ((IncludeClasses) (includeClassesRegexs.get(i)));
            builder.addArg("--includeClasses", includeClassesRegex.getRegex());
        }
        for (int i = 0; i < excludeClassesRegexs.size(); i++) {
            ExcludeClasses excludeClassesRegex = ((ExcludeClasses) (excludeClassesRegexs.get(i)));
            builder.addArg("--excludeClasses", excludeClassesRegex.getRegex());
        }
        if (ignoreTrivial) {
            builder.addArg("--ignoreTrivial");
        }
        if (failOnError) {
            builder.addArg("--failOnError");
        }
        if (instrumentationClasspath != null) {
            processInstrumentationClasspath();
        }
        createArgumentsForFilesets(builder);
        builder.saveArgs();
    } catch (IOException ioe) {
        getProject().log("Error creating commands file.", MSG_ERR);
        throw new BuildException("Unable to create the commands file.", ioe);
    }
    getJava().createArg().setValue("--commandsfile");
    getJava().createArg().setValue(builder.getCommandLineFile());
    if ((forkedJVMDebugPort != null) && (forkedJVMDebugPort.intValue() > 0)) {
        getJava().createJvmarg().setValue("-Xdebug");
        getJava().createJvmarg().setValue(("-Xrunjdwp:transport=dt_socket,address=" + forkedJVMDebugPort) + ",server=y,suspend=y");
    }
    AntUtil.transferCoberturaDataFileProperty(getJava());
    if (getJava().executeJava() != 0) {
        throw new BuildException("Error instrumenting classes. See messages above.");
    }
    builder.dispose();
}
}