package com.kiwi.client.loadBalance;

import model.Service;

import java.util.List;

public interface LoadBalance {

    Service route(List<Service> services);
}
