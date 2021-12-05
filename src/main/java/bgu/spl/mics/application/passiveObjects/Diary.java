package bgu.spl.mics.application.passiveObjects;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private AtomicInteger totalAttacks;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long LandoTerminate;
    private long R2D2Terminate;

    private static class SingletonHolder {
        private static Diary instance = new Diary();
    }

    private Diary() {
        totalAttacks = new AtomicInteger(0);
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }


    public static Diary getInstance() {
        return SingletonHolder.instance;
    }

    public void settotalAttacks() {
        totalAttacks.incrementAndGet();
    }

    public int getTotalAttacks() {
        return totalAttacks.get();
    }

    public void setHanSoloFinish(long timeStampInMillis) {
        HanSoloFinish = timeStampInMillis;
    }

    public void setC3POFinish(long timeStampInMillis) {
        C3POFinish = timeStampInMillis;
    }

    public void setR2D2Deactivate(long timeStampInMillis) {
        R2D2Deactivate = timeStampInMillis;
    }

    public void setLeiaTerminate(long timeStampInMillis) {
        LeiaTerminate = timeStampInMillis;
    }

    public void setHanSoloTerminate(long timeStampInMillis) {
        HanSoloTerminate = timeStampInMillis;
    }

    public void setC3POTerminate(long timeStampInMillis) {
        C3POTerminate = timeStampInMillis;
    }

    public void setLandoTerminate(long timeStampInMillis) {
        LandoTerminate = timeStampInMillis;
    }

    public void setR2D2Terminate(long timeStampInMillis) {
        R2D2Terminate = timeStampInMillis;
    }


}
