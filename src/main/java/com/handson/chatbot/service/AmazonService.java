package com.handson.chatbot.service;

import okhttp3.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AmazonService {

    public static final Pattern PRODUCT_PATTERN = Pattern.compile("<span class=\"a-size-medium a-color-base a-text-normal\">([^<]+)<\\/span>.*<span class=\"a-icon-alt\">([^<]+)<\\/span>.*<span class=\"a-offscreen\">([^<]+)<\\/span>");

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();


    public String searchProducts(String keyword) throws IOException {
        return parseProductHtml(getProductHtml(keyword));
    }

    private String parseProductHtml(String html) {
        String res = "";
        Matcher matcher = PRODUCT_PATTERN.matcher(html);
        while (matcher.find()) {
            res += matcher.group(1) + " - " + matcher.group(2) + ", price:" + matcher.group(3) + "\n";
        }
        return res;
    }

    private String getProductHtml(String keyword) throws IOException {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        Request request = new Request.Builder()
                .url("https://www.amazon.com/s?i=aps&k=" + keyword + "&ref=nb_sb_noss&url=search-alias%3Daps")
                .method("GET", null)
                .addHeader("authority", "www.amazon.com")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .addHeader("accept-language", "en-US,en;q=0.9,he-IL;q=0.8,he;q=0.7")
                .addHeader("cookie", "ubid-main=134-0450108-1082361; lc-main=en_US; x-main=\"on7IA?qywAjz8ZwpDFGD32pHQhkKfJvvQgQbDIN?qThS6Y3Dqd2Af@wmLHl7l1UL\"; at-main=Atza|IwEBIC9DuVX2kXyByylstGQHxaAyVWjN368cu4pvKtKvkRY6BKfeufZjlr3UnV1bwHfhz6IWhvrAgQEUvksGO9YIhispw9WxrkECdPwveXSlFl0Kntx3WxQKnUrVSn10ug78irVhTxmAWY8ZgCPf5ZkWZFBLRjLLs1BphRpCz51MuH0eCXO2Su1BcEwag5sk9KmDGgZjkx9kkWuS2B9HGq8FSstX; sess-at-main=\"Ey13zUx0Ts8InTayWmkJLBrUZ8hjfBmXHGpr1Weqhs4=\"; sst-main=Sst1|PQF3PABiR5cfs6CADq-wMPIQCVhh1v_lvH8MOZ_6uMe9iaADsLuryaCbkbbFsuvT2k4nu52FG7F0OgdILrRnmmF2bd0K5jnHPLUBb_LyeIMB4U2BCF6S_Q6DhkRCNbs66AAzZIzyG9nWbM9cVX4if0o2slfJCKCAHBHDx_7q1vW9nUefYBFMKX5Hse0jpEoZpvmbgU69y-qD3okea_7TUbaOYHVFnmD8BwcQ5fwPcV6HTn-S5b2DOxvTQ4ycAIkFbKhjai3e7UPBmQl_2RIpz5VxXmsF9C-uF7V5kO8nCmJA6P4; i18n-prefs=USD; aws-target-visitor-id=1663965485583-355039.34_0; aws-target-data=%7B%22support%22%3A%221%22%7D; session-id-apay=258-8678640-8258130; session-id=132-8089620-7845104; session-id-time=2082787201l; sp-cdn=\"L5Z9:IL\"; aws_lang=en; AMCVS_7742037254C95E840A4C98A6%40AdobeOrg=1; s_cc=true; aws-mkto-trk=id%3A112-TZM-766%26token%3A_mch-aws.amazon.com-1695315683057-49602; AMCV_7742037254C95E840A4C98A6%40AdobeOrg=1585540135%7CMCIDTS%7C19622%7CMCMID%7C65565268523834315686387354210476423201%7CMCAID%7CNONE%7CMCOPTOUT-1695322884s%7CNONE%7CvVersion%7C4.4.0; aws-ubid-main=841-4440811-1873301; remember-account=true; aws-account-alias=995553441267; aws-userInfo=%7B%22arn%22%3A%22arn%3Aaws%3Aiam%3A%3A995553441267%3Auser%2Fliavb%22%2C%22alias%22%3A%22995553441267%22%2C%22username%22%3A%22liavb%22%2C%22keybase%22%3A%22iruRbLQOb3aAIPCEIlIr8b9WcKOCVJ%2F%2Ffd%2Bm8UG1Ies%5Cu003d%22%2C%22issuer%22%3A%22http%3A%2F%2Fsignin.aws.amazon.com%2Fsignin%22%2C%22signinType%22%3A%22PUBLIC%22%7D; aws-userInfo-signed=eyJ0eXAiOiJKV1MiLCJrZXlSZWdpb24iOiJ1cy1lYXN0LTEiLCJhbGciOiJFUzM4NCIsImtpZCI6ImViYjdjODY1LTY3NGEtNDNjZi1hYzY2LTUxNGQ1YjQxNjlhYiJ9.eyJzdWIiOiI5OTU1NTM0NDEyNjciLCJzaWduaW5UeXBlIjoiUFVCTElDIiwiaXNzIjoiaHR0cDpcL1wvc2lnbmluLmF3cy5hbWF6b24uY29tXC9zaWduaW4iLCJrZXliYXNlIjoiaXJ1UmJMUU9iM2FBSVBDRUlsSXI4YjlXY0tPQ1ZKXC9cL2ZkK204VUcxSWVzPSIsImFybiI6ImFybjphd3M6aWFtOjo5OTU1NTM0NDEyNjc6dXNlclwvbGlhdmIiLCJ1c2VybmFtZSI6ImxpYXZiIn0.7Gc8iTELxHVQdG9qBZpOORYmTnp2r1ZQLtV-zycwSP_5w6mJyP8W7-OJxCXYV213Cbd7e9fpcxGOVJeM1NVf7ic_PTwIl0Hzq6tuf3UKIr2ePSmyDorbdKN0Qw8KRsO5; regStatus=registered; noflush_awsccs_sid=e75d364aa3f210438b88f785ea5feb999e0aefc82c6c412b728712e6b78a5d65; session-token=ix/uahOMrDpTb1VSuiyexMVqwllJLgC6h0jPQLggNo0AYT7XpNGPwdcHZ3QkSviXAQGhhirHa1ZVmyeLnoIN7trzC85jUMiThZ69Ytfm7wFCn4mcDdD0TTdP1Cp3E+FUxh+GemTiLELtmG90CPeX6QUatyh2/3GDCVtM1fnu7XpJ/G6k7qWjdhXQj8KkxnhSeWaESn9F+94rNtqvyEPpyifc/VdTWSm65XMdolq/tl+4tTk4iaXUByAAP+T2D03A61ECUcRgDd4ZhMhkw4GVwakoiv19vt5hPGlUHruySW+QqJZjLyKpfRTrfmG8zdNVY0ruyoNodNHMlaJOa9OKWAleyp0QptZv74yKke6//Tv3IIWvTA/kMSgKz1Dj7Vfo; skin=noskin; csm-hit=tb:D4HD2CWJPRKNJGMA61AV+s-5QDGQXPCG792AT5J0GW7|1695743573109&t:1695743573109&adb:adblk_yes; session-token=oF3Rl827unVx6qWzwgWMln0sEZWXsMCYjf34LJNAIyXQ33g9pxpZugMsTo84lB3Ctt4qzgoiqVk3dW68vk256p7ijqJRPQ4kWCC1CGbEGfdD2hA8Tcx6FUHZI7y6M8vwyBEpvaV1Hi/vH8uVK5Yv9Oe+S428mnGrCvmIYB86ZkgJusblWtOtXreCOAwhxFK5FdIMVEITMI8nh90Uhv/l3ptonWGf7vOBnC8zh03c49V49gLuXGsnr9FQN4405909SUA1nAIZLnk+LE7pgFcZXYJIHBwFDJ4dX2kdzFziOi1+qa29Vs+zEKVAHHB2FEIRbdS+PZLHG3C/elMb6YRngnsmKMAzI1pgCNhdwtdlMN7hoUQ8Dl749sEVTpXGro4K")
                .addHeader("device-memory", "8")
                .addHeader("downlink", "10")
                .addHeader("dpr", "1")
                .addHeader("ect", "4g")
                .addHeader("referer", "https://www.amazon.com/s?k=ipad&crid=121TSRDMALGNP&sprefix=ipad%2Caps%2C205&ref=nb_sb_noss_2")
                .addHeader("rtt", "50")
                .addHeader("sec-ch-device-memory", "8")
                .addHeader("sec-ch-dpr", "1")
                .addHeader("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("sec-ch-ua-platform-version", "\"15.0.0\"")
                .addHeader("sec-ch-viewport-width", "1920")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-user", "?1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
                .addHeader("viewport-width", "1920")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
