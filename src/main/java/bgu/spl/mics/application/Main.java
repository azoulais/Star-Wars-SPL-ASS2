package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;

import com.google.gson.Gson;

import java.io.*;
import java.util.concurrent.CountDownLatch;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
    public static CountDownLatch latch = new CountDownLatch(2);

    public static void main(String[] args) throws InterruptedException {

        //read input
        Input input = null;
        Gson gson = new Gson();
        try (Reader reader = new FileReader(args[0])) {
            input = gson.fromJson(reader, Input.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //construct passive+ microservices
        Diary diary = Diary.getInstance();
        Ewoks.getInstance().setEwoksList(input.getEwoks());

        Thread leia = new Thread(new LeiaMicroservice(input.getAttacks()));
        Thread r2d2 = new Thread(new R2D2Microservice(input.getR2D2()));
        Thread lando = new Thread(new LandoMicroservice(input.getLando()));
        Thread hans = new Thread(new HanSoloMicroservice());
        Thread c3po = new Thread(new C3POMicroservice());


        //simulate
        hans.start();
        c3po.start();
        leia.start();
        r2d2.start();
        lando.start();

        //wait for threads termination
        try {
            leia.join();
            hans.join();
            c3po.join();
            r2d2.join();
            lando.join();
        } catch (InterruptedException ex) {
            System.out.println("still working");
        }

        //create output
        try (Writer writer = new FileWriter(args[1])) {
            gson.toJson(diary, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
