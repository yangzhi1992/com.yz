package com.commons.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class DockerTool {

    private final static String QAE_APP_NAME = "QAE_APP_NAME";

    private final static String[] SYSTEM_APP_ID_KEYS = new String[]{"qae.app.id", "QAE_APP_ID"};

    private final static String[] SYSTEM_HOSTNAME_KEYS = new String[]{"dubbo.docker.hostname",
            "DUBBO_DOCKER_HOSTNAME", "HOST"};

    private final static String[] SYSTEM_IPV4_ADDRESS_KEY = new String[]{"dubbo.docker.ipv4.address",
            "DUBBO_DOCKER_IPV4_ADDRESS"};

    private DockerTool() {
    }

    private volatile static boolean ON_DOCKER = false;

    private final static Logger logger = LoggerFactory.getLogger(DockerTool.class);

    @Getter
    private static String qaeAppId;

    static {
        for (String key : SYSTEM_APP_ID_KEYS) {
            qaeAppId = System.getProperty(key, System.getenv(key));
            if (qaeAppId != null) {
                ON_DOCKER = true;
                logger.info("{} is set. (value = {})", key, qaeAppId);
                break;
            }
        }
        if (qaeAppId == null) {
            logger.info("Default value will be used. (value = normal)");
        }
    }

    public static Boolean isDocker() {
        return ON_DOCKER;
    }

    public static int getDockerMappedPort(int port) {
        if (System.getenv(QAE_APP_NAME) == null) {
            throw new IllegalArgumentException(QAE_APP_NAME + "环境变量缺失");
        }
        String environmentVariablePort = System.getenv("PORT_" + port);
        if (environmentVariablePort != null) {
            int mappedPort = Integer.parseInt(environmentVariablePort.trim());
            logger.info("environment variable PORT_{} is set. (value = {})", port, mappedPort);
            return mappedPort;
        } else {
            logger.warn("environment variable PORT_{} is not set. Please check docker port mapping configuration.",
                    port);
            return port;
        }
    }

    public static String getHostAddress() {
        String hostName = null;
        String dockerIPV4Address = null;
        for (String key : SYSTEM_HOSTNAME_KEYS) {
            hostName = System.getProperty(key, System.getenv(key));
            if (hostName != null) {
                logger.info("{} is set. (value = {})", key, hostName);
                break;
            }
        }
        if (hostName == null) {
            logger.warn("dubbo.docker.hostname, DUBBO_DOCKER_HOSTNAME and HOST are not set. Please check!");
        } else {
            hostName = hostName.trim();
        }

        try {
            dockerIPV4Address = InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            for (String key : SYSTEM_IPV4_ADDRESS_KEY) {
                dockerIPV4Address = System.getProperty(key, System.getenv(key));
                if (dockerIPV4Address != null) {
                    logger.info("{} is set. (value = {})", key, dockerIPV4Address);
                    break;
                }
            }
            if (dockerIPV4Address == null) {
                logger.info("Neither dubbo.docker.ipv4.address nor DUBBO_DOCKER_IPV4_ADDRESS is set. Please check!");
            }
        }
        try {
            if (dockerIPV4Address == null) {
                dockerIPV4Address = InetAddress.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException ex) {
            logger.warn("Could not get ip address. `127.0.0.1' will be used.", ex);
        }
        dockerIPV4Address = dockerIPV4Address == null ? "127.0.0.1" : dockerIPV4Address.trim();
        if (logger.isDebugEnabled()) {
            logger.debug("Dubbo ip address {} will be used", dockerIPV4Address);
        }
        return dockerIPV4Address;
    }
}
