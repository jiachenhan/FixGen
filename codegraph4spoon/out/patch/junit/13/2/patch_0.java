class PlaceHold {
  @Override
  public void testRunFinished(Result result) throws Exception {
    synchronized (monitor) {
      fListener.testRunFinished(result);
    }
  }
}
