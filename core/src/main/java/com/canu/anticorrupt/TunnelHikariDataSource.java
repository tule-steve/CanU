package com.canu.anticorrupt;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

public class TunnelHikariDataSource extends HikariDataSource implements InitializingBean {
    private boolean createTunnel = true;

    @Value("${ssh.tunnel.url:52.78.241.146}")
    private String url;

    @Value("${ssh.tunnel.username:ubuntu}")
    private String username;

    @Value("${ssh.tunnel.password:ubuntu}")
    private String password;

    @Value("${ssh.tunnel.port:22}")
    private int port;

    private Session session;

    private int tunnelPort = 3306;

    public void afterPropertiesSet() {
        if(createTunnel) {
            // 1. Extract remote host name from the JDBC URL.
            // 2. Extract/infer remote tunnel port (e.g. 3306)
            // from the JDBC URL.
            // 3. Create a tunnel using Jsch and sample code
            // at http://www.jcraft.com/jsch/examples/PortForwardingL.java.html
            JSch jsch = new JSch();
            try {
                jsch.addIdentity("/Users/yamada/.ssh/canu.pem");
                session = jsch.getSession(username, url, port);
                session.setPassword(password);

                java.util.Properties config = new java.util.Properties();
                // Never automatically add new host keys to the host file
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                // Connect to remote server
                session.connect();

                // Apply the port forwarding
                session.setPortForwardingL(tunnelPort, "localhost", tunnelPort);
            } catch (JSchException e) {
                e.printStackTrace();
            }

        }
    }


}
