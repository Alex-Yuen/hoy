

package mobi.samov.client.core;

import java.util.Vector;

public class Observable {
    private boolean changed = false;
    private Vector obs;
   
    /** Construct an Observable with zero Observers. */

    public Observable() {
	obs = new Vector();
    }

    public synchronized void addObserver(Observer o) {
        if (o == null)
            throw new NullPointerException();
	if (!obs.contains(o)) {
	    obs.addElement(o);
	}
    }

    public synchronized void deleteObserver(Observer o) {
        obs.removeElement(o);
    }

    public void notifyObservers() {
	notifyObservers(null);
    }

    public void notifyObservers(Object arg) {

        Object[] arrLocal;

	synchronized (this) {

	    if (!changed)
                return;
            //arrLocal = obs.toArray();
	    	/**
	    	 * Edited by hoyzhang
	    	 */
	    	arrLocal = new Object[obs.size()];
	    	obs.copyInto(arrLocal);
            clearChanged();
        }
        for (int i = arrLocal.length-1; i>=0; i--)
        {
            ((Observer)arrLocal[i]).update(this, arg);
        }
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteObservers() {
	obs.removeAllElements();
    }

    protected synchronized void setChanged() {
	changed = true;
    }

    protected synchronized void clearChanged() {
	changed = false;
    }


    public synchronized boolean hasChanged() {
	return changed;
    }

    public synchronized int countObservers() {
	return obs.size();
    }
}
