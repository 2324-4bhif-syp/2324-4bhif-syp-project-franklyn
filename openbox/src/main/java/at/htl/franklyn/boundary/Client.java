package at.htl.franklyn.boundary;

import at.htl.franklyn.boundary.Dto.IpMessageDto;
import at.htl.franklyn.services.ConnectionService;
import at.htl.franklyn.services.IpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

@ClientEndpoint
public class Client {
    @Inject
    ConnectionService connectionService;

    @Inject
    IpService ipService;

    @OnOpen
    public void onOpen(Session session) throws JsonProcessingException {
        List<Inet4Address> address = ipService.getAllIp4Addresses();
        ObjectMapper om = new ObjectMapper();

        if(!address.isEmpty()) {
            session.getAsyncRemote().sendText(
                    om.writeValueAsString(IpMessageDto.fromAddressList(address))
            );
            connectionService.setConnected(true);
        } else {
            Log.error("Could not retrieve local ip addresses!");
        }
    }

    @OnClose
    public void onClose() throws IOException {
        connectionService.setConnected(false);
        connectionService.getSession().close();
    }
}
