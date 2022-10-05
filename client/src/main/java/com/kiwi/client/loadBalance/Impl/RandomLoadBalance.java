package com.kiwi.client.loadBalance.Impl;

import com.kiwi.client.loadBalance.LoadBalance;
import model.Service;

import java.util.List;
import java.util.Random;

@annotation.LoadBalance("Random")
public class RandomLoadBalance implements LoadBalance {

    private static Random random = new Random();

    @Override
    public Service route(List<Service> services) {
        return services.get(random.nextInt(services.size()));
    }
}
