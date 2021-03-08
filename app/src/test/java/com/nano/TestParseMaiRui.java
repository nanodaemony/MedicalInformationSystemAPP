package com.nano;

import com.nano.share.rsa.Base64Utils;
import com.nano.share.rsa.RsaUtils;

import org.junit.Test;

import java.security.KeyPair;

import static com.nano.share.rsa.RsaUtils.encryptData;
import static com.nano.share.rsa.RsaUtils.generateRSAKeyPair;


/**
 * 测试解析迈瑞
 * @author cz
 */
public class TestParseMaiRui {


    @Test
    public void parseT8() throws Exception{
        String data = "1231908237901fwioj2iu3908i21390489023480-234980-23940-29340-29018901382908129301923jkiowqejiowjerio1902348091238i901i230-129i3-01o230-o102-390-12390-12930-19i2340-i430-99i20-9423423421347978989awfhhuiwhefru0923i492i3409-i02-34i902345890ioswer w9ri0230-r29340-4i20-ir0i0fkwowflk 234-r=20 3-=2034-= 2 2 \\n234920-3490234-o20-3490-2394020340-234\n" +
                "234i23490-2349023490-2394-02krpwkerl;wmro23i4023o4-=2lwkrpowirl;wo-=2304-2p-3492-=40-=23p4-=2o4-=23p4r[]2elr[wpelorp[woer-=203=-40o23-=4ro-=erlw[;rl['w;erl-=23o4-=2034-=203-4=02-=3402-=34']]]]";

        KeyPair keyPair = RsaUtils.generateRSAKeyPair();

        String en = RsaUtils.encryptDataLong(data, keyPair.getPublic());
        System.out.println(en);


        System.out.println(RsaUtils.decryptDataLong(en, keyPair.getPrivate()));


    }



    @Test
    public void parseWatoex65() {
        KeyPair keyPair = generateRSAKeyPair();
    }

}
