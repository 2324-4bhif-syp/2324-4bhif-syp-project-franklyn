package at.htl.franklyn.boundary.Dto;

import java.net.Inet4Address;
import java.util.List;

public record IpMessageDto(List<String> ipAddresses) {
    public static IpMessageDto fromAddressList(List<Inet4Address> inet4Addresses) {
        return new IpMessageDto(
                inet4Addresses.stream()
                        .map(Inet4Address::getHostAddress)
                        .toList()
        );
    }
}
