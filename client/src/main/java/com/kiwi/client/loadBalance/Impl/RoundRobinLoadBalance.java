package com.kiwi.client.loadBalance.Impl;

import com.kiwi.client.loadBalance.LoadBalance;
import model.Service;

import java.util.List;

@annotation.LoadBalance("RoundRobin")
public class RoundRobinLoadBalance implements LoadBalance {


    @Override
    public Service route(List<Service> services) {
        return null;
    }
}
