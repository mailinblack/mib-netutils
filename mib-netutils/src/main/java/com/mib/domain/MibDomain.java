package com.mib.domain;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.xbill.DNS.*;

import java.net.UnknownHostException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public enum MibDomain {
    ;

    public static String getFirstTxtRecord(String entity) throws UnknownHostException, TextParseException {
        final Lookup lookUp = new Lookup(entity, Type.TXT);
        Record[] records = lookUp.run();
        if (records == null) {
            throw new UnknownHostException();
        }
        return ((TXTRecord) records[0]).getStrings().toString();
    }

    public static Boolean isDomainValid(String domainName) {  //DomainValidator apache
            DomainValidator domainValidator = DomainValidator.getInstance(true);
        return domainValidator.isValid(domainName);
    }

    public static boolean isValidIp4Cidr(final String cidr) {
        if (isBlank(cidr)) {
            return false;
        }
        final String[] cidrSplited = cidr.split("\\/");
        if (cidrSplited.length != 2) {
            return false;
        }

        final String cidrAddress = cidrSplited[0];
        final String cidrSize = cidrSplited[1];
        int cidrSizeNum;

        try {
            cidrSizeNum = Integer.parseInt(cidrSize);
        } catch (final Exception e) {
            return false;
        }

        return cidrSizeNum >= 0 && cidrSizeNum <= 32 && isValidIp4(cidrAddress);
    }

    public static boolean isValidIp4(final String ip) {
        if (isBlank(ip)) {
            return false;
        }

        final InetAddressValidator validator = InetAddressValidator.getInstance();
        return validator.isValidInet4Address(ip);
    }

    public static boolean isIp4InCidr(final String ip, final String cidr) {
        if (!isValidIp4(ip) || !isValidIp4Cidr(cidr)) {
            return false;
        }
        if (cidr.equals("0.0.0.0/0")) {
            return true;
        }
        final SubnetUtils subnetUtils = new SubnetUtils(cidr);
        subnetUtils.setInclusiveHostCount(true);

        return subnetUtils.getInfo().isInRange(ip);
    }

}