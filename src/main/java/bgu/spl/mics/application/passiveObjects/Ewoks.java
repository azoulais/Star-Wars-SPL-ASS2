package bgu.spl.mics.application.passiveObjects;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Passive object representing the resource manager of Ewok objects.
 * Implemented as a thread safe singleton.
 */

public class Ewoks {
    private ConcurrentHashMap<Integer, Ewok> ewoksList;

    private static class SingletonHolder {
        private static Ewoks instance = new Ewoks();
    }

    private Ewoks() {
    }

    public static Ewoks getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * This method creates the list of Ewoks that available to acquire.
     *
     * @param numOfEwoks The number of Ewoks that will be available.
     */
    public void setEwoksList(int numOfEwoks) {
        ewoksList = new ConcurrentHashMap<>();
        for (int i = 1; i <= numOfEwoks; i++)
            ewoksList.put(i, new Ewok(i));
    }

    /**
     * Using this method a Microservice(thread) can acquire the Ewoks it needs.
     * This method is blocking meaning that if the Ewoks requested are not available,
     * the microservice should wait until they become available.
     *
     * @param serials The serials of the Ewoks required for the attack.
     */
    public void aquireEwoks(List<Integer> serials) {
        Collections.sort(serials);
        Iterator<Integer> iterator = serials.iterator();
        while (iterator.hasNext()) {
            Integer ewokSerial = iterator.next();
            Ewok nextRequired = ewoksList.get(ewokSerial);
            synchronized (nextRequired) {
                while (!nextRequired.isAvailable()) {
                    try {
                        nextRequired.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                nextRequired.acquire();
            }
        }
    }

    /**
     * Using this method a microservice releases the Ewoks he acquired for the attack,
     * and notifies the waiting microservices for each Ewok.
     *
     * @param serials The serials of the Ewoks to release.
     */
    public void releaseEwoks(List<Integer> serials) {
        Collections.sort(serials);
        Iterator<Integer> iterator = serials.iterator();
        while (iterator.hasNext()) {
            Integer ewokSerial = iterator.next();
            Ewok nextRelease = ewoksList.get(ewokSerial);
            synchronized (nextRelease) {
                nextRelease.release();
                nextRelease.notifyAll();
            }
        }
    }
}
