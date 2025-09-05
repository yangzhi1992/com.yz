package com.commons.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.HTTPServer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PrometheusDemo {

    public static void main(String[] args) {
        try {
            System.out.println("start...");
            new HTTPServer(1234);
            submitData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void submitData() throws InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Counter requests = Counter.build()
                .name("requests_total_guoyx")
                .help("测试")
                .register();

        int max = 20;
        int min = 1;
        Random random = new Random();

        while (true) {
            TimeUnit.SECONDS.sleep(5);
            int count = random.nextInt(max) % (max - min + 1) + min;
            requests.inc(count);
            System.out.println(sdf.format(new Date()) + "increase count, current number:" + requests.get());
        }
    }
}