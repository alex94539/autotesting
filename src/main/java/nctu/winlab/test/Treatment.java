package nctu.winlab.test;

import java.util.*;

import org.onlab.packet.TpPort;
import org.onlab.packet.VlanId;
import org.onosproject.core.GroupId;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.TrafficTreatment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Treatment {
    private final Logger log = LoggerFactory.getLogger(getClass());

    enum TrString {
        OUTPUT, 
        SET_TCP_SRC, SET_TCP_DST, 
        SET_UDP_SRC, SET_UDP_DST, 
        SET_SCTP_SRC, SET_SCTP_DST,
        SET_IP_SRC, SET_IP_DST, 
        SET_ARP_SHA, SET_APR_SPA,
        SET_ETH_SRC, SET_ETH_DST
    }
    public static final String OUTPUT = "OUTPUT";
    public static final String SETTCPSRC = "SetTcpSrc";
    public static final String SETTCPDST = "SetTcpDst";
    public static final String SETUDPSRC = "SetUdpSrc";
    public static final String SETUDPDST = "SetUdpDst";
    public static final String SETSCTPSRC = "SetSctpSrc";
    public static final String SETSCTPDST = "SetSctpDst";
    public static final String SETIPSRC = "SetIpSrc";
    public static final String SETIPDST = "SetIpDst";
    public static final String SETETHSRC = "SetEthSrc";
    public static final String SETETHDST = "SetEthDst";

    public static final String PUSHVLAN = "PushVlan";
    public static final String POPVLAN = "PopVlan";

    public TrafficTreatment.Builder trt = DefaultTrafficTreatment.builder();

    public void NewTreatment(String treatmentString, List<MachineInfo> machines, AutoTestFlag ATFG) {
        switch (treatmentString) {            
            case OUTPUT:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.OUTPUT);
                trt.setOutput(machines.get(1).machineHost.location().port());
                break;
            case SETTCPSRC:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.TCPSrc);
                ATFG.SetUseDummySrcPort();
                trt.setTcpSrc(TpPort.tpPort(2830));

                break;
            case SETTCPDST:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.TCPDst);
                ATFG.SetUseDummyDstPort();
                trt.setTcpDst(TpPort.tpPort(2830));

                break;
            case SETUDPSRC:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.UDPSrc);
                ATFG.SetUseDummySrcPort();
                trt.setUdpSrc(TpPort.tpPort(2830));

                break;
            case SETUDPDST:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.UDPDst);
                ATFG.SetUseDummyDstPort();
                trt.setUdpDst(TpPort.tpPort(2830));

                break;
            case SETIPSRC:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.IPSrc);
                ATFG.SetUseDummySrcIp();
                trt.setIpSrc(machines.get(0).machineIp); //set from dummy src

                break;
            case SETIPDST:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.IPDst);
                ATFG.SetUseDummyDstIp();
                trt.setIpDst(machines.get(1).machineIp); // set to correct dst

                break;
            case SETETHSRC:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.ETHSrc);
                ATFG.SetUseDummySrcMac();
                trt.setEthSrc(machines.get(0).macAddress);

                break;
            case SETETHDST:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.ETHDst);
                ATFG.SetUseDummyDstMac();
                trt.setEthDst(machines.get(1).macAddress);

                break;
            case PUSHVLAN: // no VLAN Series support u mdfk
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.VlanPush);
                ATFG.SetUseVlanDst();

                trt.setVlanId(VlanId.vlanId((short)1)).pushVlan();
                break;
            case POPVLAN:
                ATFG.SetTreatmentFlag(AutoTestFlag.TreatmentType.VlanPop);
                ATFG.SetUseVlanSrc();

                trt.popVlan();
                break;
            default:
                log.info("---------------------------");
                log.info(ANSI.RED + "NO SUCH TREATMENT: {}" + ANSI.RESET, treatmentString);
                log.info("---------------------------");
                break;
        }
    }

    public void ToGroupTable(int gid) {
        trt.group(GroupId.valueOf(gid));
    }

    public TrafficTreatment.Builder GetTreatment() {
        return trt;
    }
}
