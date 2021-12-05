package bgu.spl.mics;

public class SimpleMicroService extends MicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public SimpleMicroService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        return;
    }

    @Override
    protected void close() {

    }
}
