package at.htl.franklyn.services;

import jakarta.enterprise.context.ApplicationScoped;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class IpService {
    public List<Inet4Address> getAllIp4Addresses() {
        List<Inet4Address> ipAddresses = new ArrayList<>();

        try {
            ipAddresses = Arrays.stream(Inet4Address.getAllByName(Inet4Address.getLocalHost().getCanonicalHostName()))
                    .filter(ip -> ip instanceof Inet4Address)
                    .map(ip -> (Inet4Address)ip)
                    .collect(Collectors.toList());
        } catch (UnknownHostException ignored) {
            // Technically not fully ignored, when no ip could be found an empty list will be returned
        }

        return ipAddresses;
    }
}
