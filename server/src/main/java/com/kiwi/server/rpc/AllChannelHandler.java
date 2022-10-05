package com.kiwi.server.rpc;


import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;


@Component
public class AllChannelHandler {

    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(
                    4,
                    20,
                    10l,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(),
                    new CustomizableThreadFactory("rpc-server-handler"),
                    new ThreadPoolExecutor.DiscardPolicy()
            );


    public static void channelRead(Runnable r) {
        executor.execute(r);
    }


    @PreDestroy
    public static void shutDown() {
        executor.shutdown();
    }

}
