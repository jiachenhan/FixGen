class PlaceHold{
public void addLineSwitch(int lineNumber, int switchNumber, int min, int max) {
    if (lineData != null) {
        lineData.addSwitch(switchNumber, min, max);
        this.branches.put(lineData);
    }
    try {
        LineData lineData = getLineData(lineNumber);
    } finally {
        lock.unlock();
    }
    lock.lock();
    try {
        LineData  = getLineData(lineNumber);
        if ( != null) {
            .addSwitch(switchNumber, );
            this.branches.put(Integer.valueOf(lineNumber));
        }
    } finally {
        unlock();
    }
    lock.lock();
}
}