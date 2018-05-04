package com.leanderli.dev.cardlauncherpro;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.leanderli.dev.cardlauncherpro.util.AppUtils;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leanderli
 * @description
 * @date 2018/4/4 00041958
 */

public class ChineseWordsConvertToPingyinTest {

    @Test
    public void convertTest() throws PinyinException {
        String str = "重阳节我在搬砖总是做着重复的工作，即使困难重重，但知道肩上担子很重";
        String str2 = "China";
        System.out.println(PinyinHelper.convertToPinyinString(str2, "，" ,PinyinFormat.WITHOUT_TONE));

//        PinyinHelper.convertToPinyinString(str, ",", PinyinFormat.WITH_TONE_MARK); // nǐ,hǎo,shì,jiè
//        PinyinHelper.convertToPinyinString(str, ",", PinyinFormat.WITH_TONE_NUMBER); // ni3,hao3,shi4,jie4
//        PinyinHelper.convertToPinyinString(str, ",", PinyinFormat.WITHOUT_TONE); // ni,hao,shi,jie
//        PinyinHelper.getShortPinyin(str); // nhsj
    }

    @Test
    public void test() {
        String str = "中国";
        System.out.println(AppUtils.isStartWithWord(str));
//
//        Pattern pattern = Pattern.compile("[a-zA-Z]+");
//        Matcher m = pattern.matcher(str.substring(0));
//        System.out.println(m.matches());
    }
}
