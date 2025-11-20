package org.me.gcu.mcmillan_mark_s2432525.data;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class FlagManager {
    private static final Map<String, String> currencyToFlag = new HashMap<>();

    // Map all currency codes to their flag
    static {
        currencyToFlag.put("EUR", "eu");
        currencyToFlag.put("GBP", "gb");
        currencyToFlag.put("CHF", "ch");
        currencyToFlag.put("NOK", "no");
        currencyToFlag.put("SEK", "se");
        currencyToFlag.put("DKK", "dk");
        currencyToFlag.put("PLN", "pl");
        currencyToFlag.put("HUF", "hu");
        currencyToFlag.put("CZK", "cz");
        currencyToFlag.put("RON", "ro");
        currencyToFlag.put("BGN", "bg");
        currencyToFlag.put("ISK", "is");
        currencyToFlag.put("RSD", "rs");
        currencyToFlag.put("TRY", "tr");
        currencyToFlag.put("MDL", "md");
        currencyToFlag.put("UAH", "ua");
        currencyToFlag.put("GEL", "ge");
        currencyToFlag.put("BYN", "by");
        currencyToFlag.put("ALL", "al");
        currencyToFlag.put("MKD", "mk");
        currencyToFlag.put("EEK", "ee");
        currencyToFlag.put("HRK", "hr");
        currencyToFlag.put("LTL", "lt");
        currencyToFlag.put("LVL", "lv");
        currencyToFlag.put("SKK", "sk");
        currencyToFlag.put("RUB", "ru");

        currencyToFlag.put("AED", "ae");
        currencyToFlag.put("SAR", "sa");
        currencyToFlag.put("QAR", "qa");
        currencyToFlag.put("BHD", "bh");
        currencyToFlag.put("OMR", "om");
        currencyToFlag.put("KWD", "kw");
        currencyToFlag.put("ILS", "il");
        currencyToFlag.put("JOD", "jo");
        currencyToFlag.put("LBP", "lb");
        currencyToFlag.put("IQD", "iq");
        currencyToFlag.put("IRR", "ir");
        currencyToFlag.put("SYP", "sy");
        currencyToFlag.put("YER", "ye");
        currencyToFlag.put("ZAR", "za");
        currencyToFlag.put("EGP", "eg");
        currencyToFlag.put("MAD", "ma");
        currencyToFlag.put("NGN", "ng");
        currencyToFlag.put("KES", "ke");
        currencyToFlag.put("TZS", "tz");
        currencyToFlag.put("UGX", "ug");
        currencyToFlag.put("ETB", "et");
        currencyToFlag.put("DZD", "dz");
        currencyToFlag.put("LYD", "ly");
        currencyToFlag.put("SDG", "sd");
        currencyToFlag.put("MUR", "mu");
        currencyToFlag.put("NAD", "na");
        currencyToFlag.put("BIF", "bi");
        currencyToFlag.put("CVE", "cv");
        currencyToFlag.put("KMF", "km");
        currencyToFlag.put("DJF", "dj");
        currencyToFlag.put("GMD", "gm");
        currencyToFlag.put("GNF", "gn");
        currencyToFlag.put("LRD", "lr");
        currencyToFlag.put("MRO", "mr");
        currencyToFlag.put("RWF", "rw");
        currencyToFlag.put("SOS", "so");
        currencyToFlag.put("MZN", "mz");
        currencyToFlag.put("ZMK", "zm");
        currencyToFlag.put("ZMW", "zm");
        currencyToFlag.put("ZWD", "zw");

        currencyToFlag.put("XAF", "cm");
        currencyToFlag.put("XOF", "sn");
        currencyToFlag.put("XPF", "pf");

        currencyToFlag.put("JPY", "jp");
        currencyToFlag.put("CNY", "cn");
        currencyToFlag.put("INR", "in");
        currencyToFlag.put("KRW", "kr");
        currencyToFlag.put("HKD", "hk");
        currencyToFlag.put("TWD", "tw");
        currencyToFlag.put("SGD", "sg");
        currencyToFlag.put("THB", "th");
        currencyToFlag.put("PKR", "pk");
        currencyToFlag.put("LKR", "lk");
        currencyToFlag.put("MYR", "my");
        currencyToFlag.put("PHP", "ph");
        currencyToFlag.put("IDR", "id");
        currencyToFlag.put("BDT", "bd");
        currencyToFlag.put("VND", "vn");
        currencyToFlag.put("MMK", "mm");
        currencyToFlag.put("KZT", "kz");
        currencyToFlag.put("UZS", "uz");
        currencyToFlag.put("AZN", "az");
        currencyToFlag.put("AFN", "af");
        currencyToFlag.put("AOA", "ao");
        currencyToFlag.put("AMD", "am");
        currencyToFlag.put("KGS", "kg");
        currencyToFlag.put("KHR", "kh");
        currencyToFlag.put("LAK", "la");
        currencyToFlag.put("NPR", "np");
        currencyToFlag.put("KPW", "kp");
        currencyToFlag.put("TJS", "tj");
        currencyToFlag.put("TMT", "tm");

        currencyToFlag.put("BAM", "ba");

        currencyToFlag.put("USD", "us");
        currencyToFlag.put("CAD", "ca");
        currencyToFlag.put("MXN", "mx");
        currencyToFlag.put("HTG", "ht");
        currencyToFlag.put("BBD", "bb");
        currencyToFlag.put("BZD", "bz");
        currencyToFlag.put("JMD", "jm");
        currencyToFlag.put("GTQ", "gt");
        currencyToFlag.put("HNL", "hn");
        currencyToFlag.put("NIO", "ni");
        currencyToFlag.put("CRC", "cr");
        currencyToFlag.put("PAB", "pa");
        currencyToFlag.put("SVC", "sv");

        currencyToFlag.put("ARS", "ar");
        currencyToFlag.put("BRL", "br");
        currencyToFlag.put("CLP", "cl");
        currencyToFlag.put("COP", "co");
        currencyToFlag.put("PEN", "pe");
        currencyToFlag.put("UYU", "uy");
        currencyToFlag.put("PYG", "py");
        currencyToFlag.put("VEF", "ve");
        currencyToFlag.put("VES", "ve");
        currencyToFlag.put("BOB", "bo");
        currencyToFlag.put("GYD", "gy");
        currencyToFlag.put("SRD", "sr");

        currencyToFlag.put("AUD", "au");
        currencyToFlag.put("NZD", "nz");
        currencyToFlag.put("FJD", "fj");
        currencyToFlag.put("PGK", "pg");
        currencyToFlag.put("SBD", "sb");
        currencyToFlag.put("WST", "ws");
        currencyToFlag.put("TOP", "to");
        currencyToFlag.put("VUV", "vu");

        currencyToFlag.put("BND", "bn");
        currencyToFlag.put("BWP", "bw");
        currencyToFlag.put("ANG", "cw");
        currencyToFlag.put("BMD", "bm");
        currencyToFlag.put("BSD", "bs");
        currencyToFlag.put("KID", "ki");
        currencyToFlag.put("XCD", "ag");
        currencyToFlag.put("MVR", "mv");
        currencyToFlag.put("SCR", "sc");
        currencyToFlag.put("MOP", "mo");
        currencyToFlag.put("AWG", "aw");
        currencyToFlag.put("FKP", "fk");
        currencyToFlag.put("SHP", "sh");
        currencyToFlag.put("STD", "st");

        currencyToFlag.put("CDF", "cd");
        currencyToFlag.put("ERN", "er");
        currencyToFlag.put("MGA", "mg");
        currencyToFlag.put("MWK", "mw");
        currencyToFlag.put("GHS", "gh");

        currencyToFlag.put("LSL", "ls");
        currencyToFlag.put("SZL", "sz");

        currencyToFlag.put("BTN", "bt");

        currencyToFlag.put("BTC", "btc");

        currencyToFlag.put("DOP", "dom");
        currencyToFlag.put("BYR", "by");
        currencyToFlag.put("KYD", "ky");
        currencyToFlag.put("SLL", "sl");
        currencyToFlag.put("TND", "tn");
        currencyToFlag.put("TTD", "tt");
        currencyToFlag.put("CUP", "cu");
        currencyToFlag.put("MNT", "mn");
    }

    // Returns the drawable resource ID for the given currency
    public static int getFlagResId(Context context, String currencyCode) {
        if (currencyCode == null) return 0;

        String alpha2 = currencyToFlag.get(currencyCode.toUpperCase());

        if (alpha2 == null) {
            Log.w("FlagManager", "No flag mapping for currency " + currencyCode);
            return 0;
        }

        int id = context.getResources().getIdentifier(
                alpha2,
                "drawable",
                context.getPackageName()
        );

        if (id == 0) {
            Log.w("FlagManager", "Drawable not found for code " + alpha2);
        }

        return id;
    }
}
