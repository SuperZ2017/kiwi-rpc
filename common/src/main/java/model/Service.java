package model;

import lombok.Data;
import lombok.ToString;

/**
 * 服务信息
 */
@Data
@ToString
public class Service {

    /**
     * 服务名称
     */
    private String name;


    private String host;


    private Integer port;
}
