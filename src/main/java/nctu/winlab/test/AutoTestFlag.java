package nctu.winlab.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AutoTestFlag {
    private final Logger log = LoggerFactory.getLogger(getClass());

    enum PingType {
        ICMPing, ARPing, TCPing, UDPing, SCTPing
    }

    enum TreatmentType {
        TCPSrc, TCPDst, UDPSrc, UDPDst, SCTPSrc, SCTPDst, IPSrc, IPDst, ETHSrc, ETHDst, OUTPUT, VlanPush, VlanPop
    }

    protected PingType currentMatchFieldFlag = PingType.ICMPing;
    protected TreatmentType currentTreatmentFlag = TreatmentType.OUTPUT;

    protected Boolean flagsProxyARP = false;

    protected Boolean flagsMatchARP = false;
    protected Boolean flagsMatchIPv4 = false;
    protected Boolean flagsMatchVlan = false;

    protected Boolean flagsUseDummySrcPort = false;
    protected Boolean flagsUseDummyDstPort = false;
    protected Boolean flagsUseDummySrcMac = false;
    protected Boolean flagsUseDummyDstMac = false;
    protected Boolean flagsUseDummySrcIp = false;
    protected Boolean flagsUseDummyDstIp = false;

    protected Boolean flagsUseVlanSrc = false;
    protected Boolean flagsUseVlanDst = false;

    protected List<TreatmentType> treatmentFlags = new ArrayList<>();

    public AutoTestFlag() {
        reset();
    }

    // matchfield region
    public void reset() {
        treatmentFlags.clear();
        flagsProxyARP = false;

        flagsMatchARP = false;
        flagsMatchIPv4 = false;
        flagsMatchVlan = false;

        flagsUseDummySrcPort = false;
        flagsUseDummyDstPort = false;
        flagsUseDummySrcMac = false;
        flagsUseDummyDstMac = false;
        flagsUseDummySrcIp = false;
        flagsUseDummyDstIp = false;

        flagsUseVlanSrc = false;
        flagsUseVlanDst = false;

        proxy(false);
    }

    public PingType GetMatchFieldFlag() {
        return currentMatchFieldFlag;
    }

    public void SetMatchFieldFlag(PingType s) {
        currentMatchFieldFlag = s;
    }

    public void EnableProxyARP() {
        if (!flagsProxyARP) {
            proxy(true);
            flagsProxyARP = true;
        }
    }

    public void DisableProxyARP() {
        proxy(false);
    }

    public Boolean MatchIPV4() {
        if (!flagsMatchIPv4) {
            flagsMatchIPv4 = true;
            return false;
        }
        return true;
    }

    public Boolean MatchARP() {
        if (!flagsMatchARP) {
            flagsMatchARP = true;
            return false;
        }
        return true;
    }

    public Boolean MatchVlan() {
        if (!flagsMatchVlan) {
            flagsMatchVlan = true;
            flagsUseVlanSrc = true;
            return false;
        }
        return true;
    }

    private void proxy(Boolean action) {
        try {
            if (action) {
                Runtime.getRuntime().exec("onos-app localhost activate org.onosproject.proxyarp");
            } else {
                Runtime.getRuntime().exec("onos-app localhost deactivate org.onosproject.proxyarp");
            }
            Thread.sleep(2000);
        } catch (Exception e) {
        }
    }

    // treatment region
    public void SetTreatmentFlag(TreatmentType s) {
        if (!treatmentFlags.contains(s)) {
            treatmentFlags.add(s);
        }
    }

    public void SetUseDummySrcIp() {
        flagsUseDummySrcIp = true;
    }

    public void SetUseDummyDstIp() {
        flagsUseDummyDstIp = true;
    }

    public void SetUseDummySrcMac() {
        flagsUseDummySrcMac = true;
    }

    public void SetUseDummyDstMac() {
        flagsUseDummyDstMac = true;
    }

    public void SetUseDummySrcPort() {
        flagsUseDummySrcPort = true;
    }

    public void SetUseDummyDstPort() {
        flagsUseDummyDstPort = true;
    }

    public void SetUseVlanSrc() {
        flagsUseVlanSrc = true;
    }

    public void SetUseVlanDst() {
        flagsUseVlanDst = true;
    }

    public Boolean GetFlagsUseDummySrcIp() {
        return flagsUseDummySrcIp;
    }

    public Boolean GetFlagsUseDummyDstIp() {
        return flagsUseDummyDstIp;
    }

    public Boolean GetFlagsUseDummySrcMac() {
        return flagsUseDummySrcMac;
    }

    public Boolean GetFlagsUseDummyDstMac() {
        return flagsUseDummyDstMac;
    }

    public Boolean GetFlagsUseDummySrcPort() {
        return flagsUseDummySrcPort;
    }

    public Boolean GetFlagsUseDummyDstPort() {
        return flagsUseDummyDstPort;
    }

    public void ReverseArray() {
        Collections.reverse(treatmentFlags);
    }

    public String GetDumpCommand(List<MachineInfo> infos) {
        return GetDumpCommand(infos, 1);
    }

    public String GetDumpCommand(List<MachineInfo> infos, int paramDstId) {
        log.info("{}", treatmentFlags.toString());
        if (treatmentFlags.size() == 1 && treatmentFlags.get(0) == TreatmentType.OUTPUT) {
            log.info(ANSI.RED + "Generate By Protocol" + ANSI.RESET);
            return generateByProtocol(infos, paramDstId);
        }
        else {
            log.info(ANSI.RED + "Generate By List" + ANSI.RESET);
            return generateByList(infos, paramDstId);
        }
    }

    private String generateByList(List<MachineInfo> infos, int paramDstId) {
        String srcIp = getSrcIpToUse(infos),
            srcMac = infos.get(0).macAddress.toString(),
            dstIp = getDstIpToUse(infos, paramDstId),
            dstMac = infos.get(paramDstId).macAddress.toString(),
            commandStr;

        commandStr = "\"sudo -S tcpdump -n ";
        for (int k = 0; k < treatmentFlags.size(); k++) {
            log.info(treatmentFlags.get(k).toString());
            switch(treatmentFlags.get(k)) {
                case ETHSrc:
                    commandStr += "ether src ";
                    commandStr += srcMac;
                    break;
                case ETHDst:
                    commandStr += "ether dst ";
                    commandStr += dstMac;
                    break;
                case TCPSrc:
                    commandStr += "tcp and src port 7650";
                    break;
                case TCPDst:
                    commandStr += "tcp and dst port 2830";
                    break;
                case UDPSrc:
                    commandStr += "udp and src port 7650";
                    break;
                case UDPDst:
                    commandStr += "udp and dst port 2830";
                    break;
                case IPSrc:
                    commandStr += "src ";
                    commandStr += srcIp;
                    break;
                case IPDst:
                    commandStr += "dst ";
                    commandStr += dstIp;
                    break;
                case VlanPush:
                    //commandStr
                    break;
                case VlanPop:

                    break;
                case OUTPUT:
                default:
                    continue;
                    //return ""; 
            }
            commandStr += " and ";
        }
        commandStr = commandStr.substring(0, commandStr.length() - 5) + " -c 1 -i " + getDstIface(infos, paramDstId) + "\"";
        return commandStr;
    }

    private String generateByProtocol(List<MachineInfo> infos, int paramDstId) {
        String src = infos.get(0).machineIp.toString();
        switch (currentMatchFieldFlag) {
            case ARPing: {
                return "\"sudo -S tcpdump -n src " + src + " and arp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case ICMPing: {
                return "\"sudo -S tcpdump -n src " + src + " and icmp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case TCPing: {
                return "\"sudo -S tcpdump -n src " + src + " and tcp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case UDPing: {
                return "\"sudo -S tcpdump -n src " + src + " and udp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            case SCTPing: {
                return "\"sudo -S tcpdump -n src " + src + " and sctp -c 1 -i " + infos.get(paramDstId).machineIface + "\"";
            }
            default:
                return "";
        }
    }

    public String GetPingCommand(List<MachineInfo> infos) {
        return GetPingCommand(infos, 1);
    }

    public String GetPingCommand(List<MachineInfo> infos, int paramDstId) {
        String srcIp, dstIp, srcPort, dstPort, srcMac, dstMac, srcIface;
        dstPort = flagsUseDummyDstPort ? "3460" : "2830";
        // The Idolm@ster ShinyColors
        // for xxx_dst series, let scapy script send to dst 3460, and make treatment
        // rewrite dst to 2830
        // The Idolm@ster Cinderella Girls
        // otherwise, simply send to 2830

        srcPort = flagsUseDummySrcPort ? "9610" : "7650";
        // The Idolm@ster AllStars
        // for xxx_src series, let scapy script send from src 9610, and make treatment
        // rewrite src to 7650
        // The Idolm@ster MillionLive TheaterDays
        // otherwise, simply send from 7650
        srcMac = flagsUseDummySrcMac ? infos.get(infos.size()-1).macAddress.toString() : infos.get(0).macAddress.toString();
        dstMac = flagsUseDummyDstMac ? infos.get(infos.size()-1).macAddress.toString() : infos.get(paramDstId).macAddress.toString();
        
        srcIp = getSrcIpToUse(infos);
        dstIp = getDstIpToUse(infos, paramDstId);
        srcIface = getSrcIface(infos);
        switch (currentMatchFieldFlag) {
            case ARPing: {
                log.info(ANSI.YELLOW + "flagARPING, using ARPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagARPING " + dstIp + "\"";
            }
            case ICMPing: { // we don't care about ports while using icmping.
                log.info(ANSI.YELLOW + "flagICMPING, using ICMPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagPING " + concatIpStr(srcMac, dstMac, srcIp, dstIp, srcIface) + "\"";
            }
            case TCPing: {
                log.info(ANSI.YELLOW + "flagTCPING, using TCPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagTCPING " + concatPortStr(srcMac, dstMac, srcIp, dstIp, srcPort, dstPort, srcIface) + "\"";
            }
            case UDPing: {
                log.info(ANSI.YELLOW + "flagUDPING, using UDPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagUDPING " + concatPortStr(srcMac, dstMac, srcIp, dstIp, srcPort, dstPort, srcIface) + "\"";
            }
            case SCTPing: {
                log.info(ANSI.YELLOW + "flagSCTPING, using SCTPing..." + ANSI.RESET);
                return "\"sudo ./ping.py flagSCTPING " + concatPortStr(srcMac, dstMac, srcIp, dstIp, srcPort, dstPort, srcIface) + "\"";
            }
            default: {
                return "";
            }
        }
    }

    private String concatIpStr(String str1, String str2, String str3, String str4, String str5) {
        return String.format("%s %s %s %s %s", str1, str2, str3, str4, str5);
    }

    private String concatPortStr(String str1, String str2, String str3, String str4, String str5, String str6, String str7) {
        return String.format("%s %s %s %s %s %s %s", str1, str2, str3, str4, str5, str6);
    }

    private String getSrcIpToUse(List<MachineInfo> infos) {
        if (flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIpV.toString();
        }
        else if (flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIpV.toString();
        }
        else if (!flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIp.toString();
        }
        else if (!flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIp.toString();
        }
        else {
            return "";
        }
    }

    private String getDstIpToUse(List<MachineInfo> infos, int paramDstId) {
        if (flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIpV.toString();
        }
        else if (flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIpV.toString();
        }
        else if (!flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIp.toString();
        }
        else if (!flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIp.toString();
        }
        else {
            return "";
        }
    }

    private String getSrcIface(List<MachineInfo> infos) {
        if (flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIfaceV;
        }
        else if (flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIfaceV;
        }
        else if (!flagsUseVlanSrc && flagsUseDummySrcIp) {
            return infos.get(infos.size()-1).machineIface;
        }
        else if (!flagsUseVlanSrc && !flagsUseDummySrcIp) {
            return infos.get(0).machineIface;
        }
        else {
            return "";
        }
    }

    private String getDstIface(List<MachineInfo> infos, int paramDstId) {
        if (flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIfaceV;
        }
        else if (flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIfaceV;
        }
        else if (!flagsUseVlanDst && flagsUseDummyDstIp) {
            return infos.get(infos.size()-1).machineIface;
        }
        else if (!flagsUseVlanDst && !flagsUseDummyDstIp) {
            return infos.get(paramDstId).machineIface;
        }
        else {
            return "";
        }
    }
}
